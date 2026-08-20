package com.example.dtroguelike.domain.season;

import org.junit.jupiter.api.Test;

import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;

import static org.junit.jupiter.api.Assertions.*;

class SeasonTest {

    @Test
    void unaTemporadaComienzaEnPreseason() {
        Club club = new Club("1", "Boca", "Argentina", "LPF", 50, new TeamStrength(50, 50, 50));
        Season season = new Season(2026, club);

        assertEquals(SeasonPhase.PRESEASON, season.getPhase());
        assertEquals(2026, season.getYear());
    }

    @Test
    void puedeAvanzarDesdePreseasonHastaTransferWindow() {
        Club club = new Club("1", "Boca", "Argentina", "LPF", 50, new TeamStrength(50, 50, 50));
        Season season = new Season(2026, club);
        season.startTransferWindow();

        assertEquals(SeasonPhase.TRANSFER_WINDOW, season.getPhase());
    }

    @Test
    void puedeAvanzarDesdeTransferWindowHastaRegularSeason() {
        Club club = new Club("1", "Boca", "Argentina", "LPF", 50, new TeamStrength(50, 50, 50));
        Season season = new Season(2026, club);

        season.startTransferWindow();
        season.startRegularSeason();

        assertEquals(SeasonPhase.REGULAR_SEASON, season.getPhase());
    }

    @Test
    void puedeFinalizarLaTemporada() {
        Club club = new Club("1", "Boca", "Argentina", "LPF", 50, new TeamStrength(50, 50, 50));
        Season season = new Season(2026, club);

        season.startTransferWindow();
        season.startRegularSeason();
        season.finish();

        assertEquals(SeasonPhase.END_OF_SEASON, season.getPhase());
    }

    @Test
    void puedePasarDeEndOfSeasonASummary() {
        Club club = new Club("1", "Boca", "Argentina", "LPF", 50, new TeamStrength(50, 50, 50));
        Season season = new Season(2026, club);

        season.startTransferWindow();
        season.startRegularSeason();
        season.finish();
        season.showSummary();

        assertEquals(SeasonPhase.SUMMARY, season.getPhase());
    }

    @Test
    void cicloCompletoDeUnaTemporada() {
        Club club = new Club("1", "Boca", "Argentina", "LPF", 50, new TeamStrength(50, 50, 50));
        Season season = new Season(2026, club);

        assertEquals(SeasonPhase.PRESEASON, season.getPhase());

        season.startTransferWindow();
        assertEquals(SeasonPhase.TRANSFER_WINDOW, season.getPhase());

        season.startRegularSeason();
        assertEquals(SeasonPhase.REGULAR_SEASON, season.getPhase());

        season.finish();
        assertEquals(SeasonPhase.END_OF_SEASON, season.getPhase());

        season.showSummary();
        assertEquals(SeasonPhase.SUMMARY, season.getPhase());
    }

    @Test
    void noPuedeComenzarRegularSeasonDesdePreseason() {
        Club club = new Club("1", "Boca", "Argentina", "LPF", 50, new TeamStrength(50, 50, 50));
        Season season = new Season(2026, club);

        assertThrows(
                IllegalStateException.class,
                season::startRegularSeason
        );
    }

    @Test
    void noPuedeFinalizarDesdePreseason() {
        Club club = new Club("1", "Boca", "Argentina", "LPF", 50, new TeamStrength(50, 50, 50));
        Season season = new Season(2026, club);

        assertThrows(
                IllegalStateException.class,
                season::finish
        );
    }

    @Test
    void noPuedeMostrarSummaryAntesDeFinalizar() {
        Club club = new Club("1", "Boca", "Argentina", "LPF", 50, new TeamStrength(50, 50, 50));
        Season season = new Season(2026, club);

        season.startTransferWindow();
        season.startRegularSeason();

        assertThrows(
                IllegalStateException.class,
                season::showSummary
        );
    }
}