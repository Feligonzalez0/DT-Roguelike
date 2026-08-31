package com.example.dtroguelike.application;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.career.CareerEndReason;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.offer.ClubOffer;
import com.example.dtroguelike.engine.CareerEngine;
import com.example.dtroguelike.engine.ClubOfferGenerator;
import com.example.dtroguelike.infrastructure.repository.CareerRepository;
import com.example.dtroguelike.web.controllers.ClubController;

import java.util.ArrayList;
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
    private List<ClubOffer> currentOffers = new ArrayList<>();

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
        currentOffers = clubOfferGenerator.generateOffers(career);
        careerRepository.save(career);
        return career;
    }

    public Optional<Career> getCurrentCareer() {
        return careerRepository.findCurrent();
    }

    public List<ClubOffer> getCurrentOffers() {
        return new ArrayList<>(currentOffers);
    }

    /** Genera entre 3 y 5 ofertas de clubes para la carrera activa. */
    public List<ClubOffer> generateOffers(Career career) {
        currentOffers = clubOfferGenerator.generateOffers(career);
        return new ArrayList<>(currentOffers);
    }

    /** El jugador elige un club a partir de una oferta y arranca la temporada. */
    public void selectClub(Career career, Club club, int contractLength) {
        careerEngine.assignClub(career, club, contractLength);
        currentOffers.clear();

        careerRepository.save(career);
    }

    public List<ClubOffer> fireManager(Career career) {
        Club previousClub = career.getCurrentClub();

        careerEngine.fireManager(career);

        return generateOffersAfterDeparture(career, previousClub);
    }

    public void retireManager(Career career) {
        careerEngine.retireManager(career);
        careerRepository.save(career);
    }

    public void continueAtCurrentClub(Career career) {
        careerEngine.continueAtCurrentClub(career);
        careerRepository.save(career);
    }

    public List<ClubOffer> resign(Career career) {
        Club previousClub = career.getCurrentClub();

        careerEngine.resignManager(career);

        return generateOffersAfterDeparture(career, previousClub);
    }

    public boolean evaluatePossibleFiring(Career career) {
        boolean fired = careerEngine.evaluatePossibleFiring(career);

        if (fired) {
            careerRepository.save(career);
        }

        return fired;
    }
    
    public ClubOffer getRenewalOffer(Career career) {
        if (career.getPendingRenewalOffer() != null) {
            return career.getPendingRenewalOffer();
        }

        ClubOffer offer =
                clubOfferGenerator.generateRenewalOffer(career);

        career.setPendingRenewalOffer(offer);

        return offer;
    }

    public void acceptRenewal(Career career) {
        ClubOffer renewalOffer = career.getPendingRenewalOffer();

        if (renewalOffer == null) {
            throw new IllegalStateException(
                    "No existe una oferta de renovación válida."
            );
        }

        careerEngine.acceptRenewal(career, renewalOffer);
        careerRepository.save(career);
    }

    public List<ClubOffer> rejectRenewal(Career career) {
        Club previousClub = career.getCurrentClub();

        careerEngine.rejectRenewal(career);

        return generateOffersAfterDeparture(career, previousClub);
    }

    public List<ClubOffer> generateOffersAfterDeparture(
            Career career,
            Club previousClub) {

        currentOffers =
                clubOfferGenerator.generateOffers(career, previousClub);

        if (currentOffers.isEmpty()) {
            career.finish(CareerEndReason.NO_OFFERS);
        }

        careerRepository.save(career);

        return new ArrayList<>(currentOffers);
    }

    /*@pre: debe ejecutarse luego de generateOffers(). */
    public SeasonEndResult processSeasonEnd(Career career) {
        if (currentOffers.isEmpty()) {
            career.finish(CareerEndReason.NO_OFFERS);
            careerRepository.save(career);
            return SeasonEndResult.CAREER_OVER;
        }

        return SeasonEndResult.CONTINUE;
    }

    /** Descarta la carrera actual para poder comenzar una nueva desde cero. */
    public void resetCareer() {
        careerRepository.clear();
    }
}
