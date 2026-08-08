package com.example.dtroguelike.domain.manager;

/**
 * Estadisticas historicas acumuladas del Director Tecnico a lo largo de
 * toda su carrera. No todas se actualizan todavia en el MVP, pero la
 * estructura queda preparada para hacerlo.
 */
public class ManagerStats {

    private int seasonsManaged;
    private int matchesManaged;
    private int wins;
    private int draws;
    private int losses;
    private int championships;
    private int promotions;
    private int relegations;
    private int clubsManaged;

    public int getSeasonsManaged() {
        return seasonsManaged;
    }

    public void incrementSeasonsManaged() {
        this.seasonsManaged++;
    }

    public int getMatchesManaged() {
        return matchesManaged;
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

    public void registerMatchResult(MatchOutcomeForManager outcome) {
        matchesManaged++;
        switch (outcome) {
            case WIN -> wins++;
            case DRAW -> draws++;
            case LOSS -> losses++;
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

    public int getClubsManaged() {
        return clubsManaged;
    }

    public void incrementClubsManaged() {
        this.clubsManaged++;
    }

    /**
     * Resultado de un partido desde la perspectiva del Manager, usado
     * unicamente para actualizar {@link ManagerStats}.
     */
    public enum MatchOutcomeForManager {
        WIN, DRAW, LOSS
    }
}
