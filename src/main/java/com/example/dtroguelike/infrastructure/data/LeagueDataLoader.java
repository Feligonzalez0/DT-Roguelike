package com.example.dtroguelike.infrastructure.data;

import com.example.dtroguelike.domain.league.League;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Carga las ligas desde {@code data/leagues.json}. Los clubes se
 * agregan despues, desde {@link ClubDataLoader}, para mantener cada
 * loader responsable de un unico archivo.
 */
public class LeagueDataLoader {

    private final Gson gson = new Gson();

    public Map<String, League> loadLeagues(String resourcePath) {
        Map<String, League> leaguesById = new HashMap<>();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("No se encontro el recurso: " + resourcePath);
            }
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Type listType = new TypeToken<List<LeagueDto>>() {}.getType();
            List<LeagueDto> dtos = gson.fromJson(reader, listType);
            if (dtos == null) {
                dtos = new ArrayList<>();
            }
            for (LeagueDto dto : dtos) {
                League league = new League(dto.id, dto.name, dto.country, dto.level, dto.strength);
                leaguesById.put(league.getId(), league);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error cargando ligas desde " + resourcePath, e);
        }
        return leaguesById;
    }
}
