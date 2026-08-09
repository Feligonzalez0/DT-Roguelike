package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;
import com.example.dtroguelike.domain.common.GamePhase;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.manager.ManagerStyle;
import com.example.dtroguelike.domain.season.SeasonPhase;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CareerEngineTest {

    private CareerEngine buildCareerEngine() {
        MatchSimulator matchSimulator = new MatchSimulator(new Random(42));
        SeasonSimulator seasonSimulator =
                new SeasonSimulator(matchSimulator, new Random(42));

        return new CareerEngine(
                seasonSimulator,
                new ReputationEngine(),
                new ProgressionEngine()
        );
    }

    private Club buildClub() {
        return new Club(
                "test-club",
                "Test Club",
                "Argentina",
                "Liga",
                60,
                new TeamStrength(60, 60, 60)
        );
    }

    private Career buildCareer() {
        Manager manager = new Manager(
                "DT Test",
                40,
                "Argentina",
                ManagerStyle.MANAGER
        );

        CareerEngine engine = buildCareerEngine();
        Career career = engine.startCareer(manager);

        engine.assignClub(career, buildClub());

        return career;
    }

    @Test
    void contratarClubComienzaEnPreseason() {
        Career career = buildCareer();

        assertNotNull(career.getCurrentSeason());

        assertEquals(
                SeasonPhase.PRESEASON,
                career.getCurrentSeason().getPhase()
        );

        assertEquals(
                GamePhase.PRESEASON,
                career.getPhase()
        );
    }

    @Test
    void preseasonPasaATransferWindow() {
        Career career = buildCareer();
        CareerEngine engine = buildCareerEngine();

        engine.startTransferWindow(career);

        assertEquals(
                SeasonPhase.TRANSFER_WINDOW,
                career.getCurrentSeason().getPhase()
        );

        assertEquals(
                GamePhase.TRANSFER_WINDOW,
                career.getPhase()
        );
    }

    @Test
    void transferWindowPasaARegularSeason() {
        Career career = buildCareer();
        CareerEngine engine = buildCareerEngine();

        engine.startTransferWindow(career);
        engine.startRegularSeason(career);

        assertEquals(
                SeasonPhase.REGULAR_SEASON,
                career.getCurrentSeason().getPhase()
        );

        assertEquals(
                GamePhase.REGULAR_SEASON,
                career.getPhase()
        );
    }

    @Test
    void regularSeasonPasaAEndOfSeason() {
        Career career = buildCareer();
        CareerEngine engine = buildCareerEngine();

        engine.startTransferWindow(career);
        engine.startRegularSeason(career);
        engine.finishSeason(career);

        assertEquals(
                SeasonPhase.END_OF_SEASON,
                career.getCurrentSeason().getPhase()
        );

        assertEquals(
                GamePhase.END_OF_SEASON,
                career.getPhase()
        );
    }

    @Test
    void endOfSeasonPasaASummary() {
        Career career = buildCareer();
        CareerEngine engine = buildCareerEngine();

        engine.startTransferWindow(career);
        engine.startRegularSeason(career);
        engine.finishSeason(career);
        engine.showSeasonSummary(career);

        assertEquals(
                SeasonPhase.SUMMARY,
                career.getCurrentSeason().getPhase()
        );

        assertEquals(
                GamePhase.SUMMARY,
                career.getPhase()
        );
    }

    @Test
    void summaryCreaNuevaTemporadaConElAñoSiguiente() {
        Career career = buildCareer();
        CareerEngine engine = buildCareerEngine();

        int year = career.getCurrentSeason().getYear();

        engine.startTransferWindow(career);
        engine.startRegularSeason(career);
        engine.finishSeason(career);
        engine.showSeasonSummary(career);

        engine.startNextSeason(career);

        assertEquals(
                year + 1,
                career.getCurrentSeason().getYear()
        );

        assertEquals(
                SeasonPhase.PRESEASON,
                career.getCurrentSeason().getPhase()
        );

        assertEquals(
                GamePhase.PRESEASON,
                career.getPhase()
        );
    }

    @Test
    void noPuedeSaltarDePreseasonARegularSeason() {
        Career career = buildCareer();
        CareerEngine engine = buildCareerEngine();

        assertThrows(
                IllegalStateException.class,
                () -> engine.startRegularSeason(career)
        );

        assertEquals(
                SeasonPhase.PRESEASON,
                career.getCurrentSeason().getPhase()
        );
    }

    @Test
    void noPuedeFinalizarTemporadaDesdePreseason() {
        Career career = buildCareer();
        CareerEngine engine = buildCareerEngine();

        assertThrows(
                IllegalStateException.class,
                () -> engine.finishSeason(career)
        );

        assertEquals(
                SeasonPhase.PRESEASON,
                career.getCurrentSeason().getPhase()
        );
    }

    @Test
    void noPuedeMostrarSummaryAntesDeFinalizar() {
        Career career = buildCareer();
        CareerEngine engine = buildCareerEngine();

        engine.startTransferWindow(career);
        engine.startRegularSeason(career);

        assertThrows(
                IllegalStateException.class,
                () -> engine.showSeasonSummary(career)
        );

        assertEquals(
                SeasonPhase.REGULAR_SEASON,
                career.getCurrentSeason().getPhase()
        );
    }

    @Test
    void noPuedeCrearNuevaTemporadaAntesDelSummary() {
        Career career = buildCareer();
        CareerEngine engine = buildCareerEngine();

        assertThrows(
                IllegalStateException.class,
                () -> engine.startNextSeason(career)
        );

        assertEquals(
                2026,
                career.getCurrentSeason().getYear()
        );
    }
}
