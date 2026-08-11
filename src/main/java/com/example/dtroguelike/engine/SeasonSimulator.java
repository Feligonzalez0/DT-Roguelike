package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchResult;
import com.example.dtroguelike.domain.match.MatchState;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.domain.season.SeasonPhase;

import java.util.Comparator;
import java.util.Optional;
import java.util.Random;

/**
 * Simulador simplificado de temporada. Para este MVP no arma una
 * simulacion masiva de toda la liga de una vez; permite avanzar la
 * temporada del club dirigido simulando, de a uno, los partidos que ya
 * genero {@link FixtureGenerator} para esa temporada.
 */
public class SeasonSimulator {

    private final MatchSimulator matchSimulator;
    private final Random random;

    public SeasonSimulator(MatchSimulator matchSimulator) {
        this(matchSimulator, new Random());
    }

    public SeasonSimulator(MatchSimulator matchSimulator, Random random) {
        this.matchSimulator = matchSimulator;
        this.random = random;
    }

    /**
     * Simula el proximo partido pendiente ({@link MatchState#NOT_STARTED})
     * del club dirigido por el DT dentro de la temporada actual: calcula
     * el resultado, lo asocia al {@link Match} (que queda en
     * {@code FINISHED}) y actualiza las estadisticas de la temporada
     * ({@link Season#getStats()}) desde la perspectiva del club dirigido.
     *
     * Solo se pueden simular partidos durante {@link SeasonPhase#REGULAR_SEASON}.
     *
     * @return el {@link Match} recien simulado (ya en estado FINISHED).
     * @throws IllegalStateException si no hay carrera/temporada/club
     *         activos, si la temporada no esta en REGULAR_SEASON, o si no
     *         quedan partidos pendientes para el club dirigido.
     *
     * TODO: en una iteracion futura, extender esto (o SeasonService) para
     * poder simular una fecha/temporada completa de una sola vez y armar
     * la tabla de posiciones de la liga.
     */
    public Match simulateNextMatch(Career career) {
        Season season = requireRegularSeason(career);
        Club managedClub = requireManagedClub(career);

        Match nextMatch = findNextPendingMatch(season, managedClub)
                .orElseThrow(() -> new IllegalStateException(
                        "No hay mas partidos pendientes para simular en esta temporada."));

        MatchResult result = matchSimulator.simulate(nextMatch, career);
        nextMatch.setResult(result); // deja el Match en MatchState.FINISHED

        registerResultInSeasonStats(season, managedClub, nextMatch, result);

        return nextMatch;
    }

    private Optional<Match> findNextPendingMatch(Season season, Club managedClub) {
        String clubId = managedClub.getId();
        return season.getMatches().stream()
                .filter(match -> match.getState() == MatchState.NOT_STARTED)
                .filter(match -> match.getHomeTeam().getId().equals(clubId)
                        || match.getAwayTeam().getId().equals(clubId))
                .min(Comparator.comparingInt(Match::getRound));
    }

    private void registerResultInSeasonStats(Season season, Club managedClub, Match match, MatchResult result) {
        boolean managedClubIsHome = match.getHomeTeam().getId().equals(managedClub.getId());
        int goalsFor = managedClubIsHome ? result.getHomeGoals() : result.getAwayGoals();
        int goalsAgainst = managedClubIsHome ? result.getAwayGoals() : result.getHomeGoals();
        season.getStats().registerMatch(goalsFor, goalsAgainst);
    }

    private Season requireRegularSeason(Career career) {
        if (career == null || career.getCurrentSeason() == null) {
            throw new IllegalStateException("No hay una temporada activa.");
        }
        Season season = career.getCurrentSeason();
        if (season.getPhase() != SeasonPhase.REGULAR_SEASON) {
            throw new IllegalStateException(
                    "Solo se pueden simular partidos durante REGULAR_SEASON (fase actual: "
                            + season.getPhase() + ").");
        }
        return season;
    }

    private Club requireManagedClub(Career career) {
        Club club = career.getCurrentClub();
        if (club == null) {
            throw new IllegalStateException("No hay un club dirigido actualmente.");
        }
        return club;
    }
}
