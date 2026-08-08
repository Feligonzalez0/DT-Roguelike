package com.example.dtroguelike.infrastructure.data;

import com.example.dtroguelike.domain.event.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga los eventos narrativos desde {@code data/events.json} y los
 * mapea a las clases de dominio del sistema de eventos.
 */
public class EventDataLoader {

    private final Gson gson = new Gson();

    public List<Event> loadEvents(String resourcePath) {
        List<Event> events = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("No se encontro el recurso: " + resourcePath);
            }
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<EventDto>>() {}.getType();
            List<EventDto> dtos = gson.fromJson(reader, listType);
            if (dtos == null) {
                dtos = new ArrayList<>();
            }
            for (EventDto dto : dtos) {
                events.add(toDomain(dto));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error cargando eventos desde " + resourcePath, e);
        }
        return events;
    }

    private Event toDomain(EventDto dto) {
        EventType type = EventType.valueOf(dto.type);
        List<EventCondition> conditions = toConditions(dto.conditions);
        List<EventOption> options = new ArrayList<>();
        if (dto.options != null) {
            for (EventOptionDto optionDto : dto.options) {
                options.add(toOption(optionDto));
            }
        }
        return new Event(dto.id, dto.title, dto.description, type, conditions, options);
    }

    private EventOption toOption(EventOptionDto dto) {
        List<EventCondition> requirements = toConditions(dto.requirements);
        Outcome success = toOutcome(dto.successOutcome);
        Outcome failure = toOutcome(dto.failureOutcome);
        return new EventOption(dto.id, dto.description, dto.successChance, requirements, success, failure);
    }

    private Outcome toOutcome(OutcomeDto dto) {
        if (dto == null) {
            return null;
        }
        List<Effect> effects = new ArrayList<>();
        if (dto.effects != null) {
            for (EffectDto effectDto : dto.effects) {
                effects.add(new Effect(EffectType.valueOf(effectDto.type), effectDto.amount));
            }
        }
        return new Outcome(dto.description, effects);
    }

    private List<EventCondition> toConditions(List<EventConditionDto> dtos) {
        List<EventCondition> conditions = new ArrayList<>();
        if (dtos != null) {
            for (EventConditionDto dto : dtos) {
                conditions.add(new EventCondition(
                        ConditionType.valueOf(dto.type),
                        ComparisonOperator.valueOf(dto.operator),
                        dto.value));
            }
        }
        return conditions;
    }
}
