package com.example.dtroguelike.domain.offer;

import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.ClubExpectations;

/**
 * Oferta de trabajo de un club hacia el Manager.
 */
public class ClubOffer {

    private final Club club;
    private final long salary;
    private final int contractLengthYears;
    private final ClubExpectations expectations;
    private final int jobSecurity;

    public ClubOffer(Club club, long salary, int contractLengthYears,
                      ClubExpectations expectations, int jobSecurity) {
        this.club = club;
        this.salary = salary;
        this.contractLengthYears = contractLengthYears;
        this.expectations = expectations;
        this.jobSecurity = jobSecurity;
    }

    public Club getClub() {
        return club;
    }

    public long getSalary() {
        return salary;
    }

    public int getContractLengthYears() {
        return contractLengthYears;
    }

    public ClubExpectations getExpectations() {
        return expectations;
    }

    public int getJobSecurity() {
        return jobSecurity;
    }
}
