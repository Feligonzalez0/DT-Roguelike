package com.example.dtroguelike.domain.season;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeasonTest {

    @Test
    void unaTemporadaComienzaEnPreseason() {
        Season season = new Season(2026);

        assertEquals(SeasonPhase.PRESEASON, season.getPhase());
        assertEquals(2026, season.getYear());
    }

    @Test
    void puedeAvanzarDesdePreseasonHastaTransferWindow() {
        Season season = new Season(2026);

        season.startTransferWindow();

        assertEquals(SeasonPhase.TRANSFER_WINDOW, season.getPhase());
    }

    @Test
    void puedeAvanzarDesdeTransferWindowHastaRegularSeason() {
        Season season = new Season(2026);

        season.startTransferWindow();
        season.startRegularSeason();

        assertEquals(SeasonPhase.REGULAR_SEASON, season.getPhase());
    }

    @Test
    void puedeFinalizarLaTemporada() {
        Season season = new Season(2026);

        season.startTransferWindow();
        season.startRegularSeason();
        season.finish();

        assertEquals(SeasonPhase.END_OF_SEASON, season.getPhase());
    }

    @Test
    void puedePasarDeEndOfSeasonASummary() {
        Season season = new Season(2026);

        season.startTransferWindow();
        season.startRegularSeason();
        season.finish();
        season.showSummary();

        assertEquals(SeasonPhase.SUMMARY, season.getPhase());
    }

    @Test
    void cicloCompletoDeUnaTemporada() {
        Season season = new Season(2026);

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
        Season season = new Season(2026);

        assertThrows(
                IllegalStateException.class,
                season::startRegularSeason
        );
    }

    @Test
    void noPuedeFinalizarDesdePreseason() {
        Season season = new Season(2026);

        assertThrows(
                IllegalStateException.class,
                season::finish
        );
    }

    @Test
    void noPuedeMostrarSummaryAntesDeFinalizar() {
        Season season = new Season(2026);

        season.startTransferWindow();
        season.startRegularSeason();

        assertThrows(
                IllegalStateException.class,
                season::showSummary
        );
    }
}