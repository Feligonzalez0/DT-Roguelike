package com.example.dtroguelike.web.controllers;

import com.example.dtroguelike.application.CareerService;
import com.example.dtroguelike.application.EventService;
import com.example.dtroguelike.application.PreseasonOutcome;
import com.example.dtroguelike.application.SeasonService;
import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.event.Event;
import com.example.dtroguelike.domain.event.EventStatus;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.domain.season.SeasonPhase;
import com.example.dtroguelike.web.viewmodels.PreseasonEventViewModel;
import com.example.dtroguelike.web.viewmodels.PreseasonOutcomeViewModel;

import java.util.Map;
import java.util.Optional;

/**
 * Controlador del evento de pretemporada (ISSUE 11): muestra la
 * decision de desarrollo al comenzar una temporada y, al resolverla,
 * avanza automaticamente de PRESEASON a TRANSFER_WINDOW.
 *
 * No contiene logica de negocio: delega en {@link EventService} (genera
 * y resuelve el evento) y en {@link SeasonService} (avance de fase).
 */
public class PreseasonController {

    private final CareerService careerService;
    private final EventService eventService;
    private final SeasonService seasonService;

    public PreseasonController(CareerService careerService, EventService eventService,
                                SeasonService seasonService) {
        this.careerService = careerService;
        this.eventService = eventService;
        this.seasonService = seasonService;
    }

    /**
     * True si hay un evento de pretemporada pendiente de resolucion
     * para la temporada actual. Se usa para decidir si mostrar la
     * pantalla del evento o redirigir directamente al dashboard
     * (por ejemplo, si el jugador refresca la pagina despues de
     * haber resuelto el evento).
     */
    public boolean isPreseasonPending() {
        Career career = requireCurrentCareer();
        Season season = career.getCurrentSeason();
        return season != null
                && season.getPhase() == SeasonPhase.PRESEASON
                && season.getPreseasonEventStatus() != EventStatus.RESOLVED;
    }

    public Map<String, Object> showPreseasonEvent() {
        Career career = requireCurrentCareer();
        Event event = eventService.getOrCreatePreseasonEvent(career);
        return Map.of("preseasonEvent", new PreseasonEventViewModel(event));
    }

    /**
     * Aplica la decision del jugador y, si se resolvio correctamente,
     * avanza automaticamente la temporada a TRANSFER_WINDOW (ya no hace
     * falta el boton de debug para esta transicion).
     */
    public Map<String, Object> resolveDecision(String optionId) {
        Career career = requireCurrentCareer();
        PreseasonOutcome outcome = eventService.resolvePreseasonDecision(career, optionId);
        seasonService.startTransferWindow(career);
        return Map.of("preseasonResult", new PreseasonOutcomeViewModel(outcome));
    }

    private Career requireCurrentCareer() {
        Optional<Career> career = careerService.getCurrentCareer();
        return career.orElseThrow(() -> new IllegalStateException("No hay una carrera activa."));
    }
}
