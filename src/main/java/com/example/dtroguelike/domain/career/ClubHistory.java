package com.example.dtroguelike.domain.career;

/**
 * Registro historico del paso del DT por un club especifico.
 */
public class ClubHistory {

    private final String clubId;
    private final String clubName;
    private int seasonsManaged;
    private int wins;
    private int draws;
    private int losses;
    private int championships;
    private int promotions;
    private int relegations;
    private int idolatry;
    private ClubDepartureReason departureReason;

    public ClubHistory(String clubId, String clubName) {
        this.clubId = clubId;
        this.clubName = clubName;
    }

    public String getClubId() {
        return clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public int getSeasonsManaged() {
        return seasonsManaged;
    }

    public void incrementSeasonsManaged() {
        this.seasonsManaged++;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLosses() {
        return losses;
    }

    public void registerMatch(boolean win, boolean draw) {
        if (win) {
            wins++;
        } else if (draw) {
            draws++;
        } else {
            losses++;
        }
    }

    public int getChampionships() {
        return championships;
    }

    public void incrementChampionships() {
        this.championships++;
    }

    public int getPromotions() {
        return promotions;
    }

    public void incrementPromotions() {
        this.promotions++;
    }

    public int getRelegations() {
        return relegations;
    }

    public void incrementRelegations() {
        this.relegations++;
    }

    public int getIdolatry() {
        return idolatry;
    }

    public void setIdolatry(int idolatry) {
        this.idolatry = idolatry;
    }

    public ClubDepartureReason getDepartureReason() {
        return departureReason;
    }

    public void setDepartureReason(ClubDepartureReason departureReason) {
        this.departureReason = departureReason;
    }
}
