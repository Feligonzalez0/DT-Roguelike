package com.example.dtroguelike.infrastructure.data;

import java.util.List;

/** DTO plano que refleja la forma de cada entrada en events.json. */
public class EventDto {
    public String id;
    public String title;
    public String description;
    public String type;
    public List<EventConditionDto> conditions;
    public List<EventOptionDto> options;
}
