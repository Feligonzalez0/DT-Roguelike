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

    public void setPhase(SeasonPhase phase) {
        this.phase = phase;
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
