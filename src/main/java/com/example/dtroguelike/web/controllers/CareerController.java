package com.example.dtroguelike.web.controllers;

import com.example.dtroguelike.application.CareerService;
import com.example.dtroguelike.application.SeasonService;
import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.web.viewmodels.DashboardViewModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador del dashboard de la carrera: muestra el estado actual y
 * permite avanzar la temporada (simular el siguiente partido).
 */
public class CareerController {

    private final CareerService careerService;
    private final SeasonService seasonService;

    public CareerController(CareerService careerService, SeasonService seasonService) {
        this.careerService = careerService;
        this.seasonService = seasonService;
    }

    public Map<String, Object> showDashboard() {
        Career career = requireCurrentCareer();
        DashboardViewModel viewModel = new DashboardViewModel(career);
        return Map.of("dashboard", viewModel);
    }

    public Career finishSeason() {
        Career career = requireCurrentCareer();
        seasonService.finishSeason(career);
        return career;
    }

    public Career advancePhase() {
        Career career = requireCurrentCareer();

        switch (career.getCurrentSeason().getPhase()) {
            case PRESEASON ->
                    seasonService.startTransferWindow(career);
            case TRANSFER_WINDOW ->
                    seasonService.startRegularSeason(career);
            case END_OF_SEASON ->
                    seasonService.showSeasonSummary(career);
            default ->
                    throw new IllegalStateException(
                            "No se puede avanzar automáticamente desde la fase "
                                    + career.getCurrentSeason().getPhase());
        }
        return career;
    }    

    /** Simula el proximo partido pendiente del club dirigido (solo durante REGULAR_SEASON). */
    public List<Match> simulateNextMatch() {
        Career career = requireCurrentCareer();
        return seasonService.simulateNextMatch(career);
    }

    private Career requireCurrentCareer() {
        Optional<Career> career = careerService.getCurrentCareer();
        return career.orElseThrow(() -> new IllegalStateException("No hay una carrera activa."));
    }

    public Career startNextSeason() {
        Career career = requireCurrentCareer();
        seasonService.startNextSeason(career);
        return career;
    }
}
