package com.example.dtroguelike.domain.event;

/**
 * Condicion simple del tipo "variable OPERADOR valor", por ejemplo:
 * reputation &gt; 50, morale &lt; 30, boardRelation &gt;= 60.
 *
 * La evaluacion real contra el estado de la carrera se resuelve en el
 * engine (ver DecisionResolver/EventEngine), que sabe como leer cada
 * {@link ConditionType} desde el {@code Career}.
 */
public class EventCondition {

    private final ConditionType type;
    private final ComparisonOperator operator;
    private final int value;

    public EventCondition(ConditionType type, ComparisonOperator operator, int value) {
        this.type = type;
        this.operator = operator;
        this.value = value;
    }

    public ConditionType getType() {
        return type;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public int getValue() {
        return value;
    }

    /** Evalua la condicion dado el valor actual de la variable que representa. */
    public boolean isSatisfiedBy(int actualValue) {
        return operator.evaluate(actualValue, value);
    }
}
