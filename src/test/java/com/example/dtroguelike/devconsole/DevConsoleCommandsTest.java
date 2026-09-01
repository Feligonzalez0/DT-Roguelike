package com.example.dtroguelike.devconsole;

import com.example.dtroguelike.application.CareerService;
import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.career.CareerState;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.manager.ManagerStyle;
import com.example.dtroguelike.engine.CareerEngine;
import com.example.dtroguelike.engine.ClubOfferGenerator;
import com.example.dtroguelike.engine.FixtureGenerator;
import com.example.dtroguelike.engine.MatchSimulator;
import com.example.dtroguelike.engine.ProgressionEngine;
import com.example.dtroguelike.engine.ReputationEngine;
import com.example.dtroguelike.engine.SeasonSimulator;
import com.example.dtroguelike.infrastructure.repository.CareerRepository;
import com.example.dtroguelike.infrastructure.repository.InMemoryCareerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DevConsoleCommandsTest {

    private CareerRepository careerRepository;
    private CareerService careerService;
    private DevConsoleCommands commands;
    private Career career;

    @BeforeEach
    void setUp() {
        List<Club> clubs = List.of(
                buildClub("river-plate", 90),
                buildClub("rival-club", 55)
        );

        MatchSimulator matchSimulator = new MatchSimulator(new Random(42));
        SeasonSimulator seasonSimulator = new SeasonSimulator(matchSimulator);
        CareerEngine careerEngine = new CareerEngine(seasonSimulator, new ReputationEngine(),
                new ProgressionEngine(), new FixtureGenerator(), clubs);
        ClubOfferGenerator clubOfferGenerator = new ClubOfferGenerator(clubs, new Random(1));

        careerRepository = new InMemoryCareerRepository();
        careerService = new CareerService(careerEngine, clubOfferGenerator, careerRepository);
        commands = new DevConsoleCommands(careerRepository, careerService);

        Manager manager = new Manager("DT Test", 40, "Argentina", ManagerStyle.MANAGER);
        career = careerEngine.startCareer(manager);
        careerRepository.save(career);
    }

    private Club buildClub(String id, int reputation) {
        return new Club(id, "Club " + id, "Argentina", "Liga", reputation, new TeamStrength(60, 60, 60));
    }

    // 1. get manager.reputation
    @Test
    void getManagerReputation() {
        assertEquals("manager.reputation = 30", commands.execute("get manager.reputation"));
    }

    // 2. set manager.reputation
    @Test
    void setManagerReputation() {
        String result = commands.execute("set manager.reputation 0");

        assertEquals("Manager reputation set to 0", result);
        assertEquals(0, career.getManager().getReputation());
    }

    @Test
    void setManagerReputationIsClampedUsingExistingDomainRules() {
        commands.execute("set manager.reputation 999");

        assertEquals(100, career.getManager().getReputation());
    }

    // 3. get career.status
    @Test
    void getCareerStatus() {
        assertEquals("career.status = " + CareerState.LOOKING_FOR_CLUB, commands.execute("get career.status"));
    }

    // 4. set career.status
    @Test
    void setCareerStatus() {
        String result = commands.execute("set career.status ACTIVE");

        assertEquals("Career status set to ACTIVE", result);
        assertEquals(CareerState.ACTIVE, career.getState());
    }

    @Test
    void setCareerEndReasonFinishesTheCareer() {
        String result = commands.execute("set career.endReason NO_OFFERS");

        assertEquals("Career endReason set to NO_OFFERS (career finished: status=FINISHED)", result);
        assertEquals(CareerState.FINISHED, career.getState());
    }

    // 5. get offers
    @Test
    void getOffers() {
        careerService.generateOffers(career);

        String result = commands.execute("get offers");

        assertTrue(result.startsWith("Offers:"));
        assertTrue(result.contains("- Club "));
    }

    // 6. get offers.count
    @Test
    void getOffersCount() {
        careerService.generateOffers(career);
        int expected = careerService.getCurrentOffers().size();

        assertEquals("offers.count = " + expected, commands.execute("get offers.count"));
    }

    // 7. get all
    @Test
    void getAll() {
        String result = commands.execute("get all");

        assertTrue(result.startsWith("=== DEBUG STATE ==="));
        assertTrue(result.contains("MANAGER"));
        assertTrue(result.contains("CAREER"));
        assertTrue(result.contains("CLUB"));
        assertTrue(result.contains("OFFERS"));
    }

    @Test
    void getAllWithoutActiveCareerReturnsError() {
        careerRepository.clear();

        assertEquals("ERROR: no active career. Start a career from the web app first.",
                commands.execute("get all"));
    }

    // 8. comando desconocido
    @Test
    void unknownCommand() {
        assertEquals("ERROR: unknown command. Type 'help' for available commands.",
                commands.execute("hello"));
    }

    // 9. propiedad desconocida
    @Test
    void unknownProperty() {
        assertEquals("ERROR: unknown property: manager.foo", commands.execute("get manager.foo"));
    }

    // 10. argumento faltante
    @Test
    void missingValue() {
        assertEquals("ERROR: missing value.\nUsage: set manager.reputation <value>",
                commands.execute("set manager.reputation"));
    }

    // 11. valor numerico invalido
    @Test
    void invalidIntegerValue() {
        assertEquals("ERROR: invalid integer value: abc", commands.execute("set manager.reputation abc"));
    }

    @Test
    void clubPropertiesRequireAnAssignedClub() {
        assertEquals("ERROR: no club assigned to the current career yet.", commands.execute("get club.attack"));
        assertEquals("ERROR: no club assigned to the current career yet.",
                commands.execute("set club.attack 70"));
    }

    @Test
    void helpListsCommands() {
        assertTrue(commands.execute("help").contains("get <property>"));
    }
}
