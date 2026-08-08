package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchResult;

import java.util.Random;

/**
 * Simulacion extremadamente simple de un partido, basada en la fuerza
 * de cada equipo, la reputacion/atributos del DT local y un factor de
 * azar. No pretende ser una simulacion futbolistica realista todavia.
 */
public class MatchSimulator {

    private final Random random;

    public MatchSimulator() {
        this(new Random());
    }

    public MatchSimulator(Random random) {
        this.random = random;
    }

    public MatchResult simulate(Match match, Career career) {
        Club home = match.getHomeTeam();
        Club away = match.getAwayTeam();

        double homePower = teamPower(home.getStrength(), career, home);
        double awayPower = teamPower(away.getStrength(), career, away);

        // Pequeña ventaja de local.
        homePower *= 1.1;

        int homeGoals = simulateGoals(homePower, awayPower);
        int awayGoals = simulateGoals(awayPower, homePower);

        return new MatchResult(homeGoals, awayGoals);
    }

    private double teamPower(TeamStrength strength, Career career, Club club) {
        double base = strength.overall();
        // Si el DT dirige a este club, sus atributos aportan un pequeño extra.
        if (career.getCurrentClub() != null && career.getCurrentClub().getId().equals(club.getId())) {
            double managerBonus = (career.getManager().getAttributes().getTactics()
                    + career.getManager().getAttributes().getLeadership()) / 20.0; // hasta +10
            base += managerBonus;
        }
        return base;
    }

    private int simulateGoals(double power, double rivalPower) {
        double diff = power - rivalPower;
        double expectedGoals = Math.max(0.2, 1.3 + diff / 40.0);
        double roll = random.nextDouble() * expectedGoals * 2;
        return (int) Math.round(roll);
    }
}
