package com.example.dtroguelike.domain.event;

/**
 * Estado de resolucion de un {@link Event} concreto que quedo pendiente
 * de decision del jugador (por ejemplo, el evento de pretemporada).
 *
 * Permite garantizar que, una vez que el jugador toma una decision, el
 * mismo evento no pueda volver a resolverse (ni por un doble submit del
 * formulario ni por refrescar la pagina).
 */
public enum EventStatus {
    PENDING,
    RESOLVED
}
