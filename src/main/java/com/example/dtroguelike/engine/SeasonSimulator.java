package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchResult;
import com.example.dtroguelike.domain.match.MatchState;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.domain.season.SeasonPhase;

import java.util.List;
import java.util.Random;

/**
 * Simulador simplificado de temporada.
 *
 * Para este MVP permite avanzar la temporada fecha por fecha.
 *
 * Al simular una fecha se simulan TODOS los partidos de esa fecha,
 * no solamente el partido del club dirigido.
 *
 * Esto permite preparar el sistema para implementar posteriormente
 * la tabla de posiciones de la liga.
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
     * Simula la próxima fecha pendiente de la temporada.
     *
     * La fecha se obtiene del fixture agrupado:
     *
     * List<List<Match>>
     *
     * donde cada elemento representa una fecha.
     *
     * Todos los partidos de la fecha son simulados, incluyendo
     * aquellos en los que no participa el club dirigido.
     *
     * Las estadísticas de SeasonStats se actualizan solamente
     * desde la perspectiva del club dirigido.
     *
     * En el futuro, la actualización de la tabla de posiciones
     * utilizará todos los resultados de la fecha.
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

        /*
         * Obtiene la primera fecha que todavía tiene
         * partidos sin jugar.
         */
        List<Match> nextRound =
                findNextPendingRound(season);

        if (nextRound == null) {
            throw new IllegalStateException(
                    "No hay más fechas pendientes para simular en esta temporada."
            );
        }

        /*
         * Simulamos TODOS los partidos de la fecha.
         */
        for (Match match : nextRound) {

            if (match.getState() != MatchState.NOT_STARTED) {
                continue;
            }

            MatchResult result =
                    matchSimulator.simulate(
                            match,
                            career
                    );

            /*
             * El resultado deja automáticamente el Match
             * en FINISHED.
             */
            match.setResult(result);

            /*
             * Por ahora solamente actualizamos las estadísticas
             * de la temporada del club dirigido.
             *
             * La futura tabla de posiciones deberá procesar
             * todos los partidos de la fecha.
             */
            if (isManagedClubMatch(match, managedClub)) {

                registerResultInSeasonStats(
                        season,
                        managedClub,
                        match,
                        result
                );
            }
        }

        /*
         * Una fecha completa fue simulada.
         */
        season.incrementCurrentRound();

        return nextRound;
    }

    /**
     * Busca la primera fecha del fixture que todavía contiene
     * al menos un partido NOT_STARTED.
     */
    private List<Match> findNextPendingRound(Season season) {

        List<List<Match>> fixture =
                season.getFixture();

        for (List<Match> round : fixture) {

            boolean hasPendingMatch =
                    round.stream()
                            .anyMatch(match ->
                                    match.getState()
                                            == MatchState.NOT_STARTED
                            );

            if (hasPendingMatch) {
                return round;
            }
        }

        return null;
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