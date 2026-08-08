package com.example.dtroguelike.domain.career;

import com.example.dtroguelike.domain.achievement.Achievement;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa el legado acumulado del Director Tecnico a lo largo de su
 * carrera. El calculo detallado de legado (formulas, ponderaciones,
 * etc.) queda pendiente para una iteracion futura.
 */
public class Legacy {

    private int totalIdolatry;
    private int championships;
    private int promotions;
    private int relegations;
    private int importantWins;
    private final List<ClubHistory> clubHistory = new ArrayList<>();
    private final List<Achievement> achievements = new ArrayList<>();

    public int getTotalIdolatry() {
        return totalIdolatry;
    }

    public void addIdolatry(int amount) {
        this.totalIdolatry += amount;
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

    public int getImportantWins() {
        return importantWins;
    }

    public void incrementImportantWins() {
        this.importantWins++;
    }

    public List<ClubHistory> getClubHistory() {
        return clubHistory;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public void addAchievement(Achievement achievement) {
        achievements.add(achievement);
    }

    // TODO: implementar calculo completo de un "puntaje de legado" que
    // combine idolatria, titulos y logros en un unico indicador.
}
