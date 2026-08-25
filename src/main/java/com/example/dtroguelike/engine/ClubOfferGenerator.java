package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.ClubExpectations;
import com.example.dtroguelike.domain.common.GameConstants;
import com.example.dtroguelike.domain.offer.ClubOffer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Genera ofertas de club para el Manager segun su reputacion.
 * Implementacion simple: ordena los clubes disponibles por que tan
 * "compatibles" son con la reputacion actual del DT y selecciona un
 * grupo de entre {@code MIN_INITIAL_OFFERS} y {@code MAX_INITIAL_OFFERS}.
 */
public class ClubOfferGenerator {

    private final List<Club> allClubs;
    private final Random random;

    public ClubOfferGenerator(List<Club> allClubs) {
        this(allClubs, new Random());
    }

    public ClubOfferGenerator(List<Club> allClubs, Random random) {
        this.allClubs = allClubs;
        this.random = random;
    }

    public List<ClubOffer> generateOffers(Career career) {
        return generateOffers(career, null);
    }

    public List<ClubOffer> generateOffers(Career career, Club excludedClub) {
        List<ClubOffer> offers = new ArrayList<>();

        if (allClubs == null || allClubs.isEmpty()) {
            return offers;
        }

        int reputation = career.getManager().getReputation();

        List<Club> candidates = new ArrayList<>(allClubs.stream()
                .filter(club -> excludedClub == null
                        || !club.getId().equals(excludedClub.getId()))
                .filter(club -> Math.abs(club.getReputation() - reputation)
                        <= GameConstants.MAX_OFFER_REPUTATION_DISTANCE)
                .sorted(Comparator.comparingInt(
                        club -> Math.abs(club.getReputation() - reputation)))
                .toList());

        int offerCount = GameConstants.MIN_INITIAL_OFFERS + random.nextInt(GameConstants.MAX_INITIAL_OFFERS - GameConstants.MIN_INITIAL_OFFERS + 1);
        
        // Si no hay suficientes clubes dentro del rango normal,
        // buscamos clubes de menor reputación como fallback.
        if (candidates.size() < offerCount) {
            List<Club> fallbackCandidates = allClubs.stream()
                    .filter(club -> excludedClub == null
                            || !club.getId().equals(excludedClub.getId()))
                    .filter(club -> club.getReputation() < reputation)
                    .filter(club -> !candidates.contains(club))
                    .sorted(Comparator.comparingInt(
                            club -> reputation - club.getReputation()))
                    .toList();

            for (Club club : fallbackCandidates) {
                if (candidates.size() >= offerCount) {
                    break;
                }

                candidates.add(club);
            }
        }

        offerCount = Math.min(offerCount, candidates.size());

        for (int i = 0; i < offerCount; i++) {
            Club club = candidates.get(i);
            offers.add(buildOffer(club, reputation));
        }

        return offers;
    }

    private ClubOffer buildOffer(Club club, int managerReputation) {
        long baseSalary = 20_000L + (long) club.getReputation() * 1_000L;
        int contractLength = 1 + random.nextInt(3); // 1 a 3 años
        ClubExpectations expectations = club.getExpectations();
        int jobSecurity = GameConstants.clamp(
                GameConstants.DEFAULT_JOB_SECURITY + (managerReputation - club.getReputation()) / 2,
                GameConstants.MIN_JOB_SECURITY, GameConstants.MAX_JOB_SECURITY);

        return new ClubOffer(club, baseSalary, contractLength, expectations, jobSecurity);
    }

    public ClubOffer generateRenewalOffer(Career career) {
        if (career.getCurrentClub() == null) {
            return null;
        }

        if (career.getContractRemainingYears() > 0) {
            return null;
        }

        if (!shouldOfferRenewal(career)) {
            return null;
        }

        Club club = career.getCurrentClub();
        int managerReputation = career.getManager().getReputation();

        return buildOffer(club, managerReputation);
    }

    private boolean shouldOfferRenewal(Career career) {
        int jobSecurity = career.getCurrentClubState().getJobSecurity();

        boolean objectiveCompleted = career.getCurrentSeason().getObjectiveResult().isObjectiveCompleted();

        int chance;

        if (jobSecurity < GameConstants.RENEWAL_LOW_JOB_SECURITY) {
            chance = GameConstants.RENEWAL_LOW_CHANCE;
        } else if (objectiveCompleted
                && jobSecurity >= GameConstants.RENEWAL_HIGH_JOB_SECURITY) {
            chance = GameConstants.RENEWAL_HIGH_CHANCE;
        } else if (objectiveCompleted
                || jobSecurity >= GameConstants.RENEWAL_HIGH_JOB_SECURITY) {
            chance = GameConstants.RENEWAL_MEDIUM_CHANCE;
        } else {
            chance = GameConstants.RENEWAL_LOW_CHANCE;
        }

        return random.nextInt(100) < chance;
    }
}
