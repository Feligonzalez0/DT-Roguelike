package com.example.dtroguelike.domain.achievement;

import java.time.LocalDate;

/**
 * Logro desbloqueado por el Manager durante su carrera.
 * El sistema completo de desbloqueo automatico queda pendiente para una
 * iteracion futura; por ahora la clase solo modela el dato.
 */
public class Achievement {

    private final AchievementType type;
    private final String description;
    private final LocalDate unlockedAt;

    public Achievement(AchievementType type, String description, LocalDate unlockedAt) {
        this.type = type;
        this.description = description;
        this.unlockedAt = unlockedAt;
    }

    public AchievementType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getUnlockedAt() {
        return unlockedAt;
    }
}
