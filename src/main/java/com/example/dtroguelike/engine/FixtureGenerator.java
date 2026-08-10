package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchCompetition;
import com.example.dtroguelike.domain.match.MatchImportance;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Genera el fixture de una liga con formato todos contra todos,
 * ida y vuelta.
 *
 * Características:
 *
 * - Cada pareja de clubes se enfrenta dos veces.
 * - Cada club juega la mitad de sus partidos como local
 *   y la otra mitad como visitante.
 * - El fixture cambia entre temporadas.
 * - El fixture de una misma temporada es reproducible.
 * - Se intenta evitar rachas largas de localía o visitante.
 */
public class FixtureGenerator {
    /**
     * Genera todos los partidos de una temporada.
     *
     * El año se utiliza como seed del generador aleatorio.
     *
     * Por lo tanto:
     *
     * generate(clubs, 2026)
     * generate(clubs, 2026)
     *
     * producen el mismo fixture.
     *
     * Mientras que:
     *
     * generate(clubs, 2026)
     * generate(clubs, 2027)
     *
     * producen fixtures diferentes.
     *
     * @param clubs clubes participantes de la liga
     * @param seasonYear año de la temporada
     * @return lista completa de partidos ordenados por fecha
     */
    public List<Match> generate(List<Club> clubs, int seasonYear) {
        if (clubs == null || clubs.size() < 2) {
            throw new IllegalArgumentException(
                    "Una liga necesita al menos 2 clubes."
            );
        }

        //Usamos el año como seed.
        Random random = new Random(seasonYear);

        //Copiamos la lista para no modificar la lista original.
        List<Club> teams = new ArrayList<>(clubs);
        Collections.shuffle(teams, random);

        /*
         * Si hay una cantidad impar de equipos agregamos un BYE.
         * El equipo que se enfrenta a null tiene fecha libre.
         */
        if (teams.size() % 2 != 0) {
            teams.add(null);
        }

        int teamCount = teams.size();
        int roundsPerLeg = teamCount - 1;

        List<Match> fixture = new ArrayList<>();
        Map<String, Boolean> lastHome = new HashMap<>(); // Se utiliza para evitar rachas largas como local/visitante.

        // PRIMERA RUEDA
        for (int round = 1; round <= roundsPerLeg; round++) {

            fixture.addAll(
                    generateRound(
                            teams,
                            round,
                            false,
                            lastHome,
                            random
                    )
            );

            rotateTeams(teams);
        }
        
        // SEGUNDA RUEDA
        for (int round = 1; round <= roundsPerLeg; round++) {

            fixture.addAll(
                    generateRound(
                            teams,
                            roundsPerLeg + round,
                            true,
                            lastHome,
                            random
                    )
            );

            rotateTeams(teams);
        }

        return fixture;
    }

    /**
     * Genera una fecha completa.
     */
    private List<Match> generateRound(
            List<Club> teams,
            int round,
            boolean reverseHomeAway,
            Map<String, Boolean> lastHome,
            Random random) {

        List<Match> matches = new ArrayList<>();

        int half = teams.size() / 2;

        for (int i = 0; i < half; i++) {

            Club first = teams.get(i);
            Club second = teams.get(
                    teams.size() - 1 - i
            );

            /*
             * Si uno de los dos es null significa que tiene
             * fecha libre.
             */
            if (first == null || second == null) {
                continue;
            }

            /*
             * Determinamos inicialmente quién es local.
             *
             * La combinación de round + posición produce una
             * distribución inicial relativamente equilibrada.
             */
            boolean firstIsHome =
                    ((round + i) % 2 == 0);

            /*
             * Introducimos variación utilizando el Random
             * correspondiente a la temporada.
             */
            if (random.nextBoolean()) {
                firstIsHome = !firstIsHome;
            }

            Club home = firstIsHome
                    ? first
                    : second;

            Club away = firstIsHome
                    ? second
                    : first;

            /*
             * En la segunda rueda se invierte la localía.
             */
            if (reverseHomeAway) {
                Club temp = home;
                home = away;
                away = temp;
            }

            /*
             * Intentamos evitar que ambos equipos repitan
             * la misma condición de la fecha anterior.
             *
             * Ejemplo:
             *
             * A fue local
             * B fue visitante
             *
             * Si ahora A vuelve a ser local y B vuelve a ser
             * visitante, invertimos la localía.
             */
            Boolean homeWasHome =
                    lastHome.get(home.getId());

            Boolean awayWasHome =
                    lastHome.get(away.getId());

            boolean shouldSwap =
                    (Boolean.TRUE.equals(homeWasHome)
                            && Boolean.FALSE.equals(awayWasHome))
                    ||
                    (Boolean.FALSE.equals(homeWasHome)
                            && Boolean.TRUE.equals(awayWasHome));

            if (shouldSwap) {
                Club temp = home;
                home = away;
                away = temp;
            }

            /*
             * Guardamos la localía de ambos clubes para la
             * próxima fecha.
             */
            lastHome.put(home.getId(), true);
            lastHome.put(away.getId(), false);

            /*
             * Creamos el partido.
             */
            Match match = new Match(
                    home,
                    away,
                    MatchCompetition.LEAGUE,
                    MatchImportance.NORMAL,
                    round
            );

            matches.add(match);
        }

        return matches;
    }

    /**
     * Sistema de rotación circular.
     *
     * Mantiene fijo el primer club y rota el resto.
     */
    private void rotateTeams(List<Club> teams) {

        Club fixed = teams.get(0);

        List<Club> rotating = new ArrayList<>(
                teams.subList(1, teams.size())
        );

        Club last = rotating.remove(
                rotating.size() - 1
        );

        rotating.add(0, last);

        teams.clear();

        teams.add(fixed);
        teams.addAll(rotating);
    }
}
