package com.example.dtroguelike.web.viewmodels;

import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchState;

public class FixtureMatchViewModel {

    public final int round;
    public final String opponent;
    public final String homeAway;
    public final String result;

    /*
     * Clase CSS utilizada para pintar el partido.
     *
     * fixture-match-win
     * fixture-match-draw
     * fixture-match-loss
     * fixture-match-pending
     */
    public final String resultClass;

    public final boolean played;

    public FixtureMatchViewModel(Match match, String managedClubId){
        this.round = match.getRound();

        boolean managedClubIsHome =match.getHomeTeam().getId().equals(managedClubId);

        if (managedClubIsHome) {
            this.opponent = match.getAwayTeam().getName();
            this.homeAway = "LOCAL";
        } else {
            this.opponent = match.getHomeTeam().getName();
            this.homeAway = "VISITANTE";
        }

        this.played = match.getState() == MatchState.FINISHED;

        if (!played || match.getResult() == null) {
            this.result = "-";
            this.resultClass = "fixture-match-pending";
            return;
        }

        int homeGoals = match.getResult().getHomeGoals();

        int awayGoals = match.getResult().getAwayGoals();

        if (managedClubIsHome){
            this.result = homeGoals + " - " + awayGoals;

            if (homeGoals > awayGoals) {
                this.resultClass = "fixture-match-win";
            } else if (homeGoals == awayGoals) {
                this.resultClass = "fixture-match-draw";
            } else {
                this.resultClass = "fixture-match-loss";
            }
        } else{
            this.result = awayGoals + " - " + homeGoals;

            if (awayGoals > homeGoals) {
                this.resultClass = "fixture-match-win";
            } else if (awayGoals == homeGoals) {
                this.resultClass = "fixture-match-draw";
            } else {
                this.resultClass = "fixture-match-loss";
            }
        }
    }
}