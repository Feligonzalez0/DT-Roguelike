package com.example.dtroguelike.domain.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Consecuencia narrativa de resolver una {@link EventOption}: un texto
 * descriptivo mas una lista de {@link Effect} a aplicar sobre el estado
 * del juego.
 */
public class Outcome {

    private final String description;
    private final List<Effect> effects;

    public Outcome(String description, List<Effect> effects) {
        this.description = description;
        this.effects = effects != null ? effects : new ArrayList<>();
    }

    public static Outcome of(String description, Effect... effects) {
        List<Effect> list = new ArrayList<>();
        Collections.addAll(list, effects);
        return new Outcome(description, list);
    }

    public String getDescription() {
        return description;
    }

    public List<Effect> getEffects() {
        return effects;
    }
}
