package com.example.dtroguelike;

import com.example.dtroguelike.application.MatchService;
import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.manager.ManagerStyle;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchCompetition;
import com.example.dtroguelike.domain.match.MatchImportance;
import com.example.dtroguelike.domain.match.MatchOutcome;
import com.example.dtroguelike.domain.match.MatchResult;
import com.example.dtroguelike.domain.match.MatchState;
import com.example.dtroguelike.engine.CareerEngine;
import com.example.dtroguelike.engine.FixtureGenerator;
import com.example.dtroguelike.engine.MatchSimulator;
import com.example.dtroguelike.engine.ProgressionEngine;
import com.example.dtroguelike.engine.ReputationEngine;
import com.example.dtroguelike.engine.SeasonSimulator;
import com.example.dtroguelike.infrastructure.repository.CareerRepository;
import com.example.dtroguelike.infrastructure.repository.InMemoryCareerRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MatchSimulatorTest {

    private Club buildClub(String id, int strength) {
        return new Club(id, id + " FC", "Argentina", "Liga", strength,
                new TeamStrength(strength, strength, strength));
    }

    /**
     * Arma una carrera con un club ya dirigido (misma liga que su rival),
     * usando el mismo wiring que la aplicacion real (FixtureGenerator +
     * lista de clubes de la liga), en lugar de nulls, para que
     * {@code engine.assignClub} pueda generar el fixture de la temporada
     * sin romper.
     */
    private Career buildCareerWithClub(Club managedClub, List<Club> leagueClubs, Random seasonRandom) {
        MatchSimulator matchSimulator = new MatchSimulator(seasonRandom);
        SeasonSimulator seasonSimulator = new SeasonSimulator(matchSimulator);
        CareerEngine engine = new CareerEngine(
                seasonSimulator, new ReputationEngine(), new ProgressionEngine(),
                new FixtureGenerator(), leagueClubs);

        Manager manager = new Manager("DT", 40, "Argentina", ManagerStyle.MANAGER);
        Career career = engine.startCareer(manager);
        engine.assignClub(career, managedClub,2);
        return career;
    }

    @Test
    void simulatesAMatchWithNonNegativeGoals() {
        Club home = buildClub("home", 70);
        Club away = buildClub("away", 60);
        Career career = buildCareerWithClub(home, List.of(home, away), new Random(7));

        MatchSimulator matchSimulator = new MatchSimulator(new Random(7));
        Match match = new Match(home, away, MatchCompetition.LEAGUE, MatchImportance.NORMAL, 0);
        MatchResult result = matchSimulator.simulate(match, career);

        assertTrue(result.getHomeGoals() >= 0);
        assertTrue(result.getAwayGoals() >= 0);
        assertNotNull(result.getOutcome());
    }

    @Test
    void goalsStayWithinAReasonableRange() {
        Club home = buildClub("home", 70);
        Club away = buildClub("away", 60);
        Career career = buildCareerWithClub(home, List.of(home, away), new Random(1));
        MatchSimulator matchSimulator = new MatchSimulator(new Random(123));

        for (int i = 0; i < 500; i++) {
            Match match = new Match(home, away, MatchCompetition.LEAGUE, MatchImportance.NORMAL, i);
            MatchResult result = matchSimulator.simulate(match, career);

            assertTrue(result.getHomeGoals() >= 0 && result.getHomeGoals() <= 8,
                    "Goles de local fuera de rango: " + result.getHomeGoals());
            assertTrue(result.getAwayGoals() >= 0 && result.getAwayGoals() <= 8,
                    "Goles de visitante fuera de rango: " + result.getAwayGoals());
        }
    }

    @Test
    void matchResultDistinguishesHomeWinDrawAndAwayWin() {
        assertEquals(MatchOutcome.HOME_WIN, new MatchResult(2, 1).getOutcome());
        assertEquals(MatchOutcome.DRAW, new MatchResult(1, 1).getOutcome());
        assertEquals(MatchOutcome.AWAY_WIN, new MatchResult(0, 2).getOutcome());
    }

    @Test
    void strongerTeamScoresMoreGoalsOnAverage() {
        Club strong = buildClub("strong", 85);
        Club weak = buildClub("weak", 35);
        Career career = buildCareerWithClub(strong, List.of(strong, weak), new Random(1));
        MatchSimulator matchSimulator = new MatchSimulator(new Random(99));

        long strongGoals = 0;
        long weakGoals = 0;
        int simulations = 300;

        for (int i = 0; i < simulations; i++) {
            // Alternamos localia para que la ventaja de local no distorsione la comparacion.
            Match match = (i % 2 == 0)
                    ? new Match(strong, weak, MatchCompetition.LEAGUE, MatchImportance.NORMAL, i)
                    : new Match(weak, strong, MatchCompetition.LEAGUE, MatchImportance.NORMAL, i);

            MatchResult result = matchSimulator.simulate(match, career);

            if (i % 2 == 0) {
                strongGoals += result.getHomeGoals();
                weakGoals += result.getAwayGoals();
            } else {
                strongGoals += result.getAwayGoals();
                weakGoals += result.getHomeGoals();
            }
        }

        double strongAverage = strongGoals / (double) simulations;
        double weakAverage = weakGoals / (double) simulations;

        assertTrue(strongAverage > weakAverage,
                "Se esperaba que el equipo mas fuerte promediara mas goles: "
                        + strongAverage + " vs " + weakAverage);
    }

    @Test
    void simulatingThroughMatchServiceFinishesTheMatchWithAResult() {
        Club home = buildClub("home", 65);
        Club away = buildClub("away", 55);
        Career career = buildCareerWithClub(home, List.of(home, away), new Random(5));

        MatchSimulator matchSimulator = new MatchSimulator(new Random(5));
        CareerRepository repository = new InMemoryCareerRepository();
        repository.save(career);
        MatchService matchService = new MatchService(matchSimulator, repository);

        Match match = new Match(home, away, MatchCompetition.LEAGUE, MatchImportance.NORMAL, 1);
        assertEquals(MatchState.NOT_STARTED, match.getState());

        MatchResult result = matchService.simulate(match, career);

        assertEquals(MatchState.FINISHED, match.getState());
        assertSame(result, match.getResult());
        assertNotNull(match.getResult().getOutcome());
    }

    @Test
    void simulationDoesNotBreakWithACareerManagingAClub() {
        Club home = buildClub("home", 70);
        Club away = buildClub("away", 60);
        Career career = buildCareerWithClub(home, List.of(home, away), new Random(7));

        assertNotNull(career.getCurrentClub());
        assertEquals(home, career.getCurrentClub());
        assertNotNull(career.getCurrentSeason());

        MatchSimulator matchSimulator = new MatchSimulator(new Random(7));
        Match match = new Match(home, away, MatchCompetition.LEAGUE, MatchImportance.NORMAL, 0);

        assertDoesNotThrow(() -> matchSimulator.simulate(match, career));
    }
}
