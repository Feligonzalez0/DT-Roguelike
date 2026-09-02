package com.example.dtroguelike.web.viewmodels;

import com.example.dtroguelike.application.PreseasonOutcome;

/**
 * Representacion plana del resultado de la decision de pretemporada
 * para la vista Mustache: nombre del atributo, valor antes/despues y
 * el texto descriptivo del efecto aplicado.
 */
public class PreseasonOutcomeViewModel {

    public final String attributeName;
    public final int before;
    public final int after;
    public final int delta;
    public final String description;

    public PreseasonOutcomeViewModel(PreseasonOutcome outcome) {
        this.attributeName = outcome.attribute().getDisplayName();
        this.before = outcome.before();
        this.after = outcome.after();
        this.delta = outcome.delta();
        this.description = outcome.decisionResult().getDescription();
    }
}
