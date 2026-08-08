package com.example.dtroguelike.config;

import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.event.Event;
import com.example.dtroguelike.domain.league.League;
import com.example.dtroguelike.infrastructure.data.ClubDataLoader;
import com.example.dtroguelike.infrastructure.data.EventDataLoader;
import com.example.dtroguelike.infrastructure.data.LeagueDataLoader;

import java.util.List;
import java.util.Map;

/**
 * Contenedor simple de los datos estaticos del juego (ligas, clubes,
 * eventos), cargados una unica vez al iniciar la aplicacion desde los
 * archivos JSON en {@code src/main/resources/data}.
 */
public class GameData {

    private final Map<String, League> leaguesById;
    private final List<Club> clubs;
    private final List<Event> events;

    private GameData(Map<String, League> leaguesById, List<Club> clubs, List<Event> events) {
        this.leaguesById = leaguesById;
        this.clubs = clubs;
        this.events = events;
    }

    public static GameData load() {
        LeagueDataLoader leagueDataLoader = new LeagueDataLoader();
        ClubDataLoader clubDataLoader = new ClubDataLoader();
        EventDataLoader eventDataLoader = new EventDataLoader();

        Map<String, League> leaguesById = leagueDataLoader.loadLeagues("data/leagues.json");
        List<Club> clubs = clubDataLoader.loadClubs("data/clubs.json", leaguesById);
        List<Event> events = eventDataLoader.loadEvents("data/events.json");

        return new GameData(leaguesById, clubs, events);
    }

    public List<League> getLeagues() {
        return leaguesById.values().stream().toList();
    }

    public List<Club> getClubs() {
        return clubs;
    }

    public List<Event> getEvents() {
        return events;
    }
}
