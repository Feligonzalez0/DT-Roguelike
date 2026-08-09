package com.example.dtroguelike.application;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.match.MatchResult;
import com.example.dtroguelike.engine.CareerEngine;
import com.example.dtroguelike.engine.SeasonSimulator;
import com.example.dtroguelike.infrastructure.repository.CareerRepository;

import java.util.List;

/**
 * Casos de uso relacionados con el ciclo de vida de una temporada.
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

    /** PRESEASON -> TRANSFER_WINDOW. */
    public void startTransferWindow(Career career) {
        careerEngine.startTransferWindow(career);
        careerRepository.save(career);
    }

    /** TRANSFER_WINDOW -> REGULAR_SEASON. */
    public void startRegularSeason(Career career) {
        careerEngine.startRegularSeason(career);
        careerRepository.save(career);
    }

    /** REGULAR_SEASON -> END_OF_SEASON. */
    public void finishSeason(Career career) {
        careerEngine.finishSeason(career);
        careerRepository.save(career);
    }

    /** END_OF_SEASON -> SUMMARY. */
    public void showSeasonSummary(Career career) {
        careerEngine.showSeasonSummary(career);
        careerRepository.save(career);
    }

    /** SUMMARY -> nueva temporada en PRESEASON. */
    public void startNextSeason(Career career) {
        careerEngine.startNextSeason(career);
        careerRepository.save(career);
    }
}
