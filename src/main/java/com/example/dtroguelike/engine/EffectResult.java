package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.event.EffectType;

/**
 * Representa un {@link com.example.dtroguelike.domain.event.Effect} ya
 * aplicado, en un formato simple y entendible para la interfaz.
 */
public class EffectResult {

    private final EffectType type;
    private final int amount;

    public EffectResult(EffectType type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public EffectType getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        String sign = amount >= 0 ? "+" : "";
        return type + " " + sign + amount;
    }
}
