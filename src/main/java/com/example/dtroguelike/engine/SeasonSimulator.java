package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchResult;
import com.example.dtroguelike.domain.match.MatchState;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.domain.season.SeasonPhase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Simulador de temporada, basado exclusivamente en el fixture real de
 * la {@link Season} ({@code season.getFixture()} agrupado por fecha).
 *
 * La temporada regular se juega en dos mitades:
 *
 * - {@link #simulateFirstHalf(Career)} simula las primeras fechas del
 *   fixture (aproximadamente la mitad) al entrar a REGULAR_SEASON.
 * - {@link #simulateSecondHalf(Career)} simula el resto de las fechas
 *   cuando se finaliza la temporada.
 *
 * Ambos metodos son idempotentes respecto de partidos ya jugados: un
 * {@link Match} en estado {@link MatchState#FINISHED} nunca vuelve a
 * simularse, evitando duplicar puntos, goles o partidos jugados.
 *
 * {@link #simulateNextMatch(Career)} se mantiene para el boton de debug
 * que permite simular fecha por fecha; internamente reutiliza el mismo
 * mecanismo de simulacion que las mitades.
 */
public class SeasonSimulator {

    private final MatchSimulator matchSimulator;
    private final Random random;

    public SeasonSimulator(MatchSimulator matchSimulator) {
        this(matchSimulator, new Random());
    }

    public SeasonSimulator(
            MatchSimulator matchSimulator,
            Random random) {

        this.matchSimulator = matchSimulator;
        this.random = random;
    }

    /**
     * Simula la primera mitad del fixture de la temporada regular.
     *
     * La temporada se divide utilizando la cantidad de fechas del
     * fixture ({@code totalRounds / 2}): las primeras fechas
     * corresponden a la primera mitad y el resto a la segunda.
     *
     * Los partidos ya {@link MatchState#FINISHED} se ignoran, por lo
     * que puede llamarse de forma segura mas de una vez.
     *
     * @return los partidos efectivamente simulados en esta llamada.
     */
    public List<Match> simulateFirstHalf(Career career) {

        Season season = requireRegularSeason(career);
        Club managedClub = requireManagedClub(career);

        int totalRounds = season.getFixture().size();
        int midpoint = firstHalfMidpoint(totalRounds);

        return simulateRoundRange(career, season, managedClub, 0, midpoint);
    }

    /**
     * Simula la segunda mitad del fixture de la temporada regular
     * (todas las fechas restantes desde el punto medio).
     *
     * Igual que {@link #simulateFirstHalf(Career)}, es idempotente
     * respecto de partidos ya jugados.
     *
     * @return los partidos efectivamente simulados en esta llamada.
     */
    public List<Match> simulateSecondHalf(Career career) {

        Season season = requireRegularSeason(career);
        Club managedClub = requireManagedClub(career);

        int totalRounds = season.getFixture().size();
        int midpoint = firstHalfMidpoint(totalRounds);

        return simulateRoundRange(career, season, managedClub, midpoint, totalRounds);
    }

    /**
     * Calcula el punto medio de la temporada en base a la cantidad
     * total de fechas. La unidad logica de division es la fecha
     * (no la cantidad total de partidos), para no separar partidos
     * de una misma fecha entre las dos mitades.
     */
    private int firstHalfMidpoint(int totalRounds) {
        return totalRounds / 2;
    }

    /**
     * Simula la proxima fecha pendiente de la temporada.
     *
     * Se mantiene para el boton de debug ({@code /career/season/advance}
     * y {@code /career/match/simulate}), que permite probar la
     * simulacion fecha por fecha. Reutiliza el mismo mecanismo que
     * {@link #simulateFirstHalf(Career)} y {@link #simulateSecondHalf(Career)},
     * por lo que no introduce una tercera forma de simular partidos.
     *
     * @return lista de partidos simulados de la fecha.
     *
     * @throws IllegalStateException si no hay carrera, temporada
     * o club activos, si la temporada no está en REGULAR_SEASON,
     * o si no quedan fechas pendientes.
     */
    public List<Match> simulateNextMatch(Career career) {

        Season season = requireRegularSeason(career);
        Club managedClub = requireManagedClub(career);

        int nextRoundIndex = findNextPendingRoundIndex(season);

        if (nextRoundIndex == -1) {
            throw new IllegalStateException(
                    "No hay más fechas pendientes para simular en esta temporada."
            );
        }

        return simulateRoundRange(
                career, season, managedClub, nextRoundIndex, nextRoundIndex + 1
        );
    }

    /**
     * Simula todas las fechas del fixture entre {@code fromIndex}
     * (inclusive) y {@code toIndex} (exclusive), saltando los partidos
     * que ya estan {@link MatchState#FINISHED}.
     *
     * Actualiza, para cada partido nuevo simulado: el resultado del
     * {@link Match}, la tabla de posiciones de la temporada y, si el
     * partido corresponde al club dirigido, las {@code SeasonStats} de
     * la temporada (incluyendo la posicion en la tabla).
     */
    private List<Match> simulateRoundRange(
            Career career,
            Season season,
            Club managedClub,
            int fromIndex,
            int toIndex) {

        List<Match> simulated = new ArrayList<>();
        List<List<Match>> fixture = season.getFixture();

        for (int i = fromIndex; i < toIndex; i++) {

            List<Match> round = fixture.get(i);

            for (Match match : round) {

                if (match.getState() != MatchState.NOT_STARTED) {
                    // Partido ya jugado: nunca se vuelve a simular.
                    continue;
                }

                MatchResult result = matchSimulator.simulate(match, career);

                /*
                 * El resultado deja automáticamente el Match en
                 * FINISHED.
                 */
                match.setResult(result);

                season.getStandings().registerMatch(
                        match.getHomeTeam(), match.getAwayTeam(),
                        result.getHomeGoals(), result.getAwayGoals()
                );

                if (isManagedClubMatch(match, managedClub)) {
                    registerResultInSeasonStats(
                            season, managedClub, match, result
                    );
                }

                simulated.add(match);
            }
        }

        syncCurrentRound(season);
        syncManagedClubLeaguePosition(season, managedClub);

        return simulated;
    }

    /**
     * Recalcula {@code Season.currentRound} en base a la cantidad de
     * fechas totalmente finalizadas al comienzo del fixture. El fixture
     * se juega en orden, por lo que la primera fecha con partidos
     * pendientes marca el limite del progreso actual.
     */
    private void syncCurrentRound(Season season) {

        int finishedRounds = 0;

        for (List<Match> round : season.getFixture()) {

            boolean allFinished = round.stream()
                    .allMatch(match -> match.getState() == MatchState.FINISHED);

            if (!allFinished) {
                break;
            }

            finishedRounds++;
        }

        season.setCurrentRound(finishedRounds);
    }

    /**
     * Actualiza SeasonStats.leaguePosition con la posicion actual del
     * club dirigido en la tabla de posiciones.
     */
    private void syncManagedClubLeaguePosition(Season season, Club managedClub) {

        season.getStats().setLeaguePosition(
                season.getStandings()
                        .getEntry(managedClub.getId())
                        .getPosition()
        );
    }

    /**
     * Busca el indice (dentro del fixture) de la primera fecha que
     * todavia contiene al menos un partido NOT_STARTED.
     */
    private int findNextPendingRoundIndex(Season season) {

        List<List<Match>> fixture = season.getFixture();

        for (int i = 0; i < fixture.size(); i++) {

            boolean hasPendingMatch = fixture.get(i).stream()
                    .anyMatch(match -> match.getState() == MatchState.NOT_STARTED);

            if (hasPendingMatch) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Determina si el partido pertenece al club dirigido.
     */
    private boolean isManagedClubMatch(
            Match match,
            Club managedClub) {

        String clubId = managedClub.getId();

        return match.getHomeTeam()
                .getId()
                .equals(clubId)
                ||
                match.getAwayTeam()
                        .getId()
                        .equals(clubId);
    }

    /**
     * Registra el resultado desde la perspectiva del club dirigido.
     */
    private void registerResultInSeasonStats(
            Season season,
            Club managedClub,
            Match match,
            MatchResult result) {

        boolean managedClubIsHome =
                match.getHomeTeam()
                        .getId()
                        .equals(managedClub.getId());

        int goalsFor =
                managedClubIsHome
                        ? result.getHomeGoals()
                        : result.getAwayGoals();

        int goalsAgainst =
                managedClubIsHome
                        ? result.getAwayGoals()
                        : result.getHomeGoals();

        season.getStats().registerMatch(
                goalsFor,
                goalsAgainst
        );
    }

    /**
     * Verifica que exista una temporada y que esté en
     * REGULAR_SEASON.
     */
    private Season requireRegularSeason(
            Career career) {

        if (career == null
                || career.getCurrentSeason() == null) {

            throw new IllegalStateException(
                    "No hay una temporada activa."
            );
        }

        Season season =
                career.getCurrentSeason();

        if (season.getPhase()
                != SeasonPhase.REGULAR_SEASON) {

            throw new IllegalStateException(
                    "Solo se pueden simular partidos durante "
                    + "REGULAR_SEASON (fase actual: "
                    + season.getPhase()
                    + ")."
            );
        }

        return season;
    }

    /**
     * Obtiene el club dirigido actualmente.
     */
    private Club requireManagedClub(
            Career career) {

        Club club =
                career.getCurrentClub();

        if (club == null) {

            throw new IllegalStateException(
                    "No hay un club dirigido actualmente."
            );
        }

        return club;
    }
}
