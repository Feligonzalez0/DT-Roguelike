package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.ClubState;
import com.example.dtroguelike.domain.event.*;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.manager.ManagerAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Resuelve la decision del jugador frente a un {@link Event}:
 * 1) comprueba requisitos, 2) calcula exito/fracaso, 3) aplica efectos,
 * 4) devuelve un {@link DecisionResult} listo para la interfaz.
 */
public class DecisionResolver {

    private final Random random;

    public DecisionResolver() {
        this(new Random());
    }

    public DecisionResolver(Random random) {
        this.random = random;
    }

    public DecisionResult resolve(Career career, Event event, EventOption option) {
        if (!requirementsAreMet(career, option)) {
            return DecisionResult.requirementsNotMet(
                    "No se cumplen los requisitos para elegir: " + option.getDescription());
        }

        boolean success = random.nextDouble() < option.getSuccessChance();
        Outcome outcome = success ? option.getSuccessOutcome() : option.getFailureOutcome();

        List<EffectResult> applied = new ArrayList<>();
        if (outcome != null) {
            for (Effect effect : outcome.getEffects()) {
                applyEffect(career, effect);
                applied.add(new EffectResult(effect.getType(), effect.getAmount()));
            }
        }

        String description = outcome != null ? outcome.getDescription() : "";
        return new DecisionResult(true, success, description, applied);
    }

    private boolean requirementsAreMet(Career career, EventOption option) {
        for (EventCondition condition : option.getRequirements()) {
            int actual = readConditionValue(career, condition.getType());
            if (!condition.isSatisfiedBy(actual)) {
                return false;
            }
        }
        return true;
    }

    private int readConditionValue(Career career, ConditionType type) {
        Manager manager = career.getManager();
        ManagerAttributes attributes = manager.getAttributes();
        ClubState clubState = career.getCurrentClubState();

        return switch (type) {
            case MANAGER_REPUTATION -> manager.getReputation();
            case MANAGER_TACTICS -> attributes.getTactics();
            case MANAGER_LEADERSHIP -> attributes.getLeadership();
            case MANAGER_MANAGEMENT -> attributes.getManagement();
            case MANAGER_NEGOTIATION -> attributes.getNegotiation();
            case MANAGER_YOUTH_DEVELOPMENT -> attributes.getYouthDevelopment();
            case MANAGER_MOTIVATION -> attributes.getMotivation();
            case CLUB_MORALE -> clubState != null ? clubState.getMorale() : 0;
            case CLUB_FORM -> clubState != null ? clubState.getForm() : 0;
            case CLUB_BUDGET -> clubState != null ? (int) Math.min(Integer.MAX_VALUE, clubState.getBudget()) : 0;
            case BOARD_RELATION -> clubState != null ? clubState.getBoardRelation() : 0;
            case FANS_RELATION -> clubState != null ? clubState.getFansRelation() : 0;
            case PLAYERS_RELATION -> clubState != null ? clubState.getPlayersRelation() : 0;
            case MEDIA_RELATION -> clubState != null ? clubState.getPressRelation() : 0;
            case CLUB_IDOLATRY -> clubState != null ? clubState.getIdolatry() : 0;
            case JOB_SECURITY -> clubState != null ? clubState.getJobSecurity() : 0;
        };
    }

    private void applyEffect(Career career, Effect effect) {
        Manager manager = career.getManager();
        ManagerAttributes attributes = manager.getAttributes();
        ClubState clubState = career.getCurrentClubState();
        int amount = effect.getAmount();

        switch (effect.getType()) {
            case MANAGER_REPUTATION -> manager.addReputation(amount);
            case MANAGER_TACTICS -> attributes.addTactics(amount);
            case MANAGER_LEADERSHIP -> attributes.addLeadership(amount);
            case MANAGER_MANAGEMENT -> attributes.addManagement(amount);
            case MANAGER_NEGOTIATION -> attributes.addNegotiation(amount);
            case MANAGER_YOUTH_DEVELOPMENT -> attributes.addYouthDevelopment(amount);
            case MANAGER_MOTIVATION -> attributes.addMotivation(amount);
            case CLUB_MORALE -> ifClubPresent(clubState, cs -> cs.addMorale(amount));
            case CLUB_FORM -> ifClubPresent(clubState, cs -> cs.addForm(amount));
            case CLUB_BUDGET -> ifClubPresent(clubState, cs -> cs.addBudget(amount));
            case BOARD_RELATION -> ifClubPresent(clubState, cs -> cs.addBoardRelation(amount));
            case FANS_RELATION -> ifClubPresent(clubState, cs -> cs.addFansRelation(amount));
            case PLAYERS_RELATION -> ifClubPresent(clubState, cs -> cs.addPlayersRelation(amount));
            case MEDIA_RELATION -> ifClubPresent(clubState, cs -> cs.addPressRelation(amount));
            case CLUB_IDOLATRY -> ifClubPresent(clubState, cs -> cs.addIdolatry(amount));
            case JOB_SECURITY -> ifClubPresent(clubState, cs -> cs.addJobSecurity(amount));
        }
    }

    private void ifClubPresent(ClubState clubState, java.util.function.Consumer<ClubState> action) {
        if (clubState != null) {
            action.accept(clubState);
        }
    }
}
