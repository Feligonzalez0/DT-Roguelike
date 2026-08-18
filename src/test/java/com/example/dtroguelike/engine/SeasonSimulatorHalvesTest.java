package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;
import com.example.dtroguelike.domain.common.GamePhase;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.manager.ManagerStyle;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchState;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.domain.season.SeasonPhase;
import com.example.dtroguelike.domain.standings.StandingsEntry;

import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifica el issue 3: la temporada regular se juega en dos mitades,
 * simuladas automaticamente al entrar a REGULAR_SEASON y al finalizar
 * la temporada, sin duplicar partidos, puntos ni goles.
 */
class SeasonSimulatorHalvesTest {

    /**
     * Liga de 6 clubes -> round robin ida y vuelta de 10 fechas
     * (5 por rueda), lo que da un punto medio (10 / 2 = 5) claro para
     * testear la division en mitades.
     */
    private List<Club> buildLeagueClubs() {
        return List.of(
                buildClub("club-1", 60),
                buildClub("club-2", 58),
                buildClub("club-3", 55),
                buildClub("club-4", 62),
                buildClub("club-5", 57),
                buildClub("club-6", 59)
        );
    }

    private Club buildClub(String id, int strength) {
        return new Club(
                id, id + " FC", "Argentina", "Liga",
                strength, new TeamStrength(strength, strength, strength)
        );
    }

    private static class Harness {
        final CareerEngine engine;
        final SeasonSimulator seasonSimulator;
        final Career career;

        Harness(CareerEngine engine, SeasonSimulator seasonSimulator, Career career) {
            this.engine = engine;
            this.seasonSimulator = seasonSimulator;
            this.career = career;
        }
    }

    private Harness buildHarness() {
        List<Club> leagueClubs = buildLeagueClubs();

        MatchSimulator matchSimulator = new MatchSimulator();
        SeasonSimulator seasonSimulator =
                new SeasonSimulator(matchSimulator);

        CareerEngine engine = new CareerEngine(
                seasonSimulator,
                new ReputationEngine(),
                new ProgressionEngine(),
                new FixtureGenerator(),
                leagueClubs
        );

        Manager manager = new Manager("DT Test", 40, "Argentina", ManagerStyle.MANAGER);
        Career career = engine.startCareer(manager);
        engine.assignClub(career, leagueClubs.get(0));

        return new Harness(engine, seasonSimulator, career);
    }

    private int countFinished(List<Match> round) {
        return (int) round.stream()
                .filter(m -> m.getState() == MatchState.FINISHED)
                .count();
    }

    private int countNotStarted(List<Match> round) {
        return (int) round.stream()
                .filter(m -> m.getState() == MatchState.NOT_STARTED)
                .count();
    }

    @Test
    void entrarARegularSeasonSimulaSoloLaPrimeraMitad() {
        Harness h = buildHarness();

        h.engine.startTransferWindow(h.career);
        h.engine.startRegularSeason(h.career);

        Season season = h.career.getCurrentSeason();
        List<List<Match>> fixture = season.getFixture();

        int totalRounds = fixture.size();
        int midpoint = totalRounds / 2;

        assertEquals(10, totalRounds, "la liga de 6 clubes debe generar 10 fechas");
        assertEquals(5, midpoint);

        for (int i = 0; i < midpoint; i++) {
            List<Match> round = fixture.get(i);
            assertEquals(
                    round.size(), countFinished(round),
                    "todos los partidos de la primera mitad deben estar FINISHED (fecha " + (i + 1) + ")"
            );
        }

        for (int i = midpoint; i < totalRounds; i++) {
            List<Match> round = fixture.get(i);
            assertEquals(
                    round.size(), countNotStarted(round),
                    "todos los partidos de la segunda mitad deben seguir NOT_STARTED (fecha " + (i + 1) + ")"
            );
        }

        assertEquals(SeasonPhase.REGULAR_SEASON, season.getPhase());
        assertEquals(GamePhase.REGULAR_SEASON, h.career.getPhase());
    }

    @Test
    void tablaDespuesDePrimeraMitadReflejaSoloLosPartidosJugados() {
        Harness h = buildHarness();

        h.engine.startTransferWindow(h.career);
        h.engine.startRegularSeason(h.career);

        Season season = h.career.getCurrentSeason();
        Club managedClub = h.career.getCurrentClub();

        StandingsEntry entry = season.getStandings().getEntry(managedClub.getId());

        // El club dirigido jugo exactamente una fecha por cada una de
        // las 5 fechas de la primera mitad.
        assertEquals(5, entry.getPlayed());

        // Las SeasonStats (perspectiva del club dirigido) deben
        // coincidir con la tabla.
        assertEquals(entry.getPlayed(), season.getStats().getMatchesPlayed());
        assertEquals(entry.getWins(), season.getStats().getWins());
        assertEquals(entry.getDraws(), season.getStats().getDraws());
        assertEquals(entry.getLosses(), season.getStats().getLosses());
        assertEquals(entry.getGoalsFor(), season.getStats().getGoalsFor());
        assertEquals(entry.getGoalsAgainst(), season.getStats().getGoalsAgainst());
        assertEquals(entry.getPoints(), season.getStats().getPoints());

        // Todos los clubes de la liga jugaron la misma cantidad de
        // partidos (una fecha completa se simula entera).
        for (StandingsEntry other : season.getStandings().getEntries()) {
            assertEquals(5, other.getPlayed());
        }
    }

