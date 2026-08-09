package com.example.dtroguelike.domain.common;

/**
 * Fase actual del juego, utilizada por la capa web para decidir que
 * pantalla mostrarle al jugador.
 */
public enum GamePhase {
    MANAGER_CREATION,
    CLUB_SELECTION,
    PRESEASON,
    TRANSFER_WINDOW,
    EVENT,
    MATCH,
    MATCH_DECISION,
    REGULAR_SEASON,
    END_OF_SEASON,
    SUMMARY,
    CLUB_SELECTION_AFTER_DEPARTURE,
    CAREER_FINISHED
}
