package com.example.dtroguelike.web.controllers;

import com.example.dtroguelike.application.CareerService;
import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.web.viewmodels.FixtureViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FixtureController {

    private final CareerService careerService;

    public FixtureController(CareerService careerService) {
        this.careerService = careerService;
    }

    public Map<String, Object> showFixture() {

        Optional<Career> careerOptional = careerService.getCurrentCareer();

        if (careerOptional.isEmpty()) {
            return new HashMap<>();
        }

        Career career = careerOptional.get();

        Season season = career.getCurrentSeason();

        if (season == null || career.getCurrentClub() == null) {
            return new HashMap<>();
        }

        FixtureViewModel viewModel = new FixtureViewModel(season, career.getCurrentClub());

        Map<String, Object> model = new HashMap<>();
        model.put("fixture", viewModel);

        return model;
    }
}