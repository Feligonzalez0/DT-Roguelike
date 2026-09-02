package com.example.dtroguelike.application;

import com.example.dtroguelike.domain.manager.ManagerAttributeType;
import com.example.dtroguelike.engine.DecisionResult;

/**
 * Resultado de resolver la decision del evento de pretemporada: que
 * atributo se eligio, su valor antes y despues de aplicar el efecto, y
 * el {@link DecisionResult} crudo devuelto por el {@code DecisionResolver}.
 */
public record PreseasonOutcome(
        ManagerAttributeType attribute,
        int before,
        int after,
        DecisionResult decisionResult
) {
    public int delta() {
        return after - before;
    }
}
