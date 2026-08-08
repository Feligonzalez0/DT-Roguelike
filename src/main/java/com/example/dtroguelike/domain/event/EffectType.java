package com.example.dtroguelike.domain.event;

/**
 * Tipo de cambio que un {@link Effect} puede aplicar sobre el estado del
 * juego (Manager, Club o carrera en general).
 */
public enum EffectType {
    MANAGER_REPUTATION,

    MANAGER_TACTICS,
    MANAGER_LEADERSHIP,
    MANAGER_MANAGEMENT,
    MANAGER_NEGOTIATION,
    MANAGER_YOUTH_DEVELOPMENT,
    MANAGER_MOTIVATION,

    CLUB_MORALE,
    CLUB_FORM,
    CLUB_BUDGET,

    BOARD_RELATION,
    FANS_RELATION,
    PLAYERS_RELATION,
    MEDIA_RELATION,

    CLUB_IDOLATRY,

    JOB_SECURITY
}
