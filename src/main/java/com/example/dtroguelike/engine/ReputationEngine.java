package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.ClubState;
import com.example.dtroguelike.domain.manager.Manager;
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
        if (career.getCurrentSeason() == null) {
            return;
        }
        Manager manager = career.getManager();
        SeasonStats stats = career.getCurrentSeason().getStats();

        if (stats.getMatchesPlayed() == 0) {
            return;
        }

        double winRate = (double) stats.getWins() / stats.getMatchesPlayed();
        if (winRate >= 0.6) {
            manager.addReputation(5);
        } else if (winRate <= 0.25) {
            manager.addReputation(-5);
        }
    }

    /** Pequeño ajuste de reputacion al ser despedido. */
    public void applyFiringPenalty(Career career) {
        career.getManager().addReputation(-8);
    }

    /** Pequeño ajuste de seguridad laboral segun la forma reciente del equipo. */
    public void adjustJobSecurity(ClubState clubState) {
        if (clubState == null) {
            return;
        }
        if (clubState.getForm() < 30) {
            clubState.addJobSecurity(-10);
        } else if (clubState.getForm() > 70) {
            clubState.addJobSecurity(5);
        }
    }
}
