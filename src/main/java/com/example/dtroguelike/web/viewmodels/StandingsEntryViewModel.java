package com.example.dtroguelike.web.viewmodels;

import com.example.dtroguelike.domain.standings.StandingsEntry;

public class StandingsEntryViewModel {

    public final int position;
    public final String clubName;
    public final int played;
    public final int wins;
    public final int draws;
    public final int losses;
    public final int goalsFor;
    public final int goalsAgainst;
    public final int goalDifference;
    public final int points;
    public final String clubId;
    public final boolean isManagedClub;

    public StandingsEntryViewModel(StandingsEntry entry, String managedClubId){
        this.position = entry.getPosition();
        this.clubName = entry.getClub().getName();
        this.played = entry.getPlayed();
        this.wins = entry.getWins();
        this.draws = entry.getDraws();
        this.losses = entry.getLosses();
        this.goalsFor = entry.getGoalsFor();
        this.goalsAgainst = entry.getGoalsAgainst();
        this.goalDifference = entry.getGoalDifference();
        this.points = entry.getPoints();
        this.clubId = entry.getClub().getId();
        this.isManagedClub = this.clubId.equals(managedClubId);
    }

    public String getClubId(){
        return clubId;
    }
}