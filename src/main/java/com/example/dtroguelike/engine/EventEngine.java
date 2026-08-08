package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.event.Event;

import java.util.List;
import java.util.Random;

/**
 * Encargado de seleccionar el proximo evento a mostrarle al jugador.
 * Para el MVP simplemente elige un evento al azar de la coleccion
 * disponible. Mas adelante deberia filtrar por condiciones y evitar
 * repetir eventos recientes.
 */
public class EventEngine {

    private final List<Event> availableEvents;
    private final Random random;

    public EventEngine(List<Event> availableEvents) {
        this(availableEvents, new Random());
    }

    public EventEngine(List<Event> availableEvents, Random random) {
        this.availableEvents = availableEvents;
        this.random = random;
    }

    /**
     * Selecciona un evento valido para el estado actual de la carrera.
     * TODO: filtrar eventos segun sus {@code conditions} contra el estado
     * real de la carrera, y evitar repetir eventos recientes.
     */
    public Event generateEvent(Career career) {
        if (availableEvents == null || availableEvents.isEmpty()) {
            return null;
        }
        int index = random.nextInt(availableEvents.size());
        return availableEvents.get(index);
    }

    public List<Event> getAvailableEvents() {
        return availableEvents;
    }
}
