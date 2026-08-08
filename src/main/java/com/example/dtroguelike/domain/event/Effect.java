package com.example.dtroguelike.domain.event;

/**
 * Un cambio puntual sobre el estado del juego (por ejemplo:
 * MANAGER_REPUTATION +5, o CLUB_MORALE -10).
 */
public class Effect {

    private final EffectType type;
    private final int amount;

    public Effect(EffectType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public EffectType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }
}
