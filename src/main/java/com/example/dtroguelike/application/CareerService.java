package com.example.dtroguelike.application;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.offer.ClubOffer;
import com.example.dtroguelike.engine.CareerEngine;
import com.example.dtroguelike.engine.ClubOfferGenerator;
import com.example.dtroguelike.infrastructure.repository.CareerRepository;

import java.util.List;
import java.util.Optional;

/**
 * Caso de uso principal: crear la carrera, generar ofertas de clubes y
 * elegir un club. Es el puente entre los controllers web y el
 * CareerEngine / dominio.
 */
public class CareerService {

    private final CareerEngine careerEngine;
    private final ClubOfferGenerator clubOfferGenerator;
    private final CareerRepository careerRepository;

    public CareerService(CareerEngine careerEngine,
                          ClubOfferGenerator clubOfferGenerator,
                          CareerRepository careerRepository) {
        this.careerEngine = careerEngine;
        this.clubOfferGenerator = clubOfferGenerator;
        this.careerRepository = careerRepository;
    }

    /** Crea una nueva carrera para el Manager dado y la guarda como carrera activa. */
    public Career startNewCareer(Manager manager) {
        Career career = careerEngine.startCareer(manager);
        careerRepository.save(career);
        return career;
    }

    public Optional<Career> getCurrentCareer() {
        return careerRepository.findCurrent();
    }

    /** Genera entre 3 y 5 ofertas de clubes para la carrera activa. */
    public List<ClubOffer> generateOffers(Career career) {
        return clubOfferGenerator.generateOffers(career);
    }

    /** El jugador elige un club a partir de una oferta y arranca la temporada. */
    public void selectClub(Career career, Club club) {
        careerEngine.assignClub(career, club);
        careerRepository.save(career);
    }

    public void fireManager(Career career) {
        careerEngine.fireManager(career);
        careerRepository.save(career);
    }

    public void retireManager(Career career) {
        careerEngine.retireManager(career);
        careerRepository.save(career);
    }

    /** Descarta la carrera actual para poder comenzar una nueva desde cero. */
    public void resetCareer() {
        careerRepository.clear();
    }
}
