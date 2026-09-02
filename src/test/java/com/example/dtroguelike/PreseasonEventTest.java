package com.example.dtroguelike;

import com.example.dtroguelike.application.EventService;
import com.example.dtroguelike.application.PreseasonOutcome;
import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;
import com.example.dtroguelike.domain.event.Effect;
import com.example.dtroguelike.domain.event.EffectType;
import com.example.dtroguelike.domain.event.Event;
import com.example.dtroguelike.domain.event.EventOption;
import com.example.dtroguelike.domain.event.EventStatus;
import com.example.dtroguelike.domain.event.EventType;
import com.example.dtroguelike.domain.event.Outcome;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.manager.ManagerAttributeType;
import com.example.dtroguelike.domain.manager.ManagerStyle;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.domain.season.SeasonPhase;
import com.example.dtroguelike.engine.CareerEngine;
import com.example.dtroguelike.engine.DecisionResolver;
import com.example.dtroguelike.engine.EventEngine;
import com.example.dtroguelike.engine.FixtureGenerator;
import com.example.dtroguelike.engine.MatchSimulator;
import com.example.dtroguelike.engine.ProgressionEngine;
import com.example.dtroguelike.engine.ReputationEngine;
import com.example.dtroguelike.engine.SeasonSimulator;
import com.example.dtroguelike.infrastructure.repository.CareerRepository;
import com.example.dtroguelike.infrastructure.repository.InMemoryCareerRepository;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del evento de PRESEASON (ISSUE 11): generacion de opciones,
 * aplicacion de +delta sobre el atributo elegido, respeto de los
 * limites de atributos, seguridad frente a doble resolucion/refresh, y
 * la integracion con el avance automatico de fase de la temporada.
 */
class PreseasonEventTest {

    // FixtureGenerator.generate() necesita al menos 2 clubes de la misma
    // liga para poder armar el fixture al asignar el club (mismo patron
    // usado en EventDecisionTest/CareerEngineTest).
    private CareerEngine buildCareerEngine() {
        MatchSimulator matchSimulator = new MatchSimulator(new Random(1));
        SeasonSimulator seasonSimulator = new SeasonSimulator(matchSimulator);
        Club club = new Club("club", "Club", "Argentina", "Liga", 50, new TeamStrength(60, 60, 60));
        Club rival = new Club("rival", "Rival", "Argentina", "Liga", 50, new TeamStrength(55, 55, 55));
        return new CareerEngine(seasonSimulator, new ReputationEngine(), new ProgressionEngine(),
                new FixtureGenerator(), List.of(club, rival));
    }

    private Career buildActiveCareer() {
        CareerEngine engine = buildCareerEngine();
        Manager manager = new Manager("DT", 40, "Argentina", ManagerStyle.MANAGER);
        Career career = engine.startCareer(manager);
        // assignClub necesita el mismo club registrado en buildCareerEngine():
        // lo reconstruimos identico para no tener que exponer la lista de clubes.
        Club sameClub = new Club("club", "Club", "Argentina", "Liga", 50, new TeamStrength(60, 60, 60));
        engine.assignClub(career, sameClub, 2);
        return career;
    }

    private EventService buildEventService(Random random, CareerRepository repository) {
        EventEngine eventEngine = new EventEngine(List.of(), random);
        DecisionResolver decisionResolver = new DecisionResolver();
        return new EventService(eventEngine, decisionResolver, repository);
    }

    private EventService buildEventService(Random random) {
        return buildEventService(random, new InMemoryCareerRepository());
    }

    // =========================================================
    // GENERACION DEL EVENTO
    // =========================================================

    @Test
    void preseasonEventOffersThreeUniqueAttributeOptions() {
        EventEngine eventEngine = new EventEngine(List.of(), new Random(7));

        Career career = new Career(new Manager("null", 40, "Arg", ManagerStyle.STRATEGIST));
        career.setCurrentSeason(new Season(2026,new Club("boca", "Boca","Arg", "LPF", 50, null)));
        Event event = eventEngine.generatePreseasonEvent(career);

        assertEquals(EventType.PRESEASON_DEVELOPMENT, event.getType());
        assertEquals(3, event.getOptions().size());

        Set<String> ids = new HashSet<>();
        for (EventOption option : event.getOptions()) {
            ids.add(option.getId());
            assertEquals(1.0, option.getSuccessChance());
            assertEquals(1, option.getSuccessOutcome().getEffects().size());
        }
        assertEquals(3, ids.size(), "Las 3 opciones deben corresponder a atributos distintos");
    }

    @Test
    void preseasonEventStartsAsPending() {
        Career career = buildActiveCareer();

        assertEquals(SeasonPhase.PRESEASON, career.getCurrentSeason().getPhase());
        assertEquals(EventStatus.PENDING, career.getCurrentSeason().getPreseasonEventStatus());
    }

    // =========================================================
    // ATRIBUTOS
    // =========================================================

