package com.example.dtroguelike.domain.club;

import com.example.dtroguelike.domain.common.GameConstants;

/**
 * Fuerza deportiva de un equipo, dividida en tres lineas (0-100 cada una).
 */
public class TeamStrength {

    private int attack;
    private int midfield;
    private int defense;

    public TeamStrength(int attack, int midfield, int defense) {
        this.attack = clamp(attack);
        this.midfield = clamp(midfield);
        this.defense = clamp(defense);
    }

    private int clamp(int value) {
        return GameConstants.clamp(value, GameConstants.MIN_TEAM_STRENGTH, GameConstants.MAX_TEAM_STRENGTH);
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = clamp(attack);
    }

    public int getMidfield() {
        return midfield;
    }

    public void setMidfield(int midfield) {
        this.midfield = clamp(midfield);
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = clamp(defense);
    }

    /**
     * Promedio simple de las tres lineas, util como resumen rapido de
     * fuerza global del equipo (por ejemplo para simulacion de partidos).
     */
    public double overall() {
        return (attack + midfield + defense) / 3.0;
    }
}
