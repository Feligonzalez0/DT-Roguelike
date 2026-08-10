package com.example.dtroguelike.web.viewmodels;

import java.util.List;

public class FixtureRoundViewModel {

    public final int round;
    public final List<FixtureMatchViewModel> matches;

    public FixtureRoundViewModel(int round, List<FixtureMatchViewModel> matches) {
        this.round = round;
        this.matches = matches;
    }
}