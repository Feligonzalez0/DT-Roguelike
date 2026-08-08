package com.example.dtroguelike.domain.common;

/**
 * Constantes globales del juego. Centralizamos aca todos los limites y
 * valores por defecto para evitar numeros magicos repartidos por el codigo.
 */
public final class GameConstants {

    private GameConstants() {
        // clase de constantes, no instanciable
    }

    // --- Atributos del Manager (0-100) ---
    public static final int MIN_ATTRIBUTE = 0;
    public static final int MAX_ATTRIBUTE = 100;
    public static final int DEFAULT_MANAGER_ATTRIBUTE = 50;

    // --- Reputacion (0-100) ---
    public static final int MIN_REPUTATION = 0;
    public static final int MAX_REPUTATION = 100;
    public static final int DEFAULT_MANAGER_REPUTATION = 30;

    // --- Relaciones de club (0-100) ---
    public static final int MIN_RELATION = 0;
    public static final int MAX_RELATION = 100;
    public static final int DEFAULT_RELATION = 50;

    // --- Moral / Forma / Idolatria / Seguridad laboral (0-100) ---
    public static final int MIN_MORALE = 0;
    public static final int MAX_MORALE = 100;
    public static final int DEFAULT_MORALE = 70;

    public static final int MIN_FORM = 0;
    public static final int MAX_FORM = 100;
    public static final int DEFAULT_FORM = 70;

    public static final int MIN_IDOLATRY = 0;
    public static final int MAX_IDOLATRY = 100;
    public static final int DEFAULT_IDOLATRY = 0;

    public static final int MIN_JOB_SECURITY = 0;
    public static final int MAX_JOB_SECURITY = 100;
    public static final int DEFAULT_JOB_SECURITY = 60;

    // --- Fuerza de equipo (0-100) ---
    public static final int MIN_TEAM_STRENGTH = 0;
    public static final int MAX_TEAM_STRENGTH = 100;

    // --- Manager: valores iniciales ---
    public static final int DEFAULT_MANAGER_AGE = 35;
    public static final int RETIREMENT_AGE = 75;

    // --- Club: valores iniciales ---
    public static final long DEFAULT_CLUB_BUDGET = 1_000_000L;

    // --- Ofertas de club ---
    public static final int MIN_INITIAL_OFFERS = 3;
    public static final int MAX_INITIAL_OFFERS = 5;

    /**
     * Aplica un "clamp" generico entre un minimo y un maximo.
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
