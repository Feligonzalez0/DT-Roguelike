package com.example.dtroguelike.web.viewmodels;

import java.util.List;

import com.example.dtroguelike.domain.career.Career;

/**
 * Modelo agregado que junta todo lo que necesita el dashboard: DT,
 * club y temporada actuales.
 */
public class DashboardViewModel {

    public final ManagerViewModel manager;
    public final ClubViewModel club;
    public final SeasonViewModel season;
    public final String careerState;
    public final String gamePhase;
    public final boolean hasClub;
    public final List<StandingsEntryViewModel> nearbyStandings;

    public DashboardViewModel(Career career) {
        this.manager = new ManagerViewModel(career.getManager());
        this.hasClub = career.getCurrentClub() != null;
        this.club = hasClub ? new ClubViewModel(career.getCurrentClub(), career.getCurrentClubState()) : null;
        this.season = career.getCurrentSeason() != null ? new SeasonViewModel(career.getCurrentSeason(), career.getCurrentClub()) : null;
        this.careerState = career.getState().name();
        this.gamePhase = career.getPhase().name();
        
        //Table preview
        if (career.getCurrentSeason() != null && career.getCurrentClub() != null 
            && career.getCurrentSeason().getStandings() != null){
            StandingsViewModel standings = new StandingsViewModel(career.getCurrentSeason(), career.getCurrentClub());
            this.nearbyStandings = standings.getNearbyEntries(career.getCurrentClub().getId());
        } else {
            this.nearbyStandings = List.of();
        }

    }
}
