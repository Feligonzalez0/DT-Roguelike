package com.example.dtroguelike.web.viewmodels;

import com.example.dtroguelike.domain.manager.Manager;

/**
 * Representacion plana del Manager para las vistas Mustache (que no
 * pueden invocar metodos con logica, solo leer campos/getters simples).
 */
public class ManagerViewModel {

    public final String name;
    public final int age;
    public final String nationality;
    public final String style;
    public final String styleDescription;
    public final int reputation;

    public final int tactics;
    public final int leadership;
    public final int management;
    public final int negotiation;
    public final int youthDevelopment;
    public final int motivation;

    public ManagerViewModel(Manager manager) {
        this.name = manager.getName();
        this.age = manager.getAge();
        this.nationality = manager.getNationality();
        this.style = manager.getStyle().getDisplayName();
        this.styleDescription = manager.getStyle().getDescription();
        this.reputation = manager.getReputation();

        this.tactics = manager.getAttributes().getTactics();
        this.leadership = manager.getAttributes().getLeadership();
        this.management = manager.getAttributes().getManagement();
        this.negotiation = manager.getAttributes().getNegotiation();
        this.youthDevelopment = manager.getAttributes().getYouthDevelopment();
        this.motivation = manager.getAttributes().getMotivation();
    }
}
