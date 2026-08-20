package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.ClubState;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.domain.season.SeasonStats;

/**
 * Encargado de ajustar la reputacion del Manager segun lo que va
 * sucediendo en su carrera (resultados, campeonatos, despidos, etc).
 * Para el MVP solo se implementan ajustes muy simples; el calculo
 * detallado se agregara mas adelante.
 */
public class ReputationEngine {

    /**
     * Ajusta la reputacion del DT en funcion del resultado de la
     * temporada recien finalizada. Regla simple: buena posicion en la
     * liga sube reputacion, mala posicion la baja.
     *
     * TODO: incorporar campeonatos, ascensos, descensos y contexto del
     * club (expectativas) al calculo.
     */
    public void updateAfterSeason(Career career) {
        if (career == null || career.getCurrentSeason() == null
                || career.getManager() == null || career.getCurrentClub() == null) {
            return;
        }

        Season season = career.getCurrentSeason();
        SeasonStats stats = season.getStats();

        if (stats == null || stats.getMatchesPlayed() == 0) {
            return;
        }

        var objective = season.getObjective();
        var objectiveResult = season.getObjectiveResult();

        if (objective == null || objectiveResult == null) {
            return;
        }

        int finalPosition = objectiveResult.getFinalPosition();
        int targetPosition = objective.getTargetPosition();

        int delta = 0;

        /*
        * 1. Resultado respecto al objetivo.
        *
        * Cumplir exactamente el objetivo da una pequeña recompensa.
        * Cada posición por encima mejora la recompensa.
        * Cada posición por debajo penaliza progresivamente.
        */
        int positionDifference = targetPosition - finalPosition;

        if (positionDifference >= 0) {
            delta += 3 + Math.min(positionDifference, 4);
        } else {
            delta += Math.max(-8, positionDifference * 2);
        }

        /*
        * 2. Rendimiento deportivo.
        *
        * Se premian temporadas claramente dominantes y se penalizan
        * temporadas muy malas. No se suma nada en el rango intermedio
        * para evitar que la reputación dependa demasiado de una sola métrica.
        */
        double winRate = (double) stats.getWins() / stats.getMatchesPlayed();

        if (winRate >= 0.65) {
            delta += 3;
        } else if (winRate >= 0.55) {
            delta += 2;
        } else if (winRate <= 0.25) {
            delta -= 3;
        } else if (winRate <= 0.35) {
            delta -= 2;
        }

        /*
        * 3. Campeonato.
        *
        * Ganar la liga siempre es importante.
        * Si además se superaron ampliamente las expectativas,
        * se añade un bonus adicional por tratarse de una hazaña.
        */
        if (finalPosition == 1) {
            delta += 4;

            if (targetPosition >= 8) {
                delta += 3;
            } else if (targetPosition >= 5) {
                delta += 2;
            }
        }

        /*
        * Evitamos cambios desproporcionados en una sola temporada.
        */
        delta = Math.max(-12, Math.min(12, delta));

        career.getManager().addReputation(delta);
        career.getCurrentSeason().setReputationChange(delta);

        adjustJobSecurity(career, delta);
    }

    /** Pequeño ajuste de reputacion al ser despedido. */
    public void applyFiringPenalty(Career career) {
        career.getManager().addReputation(-8);
    }

    /**
     * Ajusta la seguridad del puesto según el rendimiento de la temporada.
     *
     * Una temporada positiva aumenta la seguridad, mientras que una
     * temporada negativa la reduce. El ajuste es proporcional al cambio
     * de reputación, pero más conservador para evitar que la seguridad
     * del puesto cambie demasiado rápido.
     */
    private void adjustJobSecurity(Career career, int reputationChange) {
        if (career == null || career.getCurrentClubState() == null) {
            return;
        }

        ClubState clubState = career.getCurrentClubState();

        int securityChange;

        if (reputationChange >= 8) {
            securityChange = 8;
        } else if (reputationChange >= 4) {
            securityChange = 5;
        } else if (reputationChange > 0) {
            securityChange = 2;
        } else if (reputationChange <= -8) {
            securityChange = -8;
        } else if (reputationChange <= -4) {
            securityChange = -5;
        } else if (reputationChange < 0) {
            securityChange = -2;
        } else {
            securityChange = 0;
        }

        clubState.addJobSecurity(securityChange);
    }
}
