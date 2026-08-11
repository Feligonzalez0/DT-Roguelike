package com.example.dtroguelike.web.viewmodels;

import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.season.Season;

import java.util.ArrayList;
import java.util.List;

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

        List<List<Match>> fixture =
                season.getFixture();

        this.rounds = new ArrayList<>();

        for (int i = 0; i < fixture.size(); i++) {

            int roundNumber = i + 1;

            List<FixtureMatchViewModel> roundMatches =
                    fixture.get(i)
                            .stream()
                            .filter(match ->
                                    match.getHomeTeam()
                                            .getId()
                                            .equals(clubId)
                                    ||
                                    match.getAwayTeam()
                                            .getId()
                                            .equals(clubId)
                            )
                            .map(match ->
                                    new FixtureMatchViewModel(
                                            match,
                                            clubId
                                    )
                            )
                            .toList();

            if (!roundMatches.isEmpty()) {
                this.rounds.add(
                        new FixtureRoundViewModel(
                                roundNumber,
                                roundMatches
                        )
                );
            }
        }
    }
}