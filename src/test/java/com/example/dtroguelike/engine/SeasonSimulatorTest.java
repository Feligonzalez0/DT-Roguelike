package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.manager.ManagerStyle;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchState;
import com.example.dtroguelike.domain.season.SeasonPhase;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class SeasonSimulatorTest {

    private Club buildClub(String id, int strength) {
        return new Club(id, id + " FC", "Argentina", "Liga", strength,
                new TeamStrength(strength, strength, strength));
    }

    /** Arma una carrera con club asignado y la temporada ya en REGULAR_SEASON. */
    private Career buildCareerInRegularSeason(Club managedClub, List<Club> leagueClubs, CareerEngine engine) {
        Manager manager = new Manager("DT", 40, "Argentina", ManagerStyle.MANAGER);
        Career career = engine.startCareer(manager);
        engine.assignClub(career, managedClub);
        engine.startTransferWindow(career);
        engine.startRegularSeason(career);
        return career;
    }

    private CareerEngine buildEngine(List<Club> leagueClubs, Random random) {
        MatchSimulator matchSimulator = new MatchSimulator(random);
        SeasonSimulator seasonSimulator = new SeasonSimulator(matchSimulator, random);
        return new CareerEngine(seasonSimulator, new ReputationEngine(), new ProgressionEngine(),
                new FixtureGenerator(), leagueClubs);
    }

    @Test
    void simulatingNextMatchFinishesItAndAssignsAValidResult() {
        Club home = buildClub("home", 70);
        Club away = buildClub("away", 60);
        List<Club> league = List.of(home, away);
        Random random = new Random(11);
        CareerEngine engine = buildEngine(league, random);
        Career career = buildCareerInRegularSeason(home, league, engine);

        MatchSimulator matchSimulator = new MatchSimulator(random);
        SeasonSimulator seasonSimulator = new SeasonSimulator(matchSimulator, random);

        Match firstPending = career.getCurrentSeason().getMatches().stream()
                .filter(m -> m.getState() == MatchState.NOT_STARTED)
                .findFirst().orElseThrow();
        assertEquals(MatchState.NOT_STARTED, firstPending.getState());

        Match simulated = seasonSimulator.simulateNextMatch(career);

        assertEquals(MatchState.FINISHED, simulated.getState());
        assertNotNull(simulated.getResult());
        assertTrue(simulated.getResult().getHomeGoals() >= 0);
        assertTrue(simulated.getResult().getAwayGoals() >= 0);
    }

    @Test
    void simulatingNextMatchUpdatesSeasonStats() {
        Club home = buildClub("home", 70);
        Club away = buildClub("away", 60);
        List<Club> league = List.of(home, away);
        Random random = new Random(22);
        CareerEngine engine = buildEngine(league, random);
        Career career = buildCareerInRegularSeason(home, league, engine);

        MatchSimulator matchSimulator = new MatchSimulator(random);
        SeasonSimulator seasonSimulator = new SeasonSimulator(matchSimulator, random);

        assertEquals(0, career.getCurrentSeason().getStats().getMatchesPlayed());

        Match simulated = seasonSimulator.simulateNextMatch(career);

        boolean managedIsHome = simulated.getHomeTeam().getId().equals(home.getId());
        int expectedGoalsFor = managedIsHome ? simulated.getResult().getHomeGoals() : simulated.getResult().getAwayGoals();
        int expectedGoalsAgainst = managedIsHome ? simulated.getResult().getAwayGoals() : simulated.getResult().getHomeGoals();

        assertEquals(1, career.getCurrentSeason().getStats().getMatchesPlayed());
        assertEquals(expectedGoalsFor, career.getCurrentSeason().getStats().getGoalsFor());
        assertEquals(expectedGoalsAgainst, career.getCurrentSeason().getStats().getGoalsAgainst());
    }

    @Test
    void simulatingConsecutiveMatchesAdvancesThroughTheFixtureInRoundOrder() {
        Club home = buildClub("home", 70);
        Club away = buildClub("away", 60);
        List<Club> league = List.of(home, away);
        Random random = new Random(33);
        CareerEngine engine = buildEngine(league, random);
        Career career = buildCareerInRegularSeason(home, league, engine);

        MatchSimulator matchSimulator = new MatchSimulator(random);
        SeasonSimulator seasonSimulator = new SeasonSimulator(matchSimulator, random);

        Match first = seasonSimulator.simulateNextMatch(career);
        Match second = seasonSimulator.simulateNextMatch(career);

        assertTrue(second.getRound() >= first.getRound());
        assertEquals(2, career.getCurrentSeason().getStats().getMatchesPlayed());
    }

    @Test
    void cannotSimulateOutsideRegularSeason() {
        Club home = buildClub("home", 70);
        Club away = buildClub("away", 60);
        List<Club> league = List.of(home, away);
        Random random = new Random(44);
        CareerEngine engine = buildEngine(league, random);

        Manager manager = new Manager("DT", 40, "Argentina", ManagerStyle.MANAGER);
        Career career = engine.startCareer(manager);
        engine.assignClub(career, home); // fase PRESEASON, todavia no REGULAR_SEASON

        MatchSimulator matchSimulator = new MatchSimulator(random);
        SeasonSimulator seasonSimulator = new SeasonSimulator(matchSimulator, random);

        assertEquals(SeasonPhase.PRESEASON, career.getCurrentSeason().getPhase());
        assertThrows(IllegalStateException.class, () -> seasonSimulator.simulateNextMatch(career));
    }

    @Test
    void throwsWhenNoMorePendingMatchesForTheManagedClub() {
        Club home = buildClub("home", 70);
        Club away = buildClub("away", 60);
        List<Club> league = List.of(home, away);
        Random random = new Random(55);
        CareerEngine engine = buildEngine(league, random);
        Career career = buildCareerInRegularSeason(home, league, engine);

        MatchSimulator matchSimulator = new MatchSimulator(random);
        SeasonSimulator seasonSimulator = new SeasonSimulator(matchSimulator, random);

        long pendingMatches = career.getCurrentSeason().getMatches().stream()
                .filter(m -> m.getHomeTeam().getId().equals(home.getId())
                        || m.getAwayTeam().getId().equals(home.getId()))
                .count();

        for (int i = 0; i < pendingMatches; i++) {
            seasonSimulator.simulateNextMatch(career);
        }

        assertThrows(IllegalStateException.class, () -> seasonSimulator.simulateNextMatch(career));
    }
}
