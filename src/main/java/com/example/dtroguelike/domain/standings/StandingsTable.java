package com.example.dtroguelike.domain.standings;

import com.example.dtroguelike.domain.club.Club;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StandingsTable {

    private final List<StandingsEntry> entries;

    public StandingsTable(List<Club> clubs) {

        this.entries = clubs.stream()
                .map(StandingsEntry::new)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    public List<StandingsEntry> getEntries() {
        return List.copyOf(entries);
    }

    public StandingsEntry getEntry(String clubId) {

        return entries.stream()
                .filter(entry ->
                        entry.getClub()
                                .getId()
                                .equals(clubId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Club no encontrado en la tabla: "
                                        + clubId
                        )
                );
    }

    public void registerMatch(Club home, Club away, int homeGoals, int awayGoals){
        StandingsEntry homeEntry = getEntry(home.getId());

        StandingsEntry awayEntry = getEntry(away.getId());

        if (homeGoals > awayGoals){
            homeEntry.registerWin(homeGoals,awayGoals);
            awayEntry.registerLoss(awayGoals, homeGoals);
        } else if (homeGoals < awayGoals){
            homeEntry.registerLoss(homeGoals, awayGoals);
            awayEntry.registerWin(awayGoals, homeGoals);
        } else {
            homeEntry.registerDraw(homeGoals, awayGoals);
            awayEntry.registerDraw(awayGoals, homeGoals);
        }

        sort();
    }

    private void sort() {

        entries.sort(
                Comparator
                        .comparingInt(
                                StandingsEntry::getPoints
                        )
                        .reversed()

                        .thenComparingInt(
                                StandingsEntry::getGoalDifference
                        )
                        .reversed()

                        .thenComparingInt(
                                StandingsEntry::getGoalsFor
                        )
                        .reversed()

                        .thenComparing(
                                entry ->
                                        entry.getClub().getName()
                        )
        );

        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setPosition(i + 1);
        }
    }
}