package com.example.dtroguelike.domain.match;

/**
 * Resultado numerico y semantico de un partido ya simulado.
 */
public class MatchResult {

    private final int homeGoals;
    private final int awayGoals;
    private final MatchOutcome outcome;

    public MatchResult(int homeGoals, int awayGoals) {
        this.homeGoals = homeGoals;
        this.awayGoals = awayGoals;
        if (homeGoals > awayGoals) {
            this.outcome = MatchOutcome.HOME_WIN;
        } else if (homeGoals < awayGoals) {
            this.outcome = MatchOutcome.AWAY_WIN;
        } else {
            this.outcome = MatchOutcome.DRAW;
        }
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public MatchOutcome getOutcome() {
        return outcome;
    }

    public String scoreLine() {
        return homeGoals + " - " + awayGoals;
    }
}
