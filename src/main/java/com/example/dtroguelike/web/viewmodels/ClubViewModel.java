package com.example.dtroguelike.web.viewmodels;

import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.ClubState;

/**
 * Representacion plana de un Club (datos permanentes + estado de
 * carrera) para las vistas Mustache.
 */
public class ClubViewModel {

    public final String id;
    public final String name;
    public final String country;
    public final String league;
    public final int reputation;
    public final int attack;
    public final int midfield;
    public final int defense;

    // Estado (puede ser null si el club todavia no fue tomado por el DT)
    public final Long budget;
    public final Integer morale;
    public final Integer boardRelation;
    public final Integer fansRelation;
    public final Integer playersRelation;
    public final Integer form;
    public final Integer idolatry;
    public final Integer jobSecurity;

    public ClubViewModel(Club club, ClubState state) {
        this.id = club.getId();
        this.name = club.getName();
        this.country = club.getCountry();
        this.league = club.getLeague();
        this.reputation = club.getReputation();
        this.attack = club.getStrength().getAttack();
        this.midfield = club.getStrength().getMidfield();
        this.defense = club.getStrength().getDefense();

        if (state != null) {
            this.budget = state.getBudget();
            this.morale = state.getMorale();
            this.boardRelation = state.getBoardRelation();
            this.fansRelation = state.getFansRelation();
            this.playersRelation = state.getPlayersRelation();
            this.form = state.getForm();
            this.idolatry = state.getIdolatry();
            this.jobSecurity = state.getJobSecurity();
        } else {
            this.budget = null;
            this.morale = null;
            this.boardRelation = null;
            this.fansRelation = null;
            this.playersRelation = null;
            this.form = null;
            this.idolatry = null;
            this.jobSecurity = null;
        }
    }
}
