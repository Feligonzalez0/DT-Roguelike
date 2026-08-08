package com.example.dtroguelike.application;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.match.MatchResult;
import com.example.dtroguelike.engine.CareerEngine;
import com.example.dtroguelike.engine.SeasonSimulator;
import com.example.dtroguelike.infrastructure.repository.CareerRepository;

import java.util.List;

/**
 * Caso de uso: avanzar la temporada actual (simular partidos, cerrar
 * la temporada).
 */
public class SeasonService {

    private final SeasonSimulator seasonSimulator;
    private final CareerEngine careerEngine;
    private final CareerRepository careerRepository;
    private final List<Club> allClubs;

    public SeasonService(SeasonSimulator seasonSimulator, CareerEngine careerEngine,
                          CareerRepository careerRepository, List<Club> allClubs) {
        this.seasonSimulator = seasonSimulator;
        this.careerEngine = careerEngine;
        this.careerRepository = careerRepository;
        this.allClubs = allClubs;
    }

    /** Simula el proximo partido de la temporada activa. */
    public MatchResult simulateNextMatch(Career career) {
        List<Club> rivals = allClubs.stream()
                .filter(c -> career.getCurrentClub() == null || !c.getId().equals(career.getCurrentClub().getId()))
                .toList();
        MatchResult result = seasonSimulator.simulateNextStep(career, rivals);
        careerRepository.save(career);
        return result;
    }

    /** Cierra la temporada actual. */
    public void finishSeason(Career career) {
        careerEngine.finishSeason(career);
        careerRepository.save(career);
    }
}
