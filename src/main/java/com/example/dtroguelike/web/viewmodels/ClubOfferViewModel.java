package com.example.dtroguelike.web.viewmodels;

import com.example.dtroguelike.domain.offer.ClubOffer;

/**
 * Representacion plana de una {@link ClubOffer} para la vista de
 * seleccion de club.
 */
public class ClubOfferViewModel {

    public final String clubId;
    public final String clubName;
    public final String league;
    public final int reputation;
    public final long salary;
    public final int contractLengthYears;
    public final int jobSecurity;
    public final boolean expectedToWinLeague;
    public final int minimumExpectedPosition;

    public ClubOfferViewModel(ClubOffer offer) {
        this.clubId = offer.getClub().getId();
        this.clubName = offer.getClub().getName();
        this.league = offer.getClub().getLeague();
        this.reputation = offer.getClub().getReputation();
        this.salary = offer.getSalary();
        this.contractLengthYears = offer.getContractLengthYears();
        this.jobSecurity = offer.getJobSecurity();
        this.expectedToWinLeague = offer.getExpectations().isExpectedToWinLeague();
        this.minimumExpectedPosition = offer.getExpectations().getMinimumExpectedPosition();
    }
}