    @Test
    void choosingAnOptionIncreasesTheChosenAttributeByDelta() {
        Career career = buildActiveCareer();
        EventService eventService = buildEventService(new Random(3));

        Event event = eventService.getOrCreatePreseasonEvent(career);
        EventOption chosen = event.getOptions().get(0);
        ManagerAttributeType attribute = ManagerAttributeType.fromEffectType(
                chosen.getSuccessOutcome().getEffects().get(0).getType());
        int before = attribute.readValue(career.getManager().getAttributes());

        PreseasonOutcome outcome = eventService.resolvePreseasonDecision(career, chosen.getId());

        assertEquals(before, outcome.before());
        assertTrue(outcome.delta() >= 1 && outcome.delta() <= 3);
        assertEquals(before + outcome.delta(), outcome.after());
        assertEquals(outcome.after(), attribute.readValue(career.getManager().getAttributes()));
    }

    @Test
    void attributeAtMaxDoesNotExceedMaxWhenResolvingPreseasonOption() {
        Career career = buildActiveCareer();
        career.getManager().getAttributes().setTactics(100);

        Outcome outcome = Outcome.of("Táctica mejorada.", new Effect(EffectType.MANAGER_TACTICS, 3));
        EventOption option = new EventOption("tactics", "Mejorar Táctica", 1.0, List.of(), outcome, outcome);
        Event event = new Event("preseason-test", "Nueva temporada",
                "Después de analizar la temporada anterior...",
                EventType.PRESEASON_DEVELOPMENT, List.of(), List.of(option));

        new DecisionResolver().resolve(career, event, option);

        assertEquals(100, career.getManager().getAttributes().getTactics());
    }

    // =========================================================
    // SEGURIDAD DEL FLUJO
    // =========================================================

    @Test
    void resolvingTheEventChangesStatusFromPendingToResolved() {
        Career career = buildActiveCareer();
        EventService eventService = buildEventService(new Random(11));

        Event event = eventService.getOrCreatePreseasonEvent(career);
        assertEquals(EventStatus.PENDING, career.getCurrentSeason().getPreseasonEventStatus());

        eventService.resolvePreseasonDecision(career, event.getOptions().get(0).getId());

        assertEquals(EventStatus.RESOLVED, career.getCurrentSeason().getPreseasonEventStatus());
    }

    @Test
    void aResolvedPreseasonEventCannotBeResolvedAgain() {
        Career career = buildActiveCareer();
        EventService eventService = buildEventService(new Random(17));

        Event event = eventService.getOrCreatePreseasonEvent(career);
        EventOption chosen = event.getOptions().get(0);

        PreseasonOutcome firstOutcome = eventService.resolvePreseasonDecision(career, chosen.getId());
        int valueAfterFirstResolution = firstOutcome.attribute().readValue(career.getManager().getAttributes());

        assertThrows(IllegalStateException.class,
                () -> eventService.resolvePreseasonDecision(career, chosen.getId()));

        int valueAfterSecondAttempt = firstOutcome.attribute().readValue(career.getManager().getAttributes());
        assertEquals(valueAfterFirstResolution, valueAfterSecondAttempt,
                "Un segundo intento de resolucion no debe volver a aplicar el efecto (Tactica +2 en vez de +1, etc.)");
    }

    @Test
    void refreshingDoesNotGenerateANewPreseasonEvent() {
        Career career = buildActiveCareer();
        EventService eventService = buildEventService(new Random(23));

        Event firstView = eventService.getOrCreatePreseasonEvent(career);
        Event secondView = eventService.getOrCreatePreseasonEvent(career);

        assertSame(firstView, secondView,
                "Refrescar la pagina no debe generar un evento de pretemporada nuevo");
        assertEquals(firstView.getId(), secondView.getId());
    }

    @Test
    void attributesArePersistedAfterResolvingThePreseasonEvent() {
        Career career = buildActiveCareer();
        CareerRepository repository = new InMemoryCareerRepository();
        repository.save(career);
        EventService eventService = buildEventService(new Random(29), repository);

        Event event = eventService.getOrCreatePreseasonEvent(career);
        PreseasonOutcome outcome = eventService.resolvePreseasonDecision(career, event.getOptions().get(0).getId());

        Career reloaded = repository.findCurrent().orElseThrow();
        int reloadedValue = outcome.attribute().readValue(reloaded.getManager().getAttributes());

        assertEquals(outcome.after(), reloadedValue);
    }

    // =========================================================
    // INTEGRACION CON EL CICLO DE TEMPORADA
    // =========================================================

    @Test
    void afterResolvingPreseasonEventTheFlowAdvancesToTransferWindow() {
        Career career = buildActiveCareer();
        EventService eventService = buildEventService(new Random(31));

        Event event = eventService.getOrCreatePreseasonEvent(career);
        eventService.resolvePreseasonDecision(career, event.getOptions().get(0).getId());

        // Igual que hace PreseasonController.resolveDecision(): al resolver
        // el evento, la temporada avanza automaticamente de PRESEASON a
        // TRANSFER_WINDOW sin necesitar el boton de debug.
        buildCareerEngine().startTransferWindow(career);

        assertEquals(SeasonPhase.TRANSFER_WINDOW, career.getCurrentSeason().getPhase());
    }
}
