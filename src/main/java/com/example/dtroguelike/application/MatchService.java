package com.example.dtroguelike.application;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.match.MatchResult;
import com.example.dtroguelike.engine.MatchSimulator;
import com.example.dtroguelike.infrastructure.repository.CareerRepository;

/**
 * Caso de uso: simular un partido individual (por ejemplo, un partido
 * importante con decisiones del jugador). Para el flujo simplificado
 * del MVP, {@link SeasonService} ya cubre la simulacion de partidos
 * normales; este servicio queda preparado para partidos especiales.
 */
public class MatchService {

    private final MatchSimulator matchSimulator;
    private final CareerRepository careerRepository;

    public MatchService(MatchSimulator matchSimulator, CareerRepository careerRepository) {
        this.matchSimulator = matchSimulator;
        this.careerRepository = careerRepository;
    }

    public MatchResult simulate(Match match, Career career) {
        MatchResult result = matchSimulator.simulate(match, career);
        match.setResult(result);
        careerRepository.save(career);
        return result;
    }
}
