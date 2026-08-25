package com.example.dtroguelike;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.career.CareerState;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.manager.ManagerStyle;
import com.example.dtroguelike.domain.offer.ClubOffer;
import com.example.dtroguelike.engine.CareerEngine;
import com.example.dtroguelike.engine.ClubOfferGenerator;
import com.example.dtroguelike.engine.FixtureGenerator;
import com.example.dtroguelike.engine.ProgressionEngine;
import com.example.dtroguelike.engine.ReputationEngine;
import com.example.dtroguelike.engine.SeasonSimulator;
import com.example.dtroguelike.engine.MatchSimulator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class CareerTest {

    private CareerEngine buildCareerEngine() {
        MatchSimulator matchSimulator = new MatchSimulator(new Random(42));
        SeasonSimulator seasonSimulator = new SeasonSimulator(matchSimulator);
        // FixtureGenerator.generate() necesita al menos 2 clubes de la misma
        // liga para poder armar el fixture cuando se asigna un club (antes
        // se pasaba null y assignClub->startSeason tiraba NPE).
        List<Club> leagueClubs = List.of(
                buildClub("river-plate", 90),
                buildClub("rival-club", 55)
        );
        return new CareerEngine(seasonSimulator, new ReputationEngine(), new ProgressionEngine(),
                new FixtureGenerator(), leagueClubs);
    }

    private Club buildClub(String id, int reputation) {
        return new Club(id, "Club " + id, "Argentina", "Liga", reputation, new TeamStrength(60, 60, 60));
    }

    @Test
    void startsCareerInLookingForClubState() {
        Manager manager = new Manager("DT", 40, "Argentina", ManagerStyle.MANAGER);
        Career career = buildCareerEngine().startCareer(manager);

        assertEquals(CareerState.LOOKING_FOR_CLUB, career.getState());
        assertNull(career.getCurrentClub());
    }

    @Test
    void generatesBetweenThreeAndFiveOffers() {
        Manager manager = new Manager("DT", 40, "Argentina", ManagerStyle.MANAGER);
        CareerEngine engine = buildCareerEngine();
        Career career = engine.startCareer(manager);

        List<Club> clubs = List.of(
                buildClub("a", 30), buildClub("b", 40), buildClub("c", 50),
                buildClub("d", 60), buildClub("e", 70), buildClub("f", 80)
        );
        ClubOfferGenerator generator = new ClubOfferGenerator(clubs, new Random(1));
        List<ClubOffer> offers = generator.generateOffers(career);

        assertTrue(offers.size() >= 3 && offers.size() <= 5);
    }

    @Test
    void selectingClubStartsSeason() {
        Manager manager = new Manager("DT", 40, "Argentina", ManagerStyle.MANAGER);
        CareerEngine engine = buildCareerEngine();
        Career career = engine.startCareer(manager);
        Club club = buildClub("river-plate", 90);

        engine.assignClub(career, club,2);

        assertEquals(club, career.getCurrentClub());
        assertNotNull(career.getCurrentClubState());
        assertNotNull(career.getCurrentSeason());
        assertEquals(CareerState.ACTIVE, career.getState());
    }

    @Test
    void createsSeasonWithGivenYear() {
        Manager manager = new Manager("DT", 40, "Argentina", ManagerStyle.MANAGER);
        CareerEngine engine = buildCareerEngine();
        Career career = engine.startCareer(manager);
        // startSeason() arma el fixture de la liga del club dirigido, asi
        // que necesita un club ya asignado (igual que en el flujo real).
        engine.assignClub(career, buildClub("boca-juniors", 80),2);

        engine.startSeason(career, 2026);

        assertEquals(2026, career.getCurrentSeason().getYear());
    }
}
