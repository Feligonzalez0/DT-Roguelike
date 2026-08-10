package com.example.dtroguelike.web.viewmodels;

import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.season.Season;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FixtureViewModel {

    public final int seasonYear;
    public final String clubName;
    public final List<FixtureRoundViewModel> rounds;

    public FixtureViewModel(
            Season season,
            Club managedClub) {

        this.seasonYear = season.getYear();
        this.clubName = managedClub.getName();

        String clubId = managedClub.getId();

        List<Match>clubMatches = season.getMatches().stream().filter(match->match.getHomeTeam().getId().equals(clubId) ||
                                match.getAwayTeam().getId().equals(clubId))
                                .sorted(Comparator.comparing(Match::getRound)).toList();

        this.rounds =
                clubMatches.stream()
                        .collect(
                                Collectors.groupingBy(
                                        Match::getRound))
                        .entrySet()
                        .stream()
                        .sorted(
                                Comparator.comparing(
                                        entry -> entry.getKey()))
                        .map(entry ->
                                new FixtureRoundViewModel(
                                        entry.getKey(),
                                        entry.getValue()
                                                .stream()
                                                .map(match ->
                                                        new FixtureMatchViewModel(
                                                                match,
                                                                clubId))
                                                .toList()
                                )
                        )
                        .collect(Collectors.toList());
    }
}