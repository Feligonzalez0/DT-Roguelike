package com.example.dtroguelike;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.manager.ManagerStyle;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchCompetition;
import com.example.dtroguelike.domain.match.MatchImportance;
import com.example.dtroguelike.domain.match.MatchResult;
import com.example.dtroguelike.engine.CareerEngine;
import com.example.dtroguelike.engine.MatchSimulator;
import com.example.dtroguelike.engine.ProgressionEngine;
import com.example.dtroguelike.engine.ReputationEngine;
import com.example.dtroguelike.engine.SeasonSimulator;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MatchSimulatorTest {

    @Test
    void simulatesAMatchWithNonNegativeGoals() {
        Manager manager = new Manager("DT", 40, "Argentina", ManagerStyle.MANAGER);
        MatchSimulator matchSimulator = new MatchSimulator(new Random(7));
        SeasonSimulator seasonSimulator = new SeasonSimulator(matchSimulator, new Random(7));
        CareerEngine engine = new CareerEngine(seasonSimulator, new ReputationEngine(), new ProgressionEngine(), null, null);
        Career career = engine.startCareer(manager);

        Club home = new Club("home", "Home FC", "Argentina", "Liga", 70, new TeamStrength(70, 70, 70));
        Club away = new Club("away", "Away FC", "Argentina", "Liga", 60, new TeamStrength(60, 60, 60));
        engine.assignClub(career, home);

        Match match = new Match(home, away, MatchCompetition.LEAGUE, MatchImportance.NORMAL, 0);
        MatchResult result = matchSimulator.simulate(match, career);

        assertTrue(result.getHomeGoals() >= 0);
        assertTrue(result.getAwayGoals() >= 0);
        assertNotNull(result.getOutcome());
    }
}
