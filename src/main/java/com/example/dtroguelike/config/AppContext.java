package com.example.dtroguelike.config;

import com.example.dtroguelike.application.CareerService;
import com.example.dtroguelike.application.EventService;
import com.example.dtroguelike.application.ManagerService;
import com.example.dtroguelike.application.MatchService;
import com.example.dtroguelike.application.SeasonService;
import com.example.dtroguelike.engine.CareerEngine;
import com.example.dtroguelike.engine.ClubOfferGenerator;
import com.example.dtroguelike.engine.DecisionResolver;
import com.example.dtroguelike.engine.EventEngine;
import com.example.dtroguelike.engine.MatchSimulator;
import com.example.dtroguelike.engine.ProgressionEngine;
import com.example.dtroguelike.engine.ReputationEngine;
import com.example.dtroguelike.engine.SeasonSimulator;
import com.example.dtroguelike.infrastructure.repository.CareerRepository;
import com.example.dtroguelike.infrastructure.repository.InMemoryCareerRepository;

/**
 * Contenedor de dependencias armado a mano (sin frameworks de DI), tal
 * como pide el alcance del MVP. Aca se instancian y conectan todos los
 * componentes: datos estaticos, engine, repositorio y servicios de
 * aplicacion.
 */
public class AppContext {

    private final GameData gameData;
    private final CareerRepository careerRepository;

    private final ManagerService managerService;
    private final CareerService careerService;
    private final SeasonService seasonService;
    private final EventService eventService;
    private final MatchService matchService;

    public AppContext() {
        this.gameData = GameData.load();
        this.careerRepository = new InMemoryCareerRepository();

        MatchSimulator matchSimulator = new MatchSimulator();
        SeasonSimulator seasonSimulator = new SeasonSimulator(matchSimulator);
        ReputationEngine reputationEngine = new ReputationEngine();
        ProgressionEngine progressionEngine = new ProgressionEngine();
        CareerEngine careerEngine = new CareerEngine(seasonSimulator, reputationEngine, progressionEngine);
        ClubOfferGenerator clubOfferGenerator = new ClubOfferGenerator(gameData.getClubs());
        EventEngine eventEngine = new EventEngine(gameData.getEvents());
        DecisionResolver decisionResolver = new DecisionResolver();

        this.managerService = new ManagerService();
        this.careerService = new CareerService(careerEngine, clubOfferGenerator, careerRepository);
        this.seasonService = new SeasonService(seasonSimulator, careerEngine, careerRepository, gameData.getClubs());
        this.eventService = new EventService(eventEngine, decisionResolver, careerRepository);
        this.matchService = new MatchService(matchSimulator, careerRepository);
    }

    public GameData getGameData() {
        return gameData;
    }

    public CareerRepository getCareerRepository() {
        return careerRepository;
    }

    public ManagerService getManagerService() {
        return managerService;
    }

    public CareerService getCareerService() {
        return careerService;
    }

    public SeasonService getSeasonService() {
        return seasonService;
    }

    public EventService getEventService() {
        return eventService;
    }

    public MatchService getMatchService() {
        return matchService;
    }
}
