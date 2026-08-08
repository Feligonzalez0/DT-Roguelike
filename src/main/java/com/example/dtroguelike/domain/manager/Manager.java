package com.example.dtroguelike.domain.manager;

import com.example.dtroguelike.domain.common.GameConstants;

import java.util.UUID;

/**
 * Representa al Director Tecnico controlado por el jugador.
 */
public class Manager {

    private final String id;
    private String name;
    private int age;
    private String nationality;
    private ManagerStyle style;
    private ManagerAttributes attributes;
    private int reputation;
    private final ManagerStats stats;

    public Manager(String name, int age, String nationality, ManagerStyle style) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.age = age;
        this.nationality = nationality;
        this.style = style;
        this.attributes = style.initialAttributes();
        this.reputation = GameConstants.DEFAULT_MANAGER_REPUTATION;
        this.stats = new ManagerStats();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void incrementAge() {
        this.age++;
    }

    public boolean isAtRetirementAge() {
        return this.age >= GameConstants.RETIREMENT_AGE;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public ManagerStyle getStyle() {
        return style;
    }

    public void setStyle(ManagerStyle style) {
        this.style = style;
    }

    public ManagerAttributes getAttributes() {
        return attributes;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = GameConstants.clamp(reputation, GameConstants.MIN_REPUTATION, GameConstants.MAX_REPUTATION);
    }

    public void addReputation(int delta) {
        setReputation(this.reputation + delta);
    }

    public ManagerStats getStats() {
        return stats;
    }
}
