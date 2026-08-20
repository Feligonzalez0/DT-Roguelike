package com.example.dtroguelike.engine;

import java.util.List;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.career.CareerState;
import com.example.dtroguelike.domain.career.ClubDepartureReason;
import com.example.dtroguelike.domain.career.ClubHistory;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.common.GamePhase;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.domain.season.SeasonObjective;
import com.example.dtroguelike.domain.season.SeasonObjectiveResult;
import com.example.dtroguelike.domain.season.SeasonPhase;
import com.example.dtroguelike.domain.standings.StandingsTable;

/**
 * Orquestador principal de una carrera. Delega el trabajo especifico a
 * los distintos motores (SeasonSimulator, ReputationEngine,
 * ProgressionEngine, etc.) en lugar de concentrar toda la logica aca.
 */
public class CareerEngine {

    private final SeasonSimulator seasonSimulator;
    private final ReputationEngine reputationEngine;
    private final ProgressionEngine progressionEngine;
    private final FixtureGenerator fixtureGenerator;
    private final List<Club> allClubs;
    private Career currentCareer;

    public CareerEngine(SeasonSimulator seasonSimulator,
                         ReputationEngine reputationEngine,
                         ProgressionEngine progressionEngine,
                         FixtureGenerator fixtureGenerator,
                         List<Club> allClubs) {
        this.seasonSimulator = seasonSimulator;
        this.reputationEngine = reputationEngine;
        this.progressionEngine = progressionEngine;
        this.fixtureGenerator = fixtureGenerator;
        this.allClubs = allClubs;
    }

    /** Crea una carrera nueva a partir de un Manager recien creado. */
    public Career startCareer(Manager manager) {
        Career career = new Career(manager);
        career.setState(CareerState.LOOKING_FOR_CLUB);
        career.setPhase(GamePhase.CLUB_SELECTION);

        this.currentCareer = career;

        return career;
    }

    /** Asigna un club al Manager (tras aceptar una oferta) y arranca la temporada. */
    public void assignClub(Career career, Club club) {
        career.assignClub(club);
        career.getManager().getStats().incrementClubsManaged();
        career.setState(CareerState.ACTIVE);

        startSeason(career, currentYearFor(career));
    }

    /** Comienza una nueva temporada dentro de la carrera actual. */
    public void startSeason(Career career, int year) {
        Season season = new Season(year, career.getCurrentClub());

        List<Club> leagueClubs = clubsOfManagedLeague(career);
        List<List<Match>> fixture = fixtureGenerator.generate(leagueClubs, year);
        season.setFixture(fixture);
        
        StandingsTable standings =
        new StandingsTable(leagueClubs);
        season.setStandings(standings);

        career.setCurrentSeason(season);
        career.getManager().getStats().incrementSeasonsManaged();
        career.setPhase(GamePhase.PRESEASON);
    }

    /** PRESEASON -> TRANSFER_WINDOW. */
    public void startTransferWindow(Career career) {
        requireSeasonPhase(career, SeasonPhase.PRESEASON);
        career.getCurrentSeason().startTransferWindow();
        career.setPhase(GamePhase.TRANSFER_WINDOW);
    }

    /**
     * TRANSFER_WINDOW -> REGULAR_SEASON.
     *
     * Al entrar a REGULAR_SEASON se simula automaticamente la primera
     * mitad del fixture de la temporada (ver {@link SeasonSimulator#simulateFirstHalf(Career)}).
     * La segunda mitad queda pendiente hasta {@link #finishSeason(Career)}.
     */
    public void startRegularSeason(Career career) {
        requireSeasonPhase(career, SeasonPhase.TRANSFER_WINDOW);
        career.getCurrentSeason().startRegularSeason();
        career.setPhase(GamePhase.REGULAR_SEASON);

        seasonSimulator.simulateFirstHalf(career);
    }

    /**
     * Cierra la temporada: REGULAR_SEASON -> END_OF_SEASON.
     *
     * Antes de cerrar la temporada se simula la segunda mitad del
     * fixture (ver {@link SeasonSimulator#simulateSecondHalf(Career)}),
     * de manera que todos los partidos de la temporada queden
     * {@code FINISHED} antes de aplicar las consecuencias del cierre.
     */
    public void finishSeason(Career career) {
        requireSeasonPhase(career, SeasonPhase.REGULAR_SEASON);

        Season season = career.getCurrentSeason();
        if (season == null) {
            throw new IllegalStateException("No hay una temporada activa.");
        }

        seasonSimulator.simulateSecondHalf(career);

        reputationEngine.updateAfterSeason(career);
        reputationEngine.adjustJobSecurity(career.getCurrentClubState());
        progressionEngine.applyManagerGrowth(career);

        //Evaluar objetivos de temporada
        Club managedClub = career.getCurrentClub();
        SeasonObjective objective = season.getObjective();
        SeasonObjectiveResult objectiveResult = new SeasonObjectiveResult();
        int finalPosition = season.getStandings().getEntry(managedClub.getId()).getPosition();

        objectiveResult.evaluateObjective(objective.getTargetPosition(), finalPosition, objective.mustWinLeague());
        season.setObjectiveResult(objectiveResult);
        
        season.finish();

        career.setPhase(GamePhase.END_OF_SEASON);
    }

    /** END_OF_SEASON -> SUMMARY. */
    public void showSeasonSummary(Career career) {
        requireSeasonPhase(career, SeasonPhase.END_OF_SEASON);
        career.getCurrentSeason().showSummary();
        career.setPhase(GamePhase.SUMMARY);
    }

    /**
     * SUMMARY -> nueva Season con el año siguiente, comenzando en PRESEASON.
     */
    public void startNextSeason(Career career) {
        requireSeasonPhase(career, SeasonPhase.SUMMARY);
        int nextYear = career.getCurrentSeason().getYear() + 1;
        startSeason(career, nextYear);
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

    private void requireSeasonPhase(Career career, SeasonPhase expected) {
        if (career == null || career.getCurrentSeason() == null) {
            throw new IllegalStateException("No hay una temporada activa.");
        }
        SeasonPhase actual = career.getCurrentSeason().getPhase();
        if (actual != expected) {
            throw new IllegalStateException(
                    "No se puede realizar esta transicion desde " + actual
                            + "; se esperaba " + expected + "."
            );
        }
    }

    private int currentYearFor(Career career) {
        return career.getCurrentSeason() != null ? career.getCurrentSeason().getYear() + 1
                : java.time.Year.now().getValue();
    }

    public Career getCurrentCareer() {
        return currentCareer;
    }

    // Obtener solo los clubes de la liga dirigida
    private List<Club> clubsOfManagedLeague(Career career) {
        Club managedClub = career.getCurrentClub();

        if (managedClub == null) {
            throw new IllegalStateException(
                    "No hay un club dirigido actualmente."
            );
        }

        return allClubs.stream()
                .filter(club ->
                        club.getLeague().equals(managedClub.getLeague()))
                .toList();
    }

}
