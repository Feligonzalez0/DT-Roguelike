package com.example.dtroguelike.domain.season;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeasonObjectiveResultTest {

    @Test
    void shouldCompleteObjectiveWhenFinalPositionIsWithinTarget() {
        SeasonObjective objective = new SeasonObjective(8, false);
        SeasonObjectiveResult result = new SeasonObjectiveResult();

        result.evaluateObjective(
                objective.getTargetPosition(),
                5,
                objective.mustWinLeague()
        );

        assertTrue(result.isObjectiveCompleted());
    }

    @Test
    void shouldCompleteObjectiveWhenFinalPositionEqualsTarget() {
        SeasonObjective objective = new SeasonObjective(8, false);
        SeasonObjectiveResult result = new SeasonObjectiveResult();

        result.evaluateObjective(
                objective.getTargetPosition(),
                8,
                objective.mustWinLeague()
        );

        assertTrue(result.isObjectiveCompleted());
    }

    @Test
    void shouldFailObjectiveWhenFinalPositionIsWorseThanTarget() {
        SeasonObjective objective = new SeasonObjective(8, false);
        SeasonObjectiveResult result = new SeasonObjectiveResult();

        result.evaluateObjective(
                objective.getTargetPosition(),
                12,
                objective.mustWinLeague()
        );

        assertFalse(result.isObjectiveCompleted());
    }

    @Test
    void shouldCompleteLeagueTitleObjectiveWhenFinishingFirst() {
        SeasonObjective objective = new SeasonObjective(1, true);
        SeasonObjectiveResult result = new SeasonObjectiveResult();

        result.evaluateObjective(
                objective.getTargetPosition(),
                1,
                objective.mustWinLeague()
        );

        assertTrue(result.isObjectiveCompleted());
    }

    @Test
    void shouldFailLeagueTitleObjectiveWhenFinishingSecond() {
        SeasonObjective objective = new SeasonObjective(1, true);
        SeasonObjectiveResult result = new SeasonObjectiveResult();

        result.evaluateObjective(
                objective.getTargetPosition(),
                2,
                objective.mustWinLeague()
        );

        assertFalse(result.isObjectiveCompleted());
    }

    @Test
    void shouldFailLeagueTitleObjectiveWhenFinishingThird() {
        SeasonObjective objective = new SeasonObjective(1, true);
        SeasonObjectiveResult result = new SeasonObjectiveResult();

        result.evaluateObjective(
                objective.getTargetPosition(),
                3,
                objective.mustWinLeague()
        );

        assertFalse(result.isObjectiveCompleted());
    }
}