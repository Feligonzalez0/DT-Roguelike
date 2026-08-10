package com.example.dtroguelike.web.viewmodels;

import java.util.Comparator;
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

        List<Match> clubMatches =
                season.getMatches().stream().filter(match -> match.getHomeTeam().getId().equals(clubId) || 
                match.getAwayTeam().getId().equals(clubId))
                .sorted(Comparator.comparing(Match::getRound)).toList();
        // Dashboard: solamente las últimas 5 fechas.
        int fromIndex = Math.max(0, clubMatches.size() - 5);

        this.recentFixture = clubMatches.subList(fromIndex, clubMatches.size()).stream()
            .map(match -> new FixtureMatchViewModel(match,clubId)).toList();
    }
}
