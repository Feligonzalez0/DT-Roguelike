package com.example.dtroguelike.domain.standings;

import com.example.dtroguelike.domain.club.Club;

public class StandingsEntry {

    private final Club club;

    private int position;
    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int points;

    public StandingsEntry(Club club) {
        this.club = club;
    }

    public Club getClub() {
        return club;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPlayed() {
        return played;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLosses() {
        return losses;
    }

    public int getGoalsFor() {
        return goalsFor;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public int getPoints() {
        return points;
    }

    public void registerWin(int goalsFor, int goalsAgainst) {
        played++;
        wins++;

        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;

        points += 3;
    }

    public void registerDraw(int goalsFor, int goalsAgainst) {
        played++;
        draws++;

        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;

        points += 1;
    }

    public void registerLoss(int goalsFor, int goalsAgainst){
        played++;
        losses++;

        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
    }
}