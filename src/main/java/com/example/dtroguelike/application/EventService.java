package com.example.dtroguelike.application;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.event.Event;
import com.example.dtroguelike.domain.event.EventOption;
import com.example.dtroguelike.engine.DecisionResolver;
import com.example.dtroguelike.engine.DecisionResult;
import com.example.dtroguelike.engine.EventEngine;
import com.example.dtroguelike.infrastructure.repository.CareerRepository;

/**
 * Caso de uso: generar el proximo evento y resolver la decision que el
 * jugador tome sobre el.
 */
public class EventService {

    private final EventEngine eventEngine;
    private final DecisionResolver decisionResolver;
    private final CareerRepository careerRepository;

    public EventService(EventEngine eventEngine, DecisionResolver decisionResolver,
                         CareerRepository careerRepository) {
        this.eventEngine = eventEngine;
        this.decisionResolver = decisionResolver;
        this.careerRepository = careerRepository;
    }

    public Event generateEvent(Career career) {
        return eventEngine.generateEvent(career);
    }

    public DecisionResult resolveDecision(Career career, Event event, EventOption option) {
        DecisionResult result = decisionResolver.resolve(career, event, option);
        careerRepository.save(career);
        return result;
    }
}
