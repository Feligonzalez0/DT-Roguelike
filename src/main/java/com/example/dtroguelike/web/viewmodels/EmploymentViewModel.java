package com.example.dtroguelike.web.viewmodels;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.offer.ClubOffer;

public class EmploymentViewModel {

    public final String clubName;

    public final int jobSecurity;
    public final int contractRemainingYears;

    public final boolean contractActive;
    public final boolean contractEnded;

    public final boolean renewalAvailable;
    public final ClubOfferViewModel renewalOffer;

    public EmploymentViewModel(
            Career career,
            ClubOffer renewalOffer) {

        this.clubName =
                career.getCurrentClub().getName();

        this.jobSecurity =
                career.getCurrentClubState().getJobSecurity();

        this.contractRemainingYears =
                career.getContractRemainingYears();

        this.contractActive =
                contractRemainingYears > 0;

        this.contractEnded =
                contractRemainingYears <= 0;

        this.renewalAvailable =
                this.contractEnded &&
                renewalOffer != null;

        this.renewalOffer =
                renewalOffer != null
                        ? new ClubOfferViewModel(renewalOffer)
                        : null;
    }
}