package com.example.dtroguelike.web.controllers;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.application.CareerService;
import com.example.dtroguelike.web.viewmodels.StandingsViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StandingsController {

    private final CareerService careerService;

    public StandingsController(CareerService careerService) {
        this.careerService = careerService;
    }

    public Map<String, Object> showStandings() {

        Optional<Career> careerOptional =
                careerService.getCurrentCareer();

        if (careerOptional.isEmpty()) {
            return new HashMap<>();
        }

        Career career = careerOptional.get();

        Season season = career.getCurrentSeason();

        if (season == null || season.getStandings() == null) {
            return new HashMap<>();
        }

        StandingsViewModel viewModel =
                new StandingsViewModel(season);

        Map<String, Object> model =
                new HashMap<>();

        model.put("standings", viewModel);

        return model;
    }
}