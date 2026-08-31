package com.example.dtroguelike.web.controllers;

import com.example.dtroguelike.application.CareerService;
import com.example.dtroguelike.application.SeasonService;
import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.match.Match;
import com.example.dtroguelike.domain.offer.ClubOffer;
import com.example.dtroguelike.domain.season.SeasonPhase;
import com.example.dtroguelike.web.viewmodels.CareerOverViewModel;
import com.example.dtroguelike.web.viewmodels.DashboardViewModel;
import com.example.dtroguelike.web.viewmodels.EmploymentViewModel;
import com.example.dtroguelike.web.viewmodels.SeasonSummaryViewModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador del dashboard de la carrera: muestra el estado actual y
 * permite avanzar la temporada (simular el siguiente partido).
 */
public class CareerController {

    private final CareerService careerService;
    private final SeasonService seasonService;

    public CareerController(CareerService careerService, SeasonService seasonService) {
        this.careerService = careerService;
        this.seasonService = seasonService;
    }

    public Map<String, Object> showDashboard() {
        Career career = requireCurrentCareer();
        DashboardViewModel viewModel = new DashboardViewModel(career);
        return Map.of("dashboard", viewModel);
    }

    public Career finishSeason() {
        Career career = requireCurrentCareer();
        seasonService.finishSeason(career);
        return career;
    }

    /**
     * Completa la transición del botón "Finalizar temporada":
     * simula/cierra la temporada y deja al jugador directamente en SUMMARY.
     */
    public Career finishSeasonAndShowSummary() {
        Career career = finishSeason();
        seasonService.showSeasonSummary(career);
        return career;
    }

    public Map<String, Object> showSeasonSummary() {
        Career career = requireCurrentCareer();
        SeasonSummaryViewModel viewModel = new SeasonSummaryViewModel(career);
        return Map.of("summary", viewModel);
    }

    public PhaseAdvanceResult advancePhase() {
        Career career = requireCurrentCareer();

        switch (career.getCurrentSeason().getPhase()) {
            case PRESEASON ->
                    seasonService.startTransferWindow(career);
            case TRANSFER_WINDOW ->
                    seasonService.startRegularSeason(career);
            case END_OF_SEASON ->
                    seasonService.showSeasonSummary(career);
            default ->
                    throw new IllegalStateException(
                            "No se puede avanzar automáticamente desde la fase "
                                    + career.getCurrentSeason().getPhase());
        }

        CareerDestination destination = career.getCurrentSeason().getPhase() == SeasonPhase.SUMMARY
                    ? CareerDestination.SEASON_SUMMARY
                    : CareerDestination.DASHBOARD;

        return new PhaseAdvanceResult(career, destination);
    }    

    /** Simula el proximo partido pendiente del club dirigido (solo durante REGULAR_SEASON). */
    public List<Match> simulateNextMatch() {
        Career career = requireCurrentCareer();
        return seasonService.simulateNextMatch(career);
    }

    private Career requireCurrentCareer() {
        Optional<Career> career = careerService.getCurrentCareer();
        return career.orElseThrow(() -> new IllegalStateException("No hay una carrera activa."));
    }

    public Career startNextSeason() {
        Career career = requireCurrentCareer();
        seasonService.startNextSeason(career);
        return career;
    }

    public Career continueAtCurrentClub() {
        Career career = requireCurrentCareer();
        careerService.continueAtCurrentClub(career);
        return career;
    }

    public List<ClubOffer> resign() {
        Career career = requireCurrentCareer();
        return careerService.resign(career);
    }

    public boolean evaluatePossibleFiring() {
        Career career = requireCurrentCareer();
        return careerService.evaluatePossibleFiring(career);
    }

    public ClubOffer showRenewalOffer() {
        Career career = requireCurrentCareer();
        return careerService.getRenewalOffer(career);
    }

    public Career acceptRenewal() {
        Career career = requireCurrentCareer();
        careerService.acceptRenewal(career);
        return career;
    }

    public List<ClubOffer> rejectRenewal() {
        Career career = requireCurrentCareer();
        return careerService.rejectRenewal(career);
    }

    public Map<String, Object> showEmploymentSituation() {
        Career career = requireCurrentCareer();

        ClubOffer renewalOffer = careerService.getRenewalOffer(career);

        EmploymentViewModel viewModel =
                new EmploymentViewModel(
                        career,
                        renewalOffer
                );

        return Map.of(
                "employment",
                viewModel
        );
    }

    // CAREER OVER
    public boolean isCareerOver() {
        return requireCurrentCareer().isFinished();
    }
    
    public Map<String, Object> showCareerOver() {
        Career career = requireCurrentCareer();
        CareerOverViewModel viewModel = new CareerOverViewModel(career);

        return Map.of(
                "careerOver",
                viewModel
        );
    }
}
