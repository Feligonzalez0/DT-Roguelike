package com.example.dtroguelike.domain.manager;

import com.example.dtroguelike.domain.common.GameConstants;

/**
 * Atributos numericos (0-100) de un Director Tecnico.
 * Separada de {@link Manager} para mantener el modelo ordenado y facil
 * de extender (por ejemplo, agregar nuevos atributos en el futuro).
 */
public class ManagerAttributes {

    private int tactics;
    private int leadership;
    private int management;
    private int negotiation;
    private int youthDevelopment;
    private int motivation;

    public ManagerAttributes() {
        this(GameConstants.DEFAULT_MANAGER_ATTRIBUTE,
                GameConstants.DEFAULT_MANAGER_ATTRIBUTE,
                GameConstants.DEFAULT_MANAGER_ATTRIBUTE,
                GameConstants.DEFAULT_MANAGER_ATTRIBUTE,
                GameConstants.DEFAULT_MANAGER_ATTRIBUTE,
                GameConstants.DEFAULT_MANAGER_ATTRIBUTE);
    }

    public ManagerAttributes(int tactics, int leadership, int management,
                              int negotiation, int youthDevelopment, int motivation) {
        this.tactics = clamp(tactics);
        this.leadership = clamp(leadership);
        this.management = clamp(management);
        this.negotiation = clamp(negotiation);
        this.youthDevelopment = clamp(youthDevelopment);
        this.motivation = clamp(motivation);
    }

    public static ManagerAttributes defaults() {
        return new ManagerAttributes();
    }

    private int clamp(int value) {
        return GameConstants.clamp(value, GameConstants.MIN_ATTRIBUTE, GameConstants.MAX_ATTRIBUTE);
    }

    public int getTactics() {
        return tactics;
    }

    public void setTactics(int tactics) {
        this.tactics = clamp(tactics);
    }

    public void addTactics(int delta) {
        setTactics(this.tactics + delta);
    }

    public int getLeadership() {
        return leadership;
    }

    public void setLeadership(int leadership) {
        this.leadership = clamp(leadership);
    }

    public void addLeadership(int delta) {
        setLeadership(this.leadership + delta);
    }

    public int getManagement() {
        return management;
    }

    public void setManagement(int management) {
        this.management = clamp(management);
    }

    public void addManagement(int delta) {
        setManagement(this.management + delta);
    }

    public int getNegotiation() {
        return negotiation;
    }

    public void setNegotiation(int negotiation) {
        this.negotiation = clamp(negotiation);
    }

    public void addNegotiation(int delta) {
        setNegotiation(this.negotiation + delta);
    }

    public int getYouthDevelopment() {
        return youthDevelopment;
    }

    public void setYouthDevelopment(int youthDevelopment) {
        this.youthDevelopment = clamp(youthDevelopment);
    }

    public void addYouthDevelopment(int delta) {
        setYouthDevelopment(this.youthDevelopment + delta);
    }

    public int getMotivation() {
        return motivation;
    }

    public void setMotivation(int motivation) {
        this.motivation = clamp(motivation);
    }

    public void addMotivation(int delta) {
        setMotivation(this.motivation + delta);
    }
}
