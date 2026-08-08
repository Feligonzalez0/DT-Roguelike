package com.example.dtroguelike.domain.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Evento narrativo que se le presenta al jugador durante la carrera,
 * con una o mas {@link EventOption} para elegir.
 */
public class Event {

    private final String id;
    private final String title;
    private final String description;
    private final EventType type;
    private final List<EventCondition> conditions;
    private final List<EventOption> options;

    public Event(String id, String title, String description, EventType type,
                 List<EventCondition> conditions, List<EventOption> options) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.conditions = conditions != null ? conditions : new ArrayList<>();
        this.options = options != null ? options : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public EventType getType() {
        return type;
    }

    public List<EventCondition> getConditions() {
        return conditions;
    }

    public List<EventOption> getOptions() {
        return options;
    }
}
