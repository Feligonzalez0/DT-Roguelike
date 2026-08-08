package com.example.dtroguelike.domain.league;

import com.example.dtroguelike.domain.club.Club;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una liga/torneo con sus clubes asociados.
 * Para el MVP solo existe la Liga Profesional Argentina, pero la
 * estructura permite agregar mas ligas sin cambios de diseño.
 */
public class League {

    private final String id;
    private final String name;
    private final String country;
    private final int level; // 1 = primera division, 2 = segunda, etc.
    private final int strength; // 0-100, nivel competitivo relativo de la liga
    private final List<Club> clubs = new ArrayList<>();

    public League(String id, String name, String country, int level, int strength) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.level = level;
        this.strength = strength;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public int getLevel() {
        return level;
    }

    public int getStrength() {
        return strength;
    }

    public List<Club> getClubs() {
        return clubs;
    }

    public void addClub(Club club) {
        clubs.add(club);
    }
}
