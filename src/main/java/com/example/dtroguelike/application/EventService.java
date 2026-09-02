package com.example.dtroguelike.application;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.event.Event;
import com.example.dtroguelike.domain.event.EventOption;
import com.example.dtroguelike.domain.event.EventStatus;
import com.example.dtroguelike.domain.manager.ManagerAttributeType;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.domain.season.SeasonPhase;
import com.example.dtroguelike.engine.DecisionResolver;
import com.example.dtroguelike.engine.DecisionResult;
import com.example.dtroguelike.engine.EventEngine;
import com.example.dtroguelike.infrastructure.repository.CareerRepository;

import java.util.NoSuchElementException;

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

    // =========================================================
    // EVENTO DE PRETEMPORADA (ISSUE 11)
    // =========================================================

    /**
     * Devuelve el evento de pretemporada de la temporada actual,
     * generandolo la primera vez que se pide. Llamadas posteriores
     * (por ejemplo, refrescar la pagina) devuelven siempre el mismo
     * evento ya generado en vez de crear uno nuevo.
     */
    public Event getOrCreatePreseasonEvent(Career career) {
        Season season = requireSeasonInPreseason(career);

        if (season.getPreseasonEvent() == null) {
            Event event = eventEngine.generatePreseasonEvent(career);
            season.setPreseasonEvent(event);
            season.setPreseasonEventStatus(EventStatus.PENDING);
            careerRepository.save(career);
        }

        return season.getPreseasonEvent();
    }

    /**
     * Resuelve la opcion elegida por el jugador para el evento de
     * pretemporada: aplica +delta sobre el atributo correspondiente y
     * marca el evento como RESOLVED.
     *
     * Es idempotente frente a un doble submit: si el evento ya fue
     * resuelto (o la temporada ya no esta en PRESEASON), rechaza la
     * resolucion en lugar de volver a aplicar el efecto.
     */
    public PreseasonOutcome resolvePreseasonDecision(Career career, String optionId) {
        Season season = requireSeasonInPreseason(career);

        if (season.getPreseasonEventStatus() == EventStatus.RESOLVED) {
            throw new IllegalStateException("El evento de pretemporada ya fue resuelto.");
        }

        Event event = season.getPreseasonEvent();
        if (event == null) {
            throw new IllegalStateException("Todavia no se genero el evento de pretemporada.");
        }

        EventOption option = event.getOptions().stream()
                .filter(candidate -> candidate.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Opcion de pretemporada invalida: " + optionId));

        ManagerAttributeType attribute = ManagerAttributeType.fromEffectType(
                option.getSuccessOutcome().getEffects().get(0).getType());
        int before = attribute.readValue(career.getManager().getAttributes());

        DecisionResult result = decisionResolver.resolve(career, event, option);

        // PENDING -> RESOLVED. A partir de aca cualquier otro intento de
        // resolver este mismo evento sera rechazado mas arriba.
        season.setPreseasonEventStatus(EventStatus.RESOLVED);

        int after = attribute.readValue(career.getManager().getAttributes());

        careerRepository.save(career);

        return new PreseasonOutcome(attribute, before, after, result);
    }

    private Season requireSeasonInPreseason(Career career) {
        Season season = career.getCurrentSeason();
        if (season == null) {
            throw new IllegalStateException("No hay una temporada activa.");
        }
        if (season.getPhase() != SeasonPhase.PRESEASON) {
            throw new IllegalStateException(
                    "La temporada no esta en fase PRESEASON (fase actual: " + season.getPhase() + ").");
        }
        return season;
    }
}
