package com.example.dtroguelike.domain.club;

/**
 * Datos permanentes de un club (no cambian durante la carrera).
 * El estado variable vive en {@link ClubState}.
 */
public class Club {

    private final String id;
    private final String name;
    private final String country;
    private final String league;
    private int reputation;
    private final TeamStrength strength;
    private ClubExpectations expectations;

    public Club(String id, String name, String country, String league,
                int reputation, TeamStrength strength) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.league = league;
        this.reputation = reputation;
        this.strength = strength;
        this.expectations = ClubExpectations.basedOnReputation(reputation);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getLeague() {
        return league;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
        this.expectations = ClubExpectations.basedOnReputation(reputation);
    }

    public TeamStrength getStrength() {
        return strength;
    }

    public ClubExpectations getExpectations() {
        return expectations;
    }

    /** Crea un nuevo estado de carrera "fresco" para este club. */
    public ClubState createInitialState() {
        return ClubState.initial();
    }
}