    @Test
    void finalizarTemporadaSimulaTodosLosPartidosRestantes() {
        Harness h = buildHarness();

        h.engine.startTransferWindow(h.career);
        h.engine.startRegularSeason(h.career);
        h.engine.finishSeason(h.career);

        Season season = h.career.getCurrentSeason();
        List<List<Match>> fixture = season.getFixture();

        for (List<Match> round : fixture) {
            for (Match match : round) {
                assertEquals(MatchState.FINISHED, match.getState());
            }
        }

        Club managedClub = h.career.getCurrentClub();
        StandingsEntry entry = season.getStandings().getEntry(managedClub.getId());

        int totalRounds = fixture.size();

        assertEquals(totalRounds, entry.getPlayed());
        assertEquals(totalRounds, season.getStats().getMatchesPlayed());

        for (StandingsEntry other : season.getStandings().getEntries()) {
            assertEquals(totalRounds, other.getPlayed());
        }

        assertEquals(SeasonPhase.END_OF_SEASON, season.getPhase());
        assertEquals(GamePhase.END_OF_SEASON, h.career.getPhase());
    }

    @Test
    void noSeDuplicanPartidosPuntosNiGolesAlReSimularLaPrimeraMitad() {
        Harness h = buildHarness();

        h.engine.startTransferWindow(h.career);
        h.engine.startRegularSeason(h.career);

        Season season = h.career.getCurrentSeason();
        Club managedClub = h.career.getCurrentClub();

        int matchesPlayedBefore = season.getStats().getMatchesPlayed();
        int pointsBefore = season.getStats().getPoints();
        int goalsForBefore = season.getStats().getGoalsFor();
        int goalsAgainstBefore = season.getStats().getGoalsAgainst();
        int standingsPlayedBefore =
                season.getStandings().getEntry(managedClub.getId()).getPlayed();

        // Volvemos a pedir la simulacion de la primera mitad: como los
        // partidos ya estan FINISHED, no debe pasar nada.
        List<Match> simuladosDeNuevo = h.seasonSimulator.simulateFirstHalf(h.career);

        assertTrue(
                simuladosDeNuevo.isEmpty(),
                "no deberian simularse partidos nuevos, ya estan todos FINISHED"
        );

        assertEquals(matchesPlayedBefore, season.getStats().getMatchesPlayed());
        assertEquals(pointsBefore, season.getStats().getPoints());
        assertEquals(goalsForBefore, season.getStats().getGoalsFor());
        assertEquals(goalsAgainstBefore, season.getStats().getGoalsAgainst());
        assertEquals(
                standingsPlayedBefore,
                season.getStandings().getEntry(managedClub.getId()).getPlayed()
        );
    }

    @Test
    void noSeDuplicanPartidosPuntosNiGolesAlReSimularLaSegundaMitad() {
        Harness h = buildHarness();

        h.engine.startTransferWindow(h.career);
        h.engine.startRegularSeason(h.career);

        // Simulamos la segunda mitad "a mano" (todavia en REGULAR_SEASON,
        // igual que hace finishSeason() internamente) dos veces seguidas.
        List<Match> primeraLlamada = h.seasonSimulator.simulateSecondHalf(h.career);
        assertFalse(primeraLlamada.isEmpty());

        Season season = h.career.getCurrentSeason();
        Club managedClub = h.career.getCurrentClub();

        int matchesPlayedBefore = season.getStats().getMatchesPlayed();
        int pointsBefore = season.getStats().getPoints();
        int goalsForBefore = season.getStats().getGoalsFor();
        int goalsAgainstBefore = season.getStats().getGoalsAgainst();

        List<Match> segundaLlamada = h.seasonSimulator.simulateSecondHalf(h.career);

        assertTrue(
                segundaLlamada.isEmpty(),
                "la segunda mitad ya esta jugada, no debe volver a simularse"
        );

        assertEquals(matchesPlayedBefore, season.getStats().getMatchesPlayed());
        assertEquals(pointsBefore, season.getStats().getPoints());
        assertEquals(goalsForBefore, season.getStats().getGoalsFor());
        assertEquals(goalsAgainstBefore, season.getStats().getGoalsAgainst());

        int totalRounds = season.getFixture().size();
        assertEquals(
                totalRounds,
                season.getStandings().getEntry(managedClub.getId()).getPlayed()
        );
    }

    @Test
    void ciclCompletoConSimulacionDeMitadesRespetaTransicionesDeFase() {
        Harness h = buildHarness();

        assertEquals(SeasonPhase.PRESEASON, h.career.getCurrentSeason().getPhase());

        h.engine.startTransferWindow(h.career);
        assertEquals(SeasonPhase.TRANSFER_WINDOW, h.career.getCurrentSeason().getPhase());

        h.engine.startRegularSeason(h.career);
        assertEquals(SeasonPhase.REGULAR_SEASON, h.career.getCurrentSeason().getPhase());

        h.engine.finishSeason(h.career);
        assertEquals(SeasonPhase.END_OF_SEASON, h.career.getCurrentSeason().getPhase());

        h.engine.showSeasonSummary(h.career);
        assertEquals(SeasonPhase.SUMMARY, h.career.getCurrentSeason().getPhase());
    }
}
