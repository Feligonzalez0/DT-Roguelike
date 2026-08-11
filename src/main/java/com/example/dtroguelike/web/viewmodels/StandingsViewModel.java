package com.example.dtroguelike.web.viewmodels;

import java.util.List;

import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.season.Season;

public class StandingsViewModel {

    public final int seasonYear;
    public final List<StandingsEntryViewModel> entries;

    public StandingsViewModel(Season season, Club managedClub) {

        this.seasonYear = season.getYear();

        this.entries = season.getStandings().getEntries()
        .stream()
        .map(entry ->
                new StandingsEntryViewModel(
                        entry,
                        managedClub.getId()
                )
        )
        .toList();
    }

    public List<StandingsEntryViewModel> getNearbyEntries(String managedClubId) {
        int managedIndex = -1;

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getClubId().equals(managedClubId)){
                managedIndex = i;
                break;
            }
        }

        if (managedIndex == -1) {
            return entries.stream()
                    .limit(5)
                    .toList();
        }

        /*
        * Queremos mostrar 5 posiciones:
        *
        *   2 arriba
        *   club dirigido
        *   2 abajo
        *
        * Si estamos cerca del principio o del final,
        * desplazamos la ventana para seguir mostrando 5.
        */

        int fromIndex = managedIndex - 2;
        int toIndex = managedIndex + 3;

        if (fromIndex < 0) {

            fromIndex = 0;
            toIndex = Math.min(5, entries.size());

        } else if (toIndex > entries.size()) {

            toIndex = entries.size();
            fromIndex = Math.max(0, toIndex - 5);
        }

        return entries.subList(fromIndex, toIndex);
    }
}