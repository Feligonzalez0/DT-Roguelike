package com.example.dtroguelike.web.controllers;

import com.example.dtroguelike.application.CareerService;
import com.example.dtroguelike.application.SeasonService;
import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.match.MatchResult;
import com.example.dtroguelike.web.viewmodels.DashboardViewModel;

import java.util.Map;
import java.util.Optional;

/**
 * Controlador del dashboard de la carrera: muestra el estado actual y
 * permite avanzar la temporada (simular el siguiente partido).
 */
public class CareerController {

    private final CareerService careerService;
    private final SeasonService seasonService;
    private String lastMatchMessage;

    public CareerController(CareerService careerService, SeasonService seasonService) {
        this.careerService = careerService;
        this.seasonService = seasonService;
    }

    public Map<String, Object> showDashboard() {
        Career career = requireCurrentCareer();
        DashboardViewModel viewModel = new DashboardViewModel(career, lastMatchMessage);
        return Map.of("dashboard", viewModel);
    }

    public Career simulateNextMatch() {
        Career career = requireCurrentCareer();
        MatchResult result = seasonService.simulateNextMatch(career);
        if (result != null) {
            lastMatchMessage = "Resultado: " + career.getCurrentClub().getName()
                    + " " + result.scoreLine();
        } else {
            lastMatchMessage = "No fue posible simular un partido en este momento.";
        }
        return career;
    }

    public Career finishSeason() {
        Career career = requireCurrentCareer();
        seasonService.finishSeason(career);
        lastMatchMessage = null;
        return career;
    }

    private Career requireCurrentCareer() {
        Optional<Career> career = careerService.getCurrentCareer();
        return career.orElseThrow(() -> new IllegalStateException("No hay una carrera activa."));
    }
}
