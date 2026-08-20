package com.example.dtroguelike.domain.season;

import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.ClubExpectations;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.standings.StandingsTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Una temporada dentro de la carrera del DT.
 */
public class Season {

    private final int year;
    private SeasonPhase phase;
    private final SeasonStats stats;
    private List<List<Match>> fixture = new ArrayList<>();
    private int currentRound;
    private StandingsTable standings;
    private SeasonObjective objective;
    private SeasonObjectiveResult objectiveResult;
    private int reputationChange;

    public Season(int year, Club managedClub) {
        this.year = year;
        this.phase = SeasonPhase.PRESEASON;
        this.stats = new SeasonStats();
        this.currentRound = 0;
        this.reputationChange = 0;

        ClubExpectations expectations = managedClub.getExpectations();
        int targetPosition = expectations.getMinimumExpectedPosition();
        boolean mustWinLeague = expectations.isExpectedToWinLeague();
    
        this.objective = new SeasonObjective(targetPosition, mustWinLeague);
    }

    public int getYear() {
        return year;
    }

    public SeasonPhase getPhase() {
        return phase;
    }
    public SeasonStats getStats() {
        return stats;
    }

    public List<List<Match>> getFixture() {
        return Collections.unmodifiableList(fixture);
    }

    public void setFixture(List<List<Match>> fixture) {
        this.fixture = fixture;
    }

    public int getCurrentRound(){
        return this.currentRound;
    }

    public void addRound(List<Match> matches) {
        fixture.add(matches);
    }

    public SeasonObjective getObjective(){
        return objective;
    }
    
    public void setObjectiveResult(SeasonObjectiveResult objectiveResult) {
        this.objectiveResult = objectiveResult;
    }

    public SeasonObjectiveResult getObjectiveResult() {
        return objectiveResult;
    }
    public int getReputationChange() {
        return reputationChange;
    }

    public void setReputationChange(int reputationChange) {
        this.reputationChange = reputationChange;
    }
    
    /*
    * Transiciones de fase.
    */

    //Avanza de PRESEASON a TRANSFER_WINDOW.
    public void startTransferWindow() {
        requirePhase(SeasonPhase.PRESEASON);
        phase = SeasonPhase.TRANSFER_WINDOW;
    }

    //Avanza de TRANSFER_WINDOW a REGULAR_SEASON.
    public void startRegularSeason() {
        requirePhase(SeasonPhase.TRANSFER_WINDOW);
        phase = SeasonPhase.REGULAR_SEASON;
    }

    //Avanza de REGULAR_SEASON a END_OF_SEASON.
    public void finish() {
        requirePhase(SeasonPhase.REGULAR_SEASON);
        phase = SeasonPhase.END_OF_SEASON;
    }

    //Avanza de END_OF_SEASON a SUMMARY.
    public void showSummary() {
        requirePhase(SeasonPhase.END_OF_SEASON);
        phase = SeasonPhase.SUMMARY;
    }

    public void incrementCurrentRound(){
        this.currentRound++;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    //Chequeo que la fase requerida coincida.
    private void requirePhase(SeasonPhase expected) {
        if (phase != expected) {
            throw new IllegalStateException(
                    "No se puede avanzar de " + phase + "; se esperaba " + expected + "."
            );
        }
    }

    // TABLA DE POSICIONES
    public StandingsTable getStandings() {
        return standings;
    }

    public void setStandings(StandingsTable standings) {
        this.standings = standings;
    }
}
