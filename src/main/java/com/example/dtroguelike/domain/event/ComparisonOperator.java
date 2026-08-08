package com.example.dtroguelike.domain.event;

/** Operador de comparacion usado por {@link EventCondition}. */
public enum ComparisonOperator {
    EQUAL,
    NOT_EQUAL,
    GREATER_THAN,
    GREATER_OR_EQUAL,
    LESS_THAN,
    LESS_OR_EQUAL;

    public boolean evaluate(int actual, int expected) {
        return switch (this) {
            case EQUAL -> actual == expected;
            case NOT_EQUAL -> actual != expected;
            case GREATER_THAN -> actual > expected;
            case GREATER_OR_EQUAL -> actual >= expected;
            case LESS_THAN -> actual < expected;
            case LESS_OR_EQUAL -> actual <= expected;
        };
    }
}
