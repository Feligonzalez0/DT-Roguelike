package com.example.dtroguelike.domain.season;

public class SeasonObjective {
    private final int targetPosition;
    private final boolean mustWinLeague;
    private String description;

    public SeasonObjective(int targetPosition, boolean mustWinLeague){
        this.targetPosition = targetPosition;
        this.mustWinLeague = mustWinLeague;
        setDescription(targetPosition);
    }

    // GETTERS
    public int getTargetPosition() {
        return targetPosition;
    }

    public boolean mustWinLeague() {
        return mustWinLeague;
    }

    public String getDescription() {
        return description;
    }

    // SETTERS
    private void setDescription(int targetPosition) {
        this.description = "Quedar entre los " + targetPosition + " primeros";

        if (targetPosition == 1){
            this.description = "Ganar la liga";
        }
    }
}
