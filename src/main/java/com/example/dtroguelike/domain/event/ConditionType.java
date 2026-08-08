package com.example.dtroguelike.domain.event;

/**
 * Que variable del estado del juego evalua una {@link EventCondition}.
 * Reutiliza el mismo universo de variables que {@link EffectType} para
 * mantener el sistema simetrico y facil de entender.
 */
public enum ConditionType {
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
