package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.common.GameConstants;
import com.example.dtroguelike.domain.event.Effect;
import com.example.dtroguelike.domain.event.Event;
import com.example.dtroguelike.domain.event.EventOption;
import com.example.dtroguelike.domain.event.EventType;
import com.example.dtroguelike.domain.event.Outcome;
import com.example.dtroguelike.domain.manager.ManagerAttributeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

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

    /**
     * Genera el evento de PRESEASON (ISSUE 11): le ofrece al jugador
     * {@link GameConstants#PRESEASON_OPTION_COUNT} atributos de Manager
     * elegidos al azar entre los 6 disponibles, cada uno con una opcion
     * que, al elegirse, aplica +delta sobre ese atributo.
     *
     * A diferencia de {@link #generateEvent(Career)}, este evento no se
     * elige de {@code availableEvents} (el pool cargado desde JSON): se
     * arma proceduralmente, ya que es un evento estructural del ciclo de
     * temporada y no un evento narrativo aleatorio.
     */
    public Event generatePreseasonEvent() {
        List<ManagerAttributeType> pool = new ArrayList<>(List.of(ManagerAttributeType.values()));
        Collections.shuffle(pool, random);

        int optionCount = Math.min(GameConstants.PRESEASON_OPTION_COUNT, pool.size());
        List<ManagerAttributeType> chosenAttributes = pool.subList(0, optionCount);

        int deltaRange = GameConstants.PRESEASON_ATTRIBUTE_MAX_DELTA - GameConstants.PRESEASON_ATTRIBUTE_MIN_DELTA;
        int delta = GameConstants.PRESEASON_ATTRIBUTE_MIN_DELTA + (deltaRange > 0 ? random.nextInt(deltaRange + 1) : 0);

        List<EventOption> options = new ArrayList<>();
        for (ManagerAttributeType attribute : chosenAttributes) {
            String optionId = attribute.name().toLowerCase(Locale.ROOT);
            String description = "Mejorar " + attribute.getDisplayName();
            Outcome outcome = Outcome.of(
                    attribute.getDisplayName() + " mejorada.",
                    new Effect(attribute.getEffectType(), delta)
            );
            // successChance = 1.0: la decision del jugador siempre se
            // aplica, no hay narrativa de exito/fracaso en este evento.
            options.add(new EventOption(optionId, description, 1.0, List.of(), outcome, outcome));
        }

        return new Event(
                "preseason-development-" + UUID.randomUUID(),
                "Nueva temporada",
                "Después de analizar la temporada anterior, decidís en qué aspecto trabajar durante la nueva temporada.",
                EventType.PRESEASON_DEVELOPMENT,
                List.of(),
                options
        );
    }
}
