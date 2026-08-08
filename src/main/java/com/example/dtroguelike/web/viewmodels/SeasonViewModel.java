package com.example.dtroguelike.web.viewmodels;

import com.example.dtroguelike.domain.season.Season;

/**
 * Representacion plana de la temporada actual para el dashboard.
 */
public class SeasonViewModel {

    public final int year;
    public final String phase;
    public final int matchesPlayed;
    public final int wins;
    public final int draws;
    public final int losses;
    public final int goalsFor;
    public final int goalsAgainst;
    public final int points;

    public SeasonViewModel(Season season) {
        this.year = season.getYear();
        this.phase = season.getPhase().name();
        this.matchesPlayed = season.getStats().getMatchesPlayed();
        this.wins = season.getStats().getWins();
        this.draws = season.getStats().getDraws();
        this.losses = season.getStats().getLosses();
        this.goalsFor = season.getStats().getGoalsFor();
        this.goalsAgainst = season.getStats().getGoalsAgainst();
        this.points = season.getStats().getPoints();
    }
}
