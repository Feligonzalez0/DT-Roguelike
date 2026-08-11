package com.example.dtroguelike.web.viewmodels;

import java.util.List;

import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.domain.season.SeasonPhase;

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

    public final boolean isPreseason;
    public final boolean isTransferWindow;
    public final boolean isRegularSeason;
    public final boolean isEndOfSeason;
    public final boolean isSummary;

    public final List<FixtureMatchViewModel> recentFixture;

    public SeasonViewModel(Season season, Club managedClub) {
        this.year = season.getYear();
        this.phase = season.getPhase().name();
        this.matchesPlayed = season.getStats().getMatchesPlayed();
        this.wins = season.getStats().getWins();
        this.draws = season.getStats().getDraws();
        this.losses = season.getStats().getLosses();
        this.goalsFor = season.getStats().getGoalsFor();
        this.goalsAgainst = season.getStats().getGoalsAgainst();
        this.points = season.getStats().getPoints();

        this.isPreseason = season.getPhase() == SeasonPhase.PRESEASON;
        this.isTransferWindow = season.getPhase() == SeasonPhase.TRANSFER_WINDOW;
        this.isRegularSeason = season.getPhase() == SeasonPhase.REGULAR_SEASON;
        this.isEndOfSeason = season.getPhase() == SeasonPhase.END_OF_SEASON;
        this.isSummary = season.getPhase() == SeasonPhase.SUMMARY;
        
        if (managedClub == null) {
            this.recentFixture = List.of();
            return;
        }
        String clubId = managedClub.getId();

        List<List<Match>> fixture = season.getFixture();

        int playedRounds = season.getCurrentRound();

        int fromRound = Math.max(0, playedRounds - 5);
        int toRound = Math.max(playedRounds, 5);

        this.recentFixture = fixture.subList(fromRound, toRound)
                .stream()
                .flatMap(List::stream)
                .filter(match ->
                        match.getHomeTeam().getId().equals(clubId)
                        || match.getAwayTeam().getId().equals(clubId)
                )
                .map(match ->
                        new FixtureMatchViewModel(match, clubId)
                )
                .toList();
    }
}
