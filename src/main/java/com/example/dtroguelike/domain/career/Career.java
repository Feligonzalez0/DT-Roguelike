package com.example.dtroguelike.domain.career;

import com.example.dtroguelike.domain.club.Club;
import com.example.dtroguelike.domain.club.ClubState;
import com.example.dtroguelike.domain.common.GamePhase;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.offer.ClubOffer;
import com.example.dtroguelike.domain.season.Season;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Clase central que representa una carrera completa de un Director
 * Tecnico: su Manager, el club actual, la temporada en curso, el
 * historial de clubes y su legado.
 */
public class Career {

    private final String id;
    private final Manager manager;
    private Club currentClub;
    private ClubState currentClubState;
    private Season currentSeason;
    private CareerState state;
    private GamePhase phase;
    private final Legacy legacy;
    private final List<ClubHistory> clubHistory = new ArrayList<>();
    private int contractRemainingYears;
    private ClubOffer pendingRenewalOffer;

    public Career(Manager manager) {
        this.id = UUID.randomUUID().toString();
        this.manager = manager;
        this.state = CareerState.CREATING_MANAGER;
        this.phase = GamePhase.MANAGER_CREATION;
        this.legacy = new Legacy();
        this.pendingRenewalOffer = null;
    }

    public String getId() {
        return id;
    }

    public Manager getManager() {
        return manager;
    }

    public Club getCurrentClub() {
        return currentClub;
    }

    public ClubState getCurrentClubState() {
        return currentClubState;
    }

    /** Asigna un nuevo club (y crea su estado inicial de carrera) al Manager. */
    public void assignClub(Club club) {
        this.currentClub = club;
        this.currentClubState = club.createInitialState();
    }

    public void clearClub() {
        this.currentClub = null;
        this.currentClubState = null;
    }

    public Season getCurrentSeason() {
        return currentSeason;
    }

    public void setCurrentSeason(Season currentSeason) {
        this.currentSeason = currentSeason;
    }

    public CareerState getState() {
        return state;
    }

    public void setState(CareerState state) {
        this.state = state;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public void setPhase(GamePhase phase) {
        this.phase = phase;
    }

    public Legacy getLegacy() {
        return legacy;
    }

    public List<ClubHistory> getClubHistory() {
        return clubHistory;
    }

    public void addClubHistory(ClubHistory history) {
        clubHistory.add(history);
    }

    // METODOS CONTRATO
    public int getContractRemainingYears(){
        return this.contractRemainingYears;
    }

    public void setContractRemainingYears(int value){
        this.contractRemainingYears = value;
    }

    public void decrementContractRemainingYears(){
        if (contractRemainingYears == 0) throw new IllegalStateException("El contrato ya finalizó. No pueden quedar años restantes negativos.");
        contractRemainingYears--;
    }

    public boolean hasActiveContract(){
        return contractRemainingYears > 0;
    }

    public boolean isContractExpired(){
        return contractRemainingYears == 0;
    }

    public boolean isFinished() {
        return state == CareerState.RETIRED || state == CareerState.FINISHED;
    }

    public ClubOffer getPendingRenewalOffer(){
        return pendingRenewalOffer;
    }

    public void setPendingRenewalOffer(ClubOffer offer){
        this.pendingRenewalOffer = offer;
    }
}
