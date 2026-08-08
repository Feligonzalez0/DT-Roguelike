package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.career.CareerState;
import com.example.dtroguelike.domain.career.ClubDepartureReason;
import com.example.dtroguelike.domain.career.ClubHistory;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.common.GamePhase;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.season.Season;

/**
 * Orquestador principal de una carrera. Delega el trabajo especifico a
 * los distintos motores (SeasonSimulator, ReputationEngine,
 * ProgressionEngine, etc.) en lugar de concentrar toda la logica aca.
 */
public class CareerEngine {

    private final SeasonSimulator seasonSimulator;
    private final ReputationEngine reputationEngine;
    private final ProgressionEngine progressionEngine;

    public CareerEngine(SeasonSimulator seasonSimulator,
                         ReputationEngine reputationEngine,
                         ProgressionEngine progressionEngine) {
        this.seasonSimulator = seasonSimulator;
        this.reputationEngine = reputationEngine;
        this.progressionEngine = progressionEngine;
    }

    /** Crea una carrera nueva a partir de un Manager recien creado. */
    public Career startCareer(Manager manager) {
        Career career = new Career(manager);
        career.setState(CareerState.LOOKING_FOR_CLUB);
        career.setPhase(GamePhase.CLUB_SELECTION);
        return career;
    }

    /** Asigna un club al Manager (tras aceptar una oferta) y arranca la temporada. */
    public void assignClub(Career career, Club club) {
        career.assignClub(club);
        career.getManager().getStats().incrementClubsManaged();
        career.setState(CareerState.ACTIVE);
        career.setPhase(GamePhase.PRESEASON);
        startSeason(career, currentYearFor(career));
    }

    /** Comienza una nueva temporada dentro de la carrera actual. */
    public void startSeason(Career career, int year) {
        Season season = new Season(year);
        career.setCurrentSeason(season);
        career.getManager().getStats().incrementSeasonsManaged();
        career.setPhase(GamePhase.PRESEASON);
    }

    /**
     * Cierra la temporada actual: actualiza reputacion, progresion del
     * Manager y deja la carrera lista para la siguiente temporada.
     */
    public void finishSeason(Career career) {
        if (career.getCurrentSeason() == null) {
            return;
        }
        seasonSimulator.finishSeason(career.getCurrentSeason());
        reputationEngine.updateAfterSeason(career);
        reputationEngine.adjustJobSecurity(career.getCurrentClubState());
        progressionEngine.applyManagerGrowth(career);
        career.setPhase(GamePhase.END_OF_SEASON);
    }

    /** El club despide al Manager. */
    public void fireManager(Career career) {
        reputationEngine.applyFiringPenalty(career);
        recordDeparture(career, ClubDepartureReason.FIRED);
        career.clearClub();
        career.setState(CareerState.LOOKING_FOR_NEW_CLUB);
        career.setPhase(GamePhase.CLUB_SELECTION_AFTER_DEPARTURE);
    }

    /** El Manager decide retirarse voluntariamente. */
    public void retireManager(Career career) {
        if (career.getCurrentClub() != null) {
            recordDeparture(career, ClubDepartureReason.RETIRED);
            career.clearClub();
        }
        career.setState(CareerState.RETIRED);
        career.setPhase(GamePhase.CAREER_FINISHED);
    }

    private void recordDeparture(Career career, ClubDepartureReason reason) {
        Club club = career.getCurrentClub();
        if (club == null) {
            return;
        }
        ClubHistory history = new ClubHistory(club.getId(), club.getName());
        history.setDepartureReason(reason);
        if (career.getCurrentClubState() != null) {
            history.setIdolatry(career.getCurrentClubState().getIdolatry());
        }
        career.addClubHistory(history);
    }

    private int currentYearFor(Career career) {
        return career.getCurrentSeason() != null ? career.getCurrentSeason().getYear() + 1
                : java.time.Year.now().getValue();
    }
}
