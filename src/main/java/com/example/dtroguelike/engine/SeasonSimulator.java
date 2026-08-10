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
 * simulando partidos individuales contra rivales elegidos al azar.
 */
@SuppressWarnings("unused")
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
     * Solo se pueden simular partidos durante REGULAR_SEASON.
     *
     * TODO: reemplazar por un fixture real de liga (ida y vuelta contra
     * todos los equipos) en una iteracion futura.
     */
}
