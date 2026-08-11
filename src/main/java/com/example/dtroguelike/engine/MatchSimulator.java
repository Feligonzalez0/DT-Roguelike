package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;
import com.example.dtroguelike.domain.common.GameConstants;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchResult;

import java.util.Random;

/**
 * Simulacion simple de un partido, basada en la fuerza de cada equipo
 * ({@link TeamStrength#overall()}), un pequeño bonus si el DT dirige a
 * alguno de los dos clubes, una ventaja de localia y un factor de azar.
 *
 * Los goles de cada equipo se generan con una distribucion de Poisson
 * (estandar para modelar goles de futbol): un equipo mas fuerte tiene,
 * en promedio, mas goles esperados que su rival, pero el resultado
 * concreto de cada partido sigue siendo aleatorio. Con Poisson los
 * resultados extremos son naturalmente poco frecuentes, sin necesidad
 * de reglas especiales; ademas se aplica un tope absoluto de goles como
 * red de seguridad.
 *
 * No pretende ser una simulacion futbolistica realista todavia (sin
 * mitades, eventos, tarjetas, lesiones, etc.) - eso queda para issues
 * futuros.
 */
public class MatchSimulator {

    private final Random random;

    public MatchSimulator() {
        this(new Random());
    }

    public MatchSimulator(Random random) {
        this.random = random;
    }

    /**
     * Simula el partido dado y devuelve el {@link MatchResult} generado.
     * No modifica el {@link Match}; es responsabilidad del caller (por
     * ejemplo {@code MatchService} o {@code SeasonSimulator}) asociar el
     * resultado con {@code match.setResult(result)}.
     */
    public MatchResult simulate(Match match, Career career) {
        Club home = match.getHomeTeam();
        Club away = match.getAwayTeam();

        double homePower = teamPower(home.getStrength(), career, home);
        double awayPower = teamPower(away.getStrength(), career, away);

        // Pequeña ventaja de localia.
        homePower *= GameConstants.MATCH_HOME_ADVANTAGE_MULTIPLIER;

        double homeExpectedGoals = expectedGoals(homePower, awayPower);
        double awayExpectedGoals = expectedGoals(awayPower, homePower);

        int homeGoals = capGoals(poissonSample(homeExpectedGoals));
        int awayGoals = capGoals(poissonSample(awayExpectedGoals));

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

    /**
     * Calcula los goles esperados (lambda de Poisson) de un equipo en
     * funcion de la diferencia de fuerza contra su rival, acotados entre
     * un piso y un techo razonables (ver {@link GameConstants}).
     */
    private double expectedGoals(double power, double rivalPower) {
        double diff = power - rivalPower;
        double expected = GameConstants.MATCH_BASE_EXPECTED_GOALS + diff / GameConstants.MATCH_STRENGTH_DIFF_DIVISOR;
        return Math.max(
                GameConstants.MATCH_MIN_EXPECTED_GOALS,
                Math.min(GameConstants.MATCH_MAX_EXPECTED_GOALS, expected)
        );
    }

    /**
     * Genera un numero de goles siguiendo una distribucion de Poisson de
     * media {@code lambda} (algoritmo de Knuth). Es el modelo estandar
     * para goles de futbol: valores bajos son los mas probables y los
     * resultados altos se vuelven cada vez mas raros.
     */
    private int poissonSample(double lambda) {
        double limit = Math.exp(-lambda);
        int goals = 0;
        double product = 1.0;
        do {
            goals++;
            product *= random.nextDouble();
        } while (product > limit);
        return goals - 1;
    }

    private int capGoals(int goals) {
        return Math.min(goals, GameConstants.MATCH_MAX_GOALS);
    }
}
