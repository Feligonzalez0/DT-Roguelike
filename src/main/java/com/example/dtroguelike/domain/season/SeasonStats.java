package com.example.dtroguelike.domain.season;

/**
 * Estadisticas acumuladas durante una temporada individual.
 */
public class SeasonStats {

    private int matchesPlayed;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int leaguePosition;
    private int championshipsWon;
    private int cupsWon;

    public int getMatchesPlayed() {
        return matchesPlayed;
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

    public int getLeaguePosition() {
        return leaguePosition;
    }

    public void setLeaguePosition(int leaguePosition) {
        this.leaguePosition = leaguePosition;
    }

    public int getChampionshipsWon() {
        return championshipsWon;
    }

    public void incrementChampionshipsWon() {
        this.championshipsWon++;
    }

    public int getCupsWon() {
        return cupsWon;
    }

    public void incrementCupsWon() {
        this.cupsWon++;
    }

    public void registerMatch(int goalsFor, int goalsAgainst) {
        matchesPlayed++;
        this.goalsFor += goalsFor;
        this.goalsAgainst += goalsAgainst;
        if (goalsFor > goalsAgainst) {
            wins++;
        } else if (goalsFor < goalsAgainst) {
            losses++;
        } else {
            draws++;
        }
    }

    public int getPoints() {
        return wins * 3 + draws;
    }
}
