package com.example.dtroguelike.web.viewmodels;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchState;
import com.example.dtroguelike.domain.season.Season;
import com.example.dtroguelike.domain.season.SeasonPhase;

import java.util.ArrayList;
import java.util.List;

/**
 * Read-only projection of a completed season for the season summary screen.
 *
 * This class composes existing domain state (SeasonStats, StandingsTable,
 * Match and Club) and does not introduce a second source of truth.
 */
public class SeasonSummaryViewModel {
    public final int seasonYear;
    public final String managerName;
    public final String clubName;
    public final String phase;
    public final int finalPosition;
    public final String finalPositionLabel;
    public final boolean champion;

    public final int matchesPlayed;
    public final int wins;
    public final int draws;
    public final int losses;
    public final int goalsFor;
    public final int goalsAgainst;
    public final int goalDifference;
    public final int points;

    public final List<StandingsEntryViewModel> standings;
    public final List<FixtureMatchViewModel> recentMatches;
    public final String seasonAssessment;

    public SeasonSummaryViewModel(Career career) {
        if (career == null || career.getCurrentSeason() == null) {
            throw new IllegalStateException("No hay una temporada activa.");
        }
        if (career.getCurrentClub() == null) {
            throw new IllegalStateException("No hay un club dirigido actualmente.");
        }

        Season season = career.getCurrentSeason();
        Club managedClub = career.getCurrentClub();

        if (season.getPhase() != SeasonPhase.SUMMARY) {
            throw new IllegalStateException(
                    "El resumen solo puede construirse desde SUMMARY (fase actual: "
                            + season.getPhase() + ")."
            );
        }
        if (season.getStandings() == null) {
            throw new IllegalStateException("No hay una tabla de posiciones disponible.");
        }

        ensureSeasonIsComplete(season);

        var standing = season.getStandings().getEntry(managedClub.getId());
        var stats = season.getStats();

        if (stats.getMatchesPlayed() != standing.getPlayed()
                || stats.getWins() != standing.getWins()
                || stats.getDraws() != standing.getDraws()
                || stats.getLosses() != standing.getLosses()
                || stats.getGoalsFor() != standing.getGoalsFor()
                || stats.getGoalsAgainst() != standing.getGoalsAgainst()
                || stats.getPoints() != standing.getPoints()) {
            throw new IllegalStateException(
                    "Las estadísticas de la temporada no coinciden con la tabla final."
            );
        }

        this.seasonYear = season.getYear();
        this.managerName = career.getManager().getName();
        this.clubName = managedClub.getName();
        this.phase = season.getPhase().name();

        this.finalPosition = standing.getPosition();
        this.champion = finalPosition == 1;
        this.finalPositionLabel = champion ? "CAMPEÓN" : finalPosition + "°";

        this.matchesPlayed = stats.getMatchesPlayed();
        this.wins = stats.getWins();
        this.draws = stats.getDraws();
        this.losses = stats.getLosses();
        this.goalsFor = stats.getGoalsFor();
        this.goalsAgainst = stats.getGoalsAgainst();
        this.goalDifference = goalsFor - goalsAgainst;
        this.points = stats.getPoints();

        this.standings = new StandingsViewModel(season, managedClub).entries;
        this.recentMatches = lastFiveManagedMatches(season, managedClub.getId());
        this.seasonAssessment = buildAssessment(finalPosition, standings.size(), champion);
    }

    private void ensureSeasonIsComplete(Season season) {
        for (List<Match> round : season.getFixture()) {
            for (Match match : round) {
                if (match.getState() != MatchState.FINISHED) {
                    throw new IllegalStateException(
                            "No se puede construir el resumen: la temporada está incompleta."
                    );
                }
            }
        }
    }

    private List<FixtureMatchViewModel> lastFiveManagedMatches(
            Season season,
            String managedClubId) {

        List<FixtureMatchViewModel> playedMatches = new ArrayList<>();

        for (List<Match> round : season.getFixture()) {
            for (Match match : round) {
                boolean managedMatch =
                        match.getHomeTeam().getId().equals(managedClubId)
                                || match.getAwayTeam().getId().equals(managedClubId);

                if (managedMatch && match.getState() == MatchState.FINISHED) {
                    playedMatches.add(new FixtureMatchViewModel(match, managedClubId));
                }
            }
        }

        int fromIndex = Math.max(0, playedMatches.size() - 5);
        return List.copyOf(playedMatches.subList(fromIndex, playedMatches.size()));
    }

    private String buildAssessment(int position, int leagueSize, boolean champion) {
        if (champion) {
            return "¡Felicitaciones, el equipo salió campeón!";
        }

        int topCutoff = Math.max(1, (int) Math.ceil(leagueSize / 3.0));
        int bottomCutoff = Math.max(topCutoff, (int) Math.floor((leagueSize * 2.0) / 3.0));

        if (position <= topCutoff) {
            return "Excelente temporada. El equipo terminó entre los primeros puestos.";
        }
        if (position > bottomCutoff) {
            return "Temporada complicada. El equipo terminó en la parte baja de la tabla.";
        }
        return "Temporada aceptable. El equipo terminó en la zona media de la tabla.";
    }
}
