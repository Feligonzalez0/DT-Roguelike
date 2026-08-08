package com.example.dtroguelike.web.controllers;

import com.example.dtroguelike.application.CareerService;
import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.offer.ClubOffer;
import com.example.dtroguelike.web.viewmodels.ClubOfferViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Controlador para mostrar ofertas de clubes y procesar la eleccion
 * del jugador.
 *
 * Como todavia no hay persistencia ni usuarios, las ofertas generadas
 * se guardan temporalmente en memoria (un unico set de ofertas activo,
 * igual que la carrera activa) para que la pantalla de seleccion y el
 * submit del formulario trabajen sobre la misma lista.
 */
public class ClubController {

    private final CareerService careerService;
    private List<ClubOffer> currentOffers = new ArrayList<>();

    public ClubController(CareerService careerService) {
        this.careerService = careerService;
    }

    public Map<String, Object> showOffers() {
        Career career = requireCurrentCareer();
        currentOffers = careerService.generateOffers(career);

        List<ClubOfferViewModel> offerViewModels = new ArrayList<>();
        for (ClubOffer offer : currentOffers) {
            offerViewModels.add(new ClubOfferViewModel(offer));
        }
        return Map.of(
                "managerName", career.getManager().getName(),
                "offers", offerViewModels
        );
    }

    public Career selectClub(String clubId) {
        Career career = requireCurrentCareer();
        ClubOffer chosen = currentOffers.stream()
                .filter(offer -> offer.getClub().getId().equals(clubId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Oferta no encontrada para el club: " + clubId));

        careerService.selectClub(career, chosen.getClub());
        return career;
    }

    private Career requireCurrentCareer() {
        Optional<Career> career = careerService.getCurrentCareer();
        return career.orElseThrow(() -> new IllegalStateException("No hay una carrera activa."));
    }
}
