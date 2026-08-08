package com.example.dtroguelike.domain.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Una de las opciones que el jugador puede elegir al enfrentar un
 * {@link Event}. Puede tener requisitos previos y una probabilidad de
 * exito que determina cual de los dos posibles {@link Outcome} se aplica.
 */
public class EventOption {

    private final String id;
    private final String description;
    /** Probabilidad de exito, de 0.0 a 1.0. */
    private final double successChance;
    private final List<EventCondition> requirements;
    private final Outcome successOutcome;
    private final Outcome failureOutcome;

    public EventOption(String id, String description, double successChance,
                        List<EventCondition> requirements,
                        Outcome successOutcome, Outcome failureOutcome) {
        this.id = id;
        this.description = description;
        this.successChance = successChance;
        this.requirements = requirements != null ? requirements : new ArrayList<>();
        this.successOutcome = successOutcome;
        this.failureOutcome = failureOutcome;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getSuccessChance() {
        return successChance;
    }

    public List<EventCondition> getRequirements() {
        return requirements;
    }

    public Outcome getSuccessOutcome() {
        return successOutcome;
    }

    public Outcome getFailureOutcome() {
        return failureOutcome;
    }
}
