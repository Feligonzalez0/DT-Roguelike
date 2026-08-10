package com.example.dtroguelike.domain.match;

import com.example.dtroguelike.domain.club.Club;

import java.util.UUID;

/**
 * Representa un partido de futbol dentro de una temporada.
 */
public class Match {

    private final String id;
    private final Club homeTeam;
    private final Club awayTeam;
    private final MatchCompetition competition;
    private final MatchImportance importance;
    private final int round;

    private MatchState state;
    private MatchResult result;

    public Match(
            Club homeTeam,
            Club awayTeam,
            MatchCompetition competition,
            MatchImportance importance,
            int round) {

        this.id = UUID.randomUUID().toString();
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.competition = competition;
        this.importance = importance;
        this.round = round;
        this.state = MatchState.NOT_STARTED;
    }

    public String getId() {
        return id;
    }

    public Club getHomeTeam() {
        return homeTeam;
    }

    public Club getAwayTeam() {
        return awayTeam;
    }

    public MatchCompetition getCompetition() {
        return competition;
    }

    public MatchImportance getImportance() {
        return importance;
    }

    public int getRound() {
        return round;
    }

    public MatchState getState() {
        return state;
    }

    public void setState(MatchState state) {
        this.state = state;
    }

    public MatchResult getResult() {
        return result;
    }

    public void setResult(MatchResult result) {
        this.result = result;
        this.state = MatchState.FINISHED;
    }
}