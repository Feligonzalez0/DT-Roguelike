package com.example.dtroguelike.domain.club;

import com.example.dtroguelike.domain.common.GameConstants;

/**
 * Estado dinamico de un club durante una carrera: separa los datos
 * permanentes del club ({@link Club}) de todo lo que cambia partido a
 * partido / temporada a temporada.
 */
public class ClubState {

    private long budget;
    private int morale;
    private int boardRelation;
    private int fansRelation;
    private int playersRelation;
    private int pressRelation;
    private int form;
    private int idolatry;
    private int jobSecurity;

    public ClubState(long budget) {
        this.budget = budget;
        this.morale = GameConstants.DEFAULT_MORALE;
        this.boardRelation = GameConstants.DEFAULT_RELATION;
        this.fansRelation = GameConstants.DEFAULT_RELATION;
        this.playersRelation = GameConstants.DEFAULT_RELATION;
        this.pressRelation = GameConstants.DEFAULT_RELATION;
        this.form = GameConstants.DEFAULT_FORM;
        this.idolatry = GameConstants.DEFAULT_IDOLATRY;
        this.jobSecurity = GameConstants.DEFAULT_JOB_SECURITY;
    }

    public static ClubState initial() {
        return new ClubState(GameConstants.DEFAULT_CLUB_BUDGET);
    }

    private int clampRelation(int value) {
        return GameConstants.clamp(value, GameConstants.MIN_RELATION, GameConstants.MAX_RELATION);
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long budget) {
        this.budget = Math.max(0, budget);
    }

    public void addBudget(long delta) {
        setBudget(this.budget + delta);
    }

    public int getMorale() {
        return morale;
    }

    public void setMorale(int morale) {
        this.morale = GameConstants.clamp(morale, GameConstants.MIN_MORALE, GameConstants.MAX_MORALE);
    }

    public void addMorale(int delta) {
        setMorale(this.morale + delta);
    }

    public int getBoardRelation() {
        return boardRelation;
    }

    public void setBoardRelation(int boardRelation) {
        this.boardRelation = clampRelation(boardRelation);
    }

    public void addBoardRelation(int delta) {
        setBoardRelation(this.boardRelation + delta);
    }

    public int getFansRelation() {
        return fansRelation;
    }

    public void setFansRelation(int fansRelation) {
        this.fansRelation = clampRelation(fansRelation);
    }

    public void addFansRelation(int delta) {
        setFansRelation(this.fansRelation + delta);
    }

    public int getPlayersRelation() {
        return playersRelation;
    }

    public void setPlayersRelation(int playersRelation) {
        this.playersRelation = clampRelation(playersRelation);
    }

    public void addPlayersRelation(int delta) {
        setPlayersRelation(this.playersRelation + delta);
    }

    public int getPressRelation() {
        return pressRelation;
    }

    public void setPressRelation(int pressRelation) {
        this.pressRelation = clampRelation(pressRelation);
    }

    public void addPressRelation(int delta) {
        setPressRelation(this.pressRelation + delta);
    }

    public int getForm() {
        return form;
    }

    public void setForm(int form) {
        this.form = GameConstants.clamp(form, GameConstants.MIN_FORM, GameConstants.MAX_FORM);
    }

    public void addForm(int delta) {
        setForm(this.form + delta);
    }

    public int getIdolatry() {
        return idolatry;
    }

    public void setIdolatry(int idolatry) {
        this.idolatry = GameConstants.clamp(idolatry, GameConstants.MIN_IDOLATRY, GameConstants.MAX_IDOLATRY);
    }

    public void addIdolatry(int delta) {
        setIdolatry(this.idolatry + delta);
    }

    public int getJobSecurity() {
        return jobSecurity;
    }

    public void setJobSecurity(int jobSecurity) {
        this.jobSecurity = GameConstants.clamp(jobSecurity, GameConstants.MIN_JOB_SECURITY, GameConstants.MAX_JOB_SECURITY);
    }

    public void addJobSecurity(int delta) {
        setJobSecurity(this.jobSecurity + delta);
    }
}
