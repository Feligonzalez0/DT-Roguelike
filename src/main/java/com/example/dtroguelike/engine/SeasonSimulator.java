package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchCompetition;
import com.example.dtroguelike.domain.match.MatchImportance;
import com.example.dtroguelike.domain.match.MatchResult;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.domain.season.SeasonPhase;

import java.util.List;
import java.util.Random;

/**
 * Simulador simplificado de temporada. Para el MVP no construye un
 * fixture completo de liga; simplemente permite avanzar la temporada
 * simulando partidos individuales contra rivales elegidos al azar de
 * la misma liga.
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
     * Simula el siguiente partido de la temporada actual del DT contra un
     * rival elegido al azar de la lista provista, y actualiza las
     * estadisticas de la temporada.
     *
     * TODO: reemplazar por un fixture real de liga (ida y vuelta contra
     * todos los equipos) en una iteracion futura.
     */
    public MatchResult simulateNextStep(Career career, List<Club> possibleRivals) {
        Season season = career.getCurrentSeason();
        Club home = career.getCurrentClub();
        if (season == null || home == null || possibleRivals == null || possibleRivals.isEmpty()) {
            return null;
        }

        Club rival = possibleRivals.get(random.nextInt(possibleRivals.size()));
        Match match = new Match(home, rival, MatchCompetition.LEAGUE, MatchImportance.NORMAL);
        MatchResult result = matchSimulator.simulate(match, career);
        match.setResult(result);
        season.addMatch(match);
        season.getStats().registerMatch(result.getHomeGoals(), result.getAwayGoals());

        if (season.getPhase() == SeasonPhase.PRESEASON) {
            season.setPhase(SeasonPhase.REGULAR_SEASON);
        }

        return result;
    }

    /** Cierra la temporada actual, avanzando su fase a END_OF_SEASON. */
    public void finishSeason(Season season) {
        if (season != null) {
            season.setPhase(SeasonPhase.END_OF_SEASON);
        }
    }
}
