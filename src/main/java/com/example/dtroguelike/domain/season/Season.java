package com.example.dtroguelike.domain.season;

import com.example.dtroguelike.domain.match.Match;

import java.util.ArrayList;
import java.util.List;

/**
 * Una temporada dentro de la carrera del DT.
 */
public class Season {

    private final int year;
    private SeasonPhase phase;
    private final SeasonStats stats;
    private final List<Match> matches = new ArrayList<>();

    public Season(int year) {
        this.year = year;
        this.phase = SeasonPhase.PRESEASON;
        this.stats = new SeasonStats();
    }

    public int getYear() {
        return year;
    }

    public SeasonPhase getPhase() {
        return phase;
    }

    /**
     * Avanza de PRESEASON a TRANSFER_WINDOW.
     */
    public void startTransferWindow() {
        requirePhase(SeasonPhase.PRESEASON);
        phase = SeasonPhase.TRANSFER_WINDOW;
    }

    /**
     * Avanza de TRANSFER_WINDOW a REGULAR_SEASON.
     */
    public void startRegularSeason() {
        requirePhase(SeasonPhase.TRANSFER_WINDOW);
        phase = SeasonPhase.REGULAR_SEASON;
    }

    /**
     * Avanza de REGULAR_SEASON a END_OF_SEASON.
     */
    public void finish() {
        requirePhase(SeasonPhase.REGULAR_SEASON);
        phase = SeasonPhase.END_OF_SEASON;
    }

    /**
     * Avanza de END_OF_SEASON a SUMMARY.
     */
    public void showSummary() {
        requirePhase(SeasonPhase.END_OF_SEASON);
        phase = SeasonPhase.SUMMARY;
    }

    /*
    Chequeo que la fase requerida coincida.
     */
    private void requirePhase(SeasonPhase expected) {
        if (phase != expected) {
            throw new IllegalStateException(
                    "No se puede avanzar de " + phase + "; se esperaba " + expected + "."
            );
        }
    }

    public SeasonStats getStats() {
        return stats;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void addMatch(Match match) {
        matches.add(match);
    }
}
