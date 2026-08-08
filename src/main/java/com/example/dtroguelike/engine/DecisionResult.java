package com.example.dtroguelike.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de resolver una decision del jugador ante un {@code Event},
 * listo para ser mostrado en la interfaz.
 */
public class DecisionResult {

    private final boolean requirementsMet;
    private final boolean success;
    private final String description;
    private final List<EffectResult> appliedEffects;

    public DecisionResult(boolean requirementsMet, boolean success, String description,
                           List<EffectResult> appliedEffects) {
        this.requirementsMet = requirementsMet;
        this.success = success;
        this.description = description;
        this.appliedEffects = appliedEffects != null ? appliedEffects : new ArrayList<>();
    }

    public static DecisionResult requirementsNotMet(String reason) {
        return new DecisionResult(false, false, reason, new ArrayList<>());
    }

    public boolean isRequirementsMet() {
        return requirementsMet;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getDescription() {
        return description;
    }

    public List<EffectResult> getAppliedEffects() {
        return appliedEffects;
    }
}
