package com.example.dtroguelike;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;
import com.example.dtroguelike.domain.event.*;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.manager.ManagerStyle;
import com.example.dtroguelike.engine.CareerEngine;
import com.example.dtroguelike.engine.DecisionResolver;
import com.example.dtroguelike.engine.DecisionResult;
import com.example.dtroguelike.engine.MatchSimulator;
import com.example.dtroguelike.engine.ProgressionEngine;
import com.example.dtroguelike.engine.ReputationEngine;
import com.example.dtroguelike.engine.SeasonSimulator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class EventDecisionTest {

    private Career buildActiveCareer() {
        Manager manager = new Manager("DT", 40, "Argentina", ManagerStyle.MANAGER);
        MatchSimulator matchSimulator = new MatchSimulator(new Random(1));
        SeasonSimulator seasonSimulator = new SeasonSimulator(matchSimulator, new Random(1));
        CareerEngine engine = new CareerEngine(seasonSimulator, new ReputationEngine(), new ProgressionEngine());
        Career career = engine.startCareer(manager);
        Club club = new Club("club", "Club", "Argentina", "Liga", 50, new TeamStrength(60, 60, 60));
        engine.assignClub(career, club);
        return career;
    }

    @Test
    void resolvesSuccessfulDecisionAndAppliesEffects() {
        Career career = buildActiveCareer();
        int reputationBefore = career.getManager().getReputation();

        Outcome successOutcome = Outcome.of("Exito", new Effect(EffectType.MANAGER_REPUTATION, 5));
        Outcome failureOutcome = Outcome.of("Fracaso", new Effect(EffectType.MANAGER_REPUTATION, -5));
        EventOption option = new EventOption("opt1", "Hacer algo", 1.0, List.of(), successOutcome, failureOutcome);
        Event event = new Event("evt1", "Titulo", "Desc", EventType.LOCKER_ROOM, List.of(), List.of(option));

        DecisionResolver resolver = new DecisionResolver(new Random(1));
        DecisionResult result = resolver.resolve(career, event, option);

        assertTrue(result.isRequirementsMet());
        assertTrue(result.isSuccess());
        assertEquals(reputationBefore + 5, career.getManager().getReputation());
    }

    @Test
    void blocksOptionWhenRequirementsNotMet() {
        Career career = buildActiveCareer();

        EventCondition requiresHighNegotiation =
                new EventCondition(ConditionType.MANAGER_NEGOTIATION, ComparisonOperator.GREATER_OR_EQUAL, 999);
        Outcome successOutcome = Outcome.of("Exito", new Effect(EffectType.MANAGER_REPUTATION, 5));
        EventOption option = new EventOption("opt1", "Negociar", 1.0,
                List.of(requiresHighNegotiation), successOutcome, successOutcome);
        Event event = new Event("evt1", "Titulo", "Desc", EventType.BOARD, List.of(), List.of(option));

        DecisionResolver resolver = new DecisionResolver();
        DecisionResult result = resolver.resolve(career, event, option);

        assertFalse(result.isRequirementsMet());
    }
}
