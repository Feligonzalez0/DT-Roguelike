package com.example.dtroguelike.domain.season;

public class SeasonObjectiveResult {
    private boolean objectiveCompleted;
    private int finalPosition;

    public void evaluateObjective(int targetPosition, int finalPosition, boolean mustWinLeague) {
        // Si debe ganar la liga, debe ser posición 1. Si no, aplica el targetPosition.
        objectiveCompleted = mustWinLeague ? finalPosition == 1 : finalPosition <= targetPosition;

        this.finalPosition = finalPosition;
    }

    public boolean isObjectiveCompleted() {
        return objectiveCompleted;
    }

    public int getFinalPosition() {
        return finalPosition;
    }

    public String getStatusText() {
        return objectiveCompleted ? "CUMPLIDO" : "INCUMPLIDO";
    }
}
