package com.example.dtroguelike.infrastructure.data;

import java.util.List;

/** DTO plano para una EventOption dentro de events.json. */
public class EventOptionDto {
    public String id;
    public String description;
    public double successChance;
    public List<EventConditionDto> requirements;
    public OutcomeDto successOutcome;
    public OutcomeDto failureOutcome;
}
