package com.example.dtroguelike.infrastructure.data;

import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.TeamStrength;
import com.example.dtroguelike.domain.league.League;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Carga los clubes desde {@code data/clubs.json} y los asocia a la
 * liga correspondiente (por {@code leagueId}).
 */
public class ClubDataLoader {

    private final Gson gson = new Gson();

    public List<Club> loadClubs(String resourcePath, Map<String, League> leaguesById) {
        List<Club> clubs = new ArrayList<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("No se encontro el recurso: " + resourcePath);
            }
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<ClubDto>>() {}.getType();
            List<ClubDto> dtos = gson.fromJson(reader, listType);
            if (dtos == null) {
                dtos = new ArrayList<>();
            }
            for (ClubDto dto : dtos) {
                League league = leaguesById.get(dto.leagueId);
                String leagueName = league != null ? league.getName() : dto.leagueId;
                TeamStrength strength = new TeamStrength(dto.attack, dto.midfield, dto.defense);
                Club club = new Club(dto.id, dto.name, dto.country, leagueName, dto.reputation, strength);
                clubs.add(club);
                if (league != null) {
                    league.addClub(club);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error cargando clubes desde " + resourcePath, e);
        }
        return clubs;
    }
}
