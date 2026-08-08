package com.example.dtroguelike;

import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClubTest {

    @Test
    void createsClubWithStrength() {
        TeamStrength strength = new TeamStrength(80, 75, 70);
        Club club = new Club("river-plate", "River Plate", "Argentina", "Liga Profesional Argentina", 92, strength);

        assertEquals("river-plate", club.getId());
        assertEquals("River Plate", club.getName());
        assertEquals(92, club.getReputation());
        assertEquals(80, club.getStrength().getAttack());
    }

    @Test
    void createsIndependentStateInstances() {
        TeamStrength strength = new TeamStrength(80, 75, 70);
        Club club = new Club("id", "Club", "Argentina", "Liga", 50, strength);

        var state1 = club.createInitialState();
        var state2 = club.createInitialState();
        state1.addBudget(1000);

        assertNotEquals(state1.getBudget(), state2.getBudget());
    }
}
