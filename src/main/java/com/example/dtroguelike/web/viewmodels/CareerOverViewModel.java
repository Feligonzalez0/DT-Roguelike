package com.example.dtroguelike.web.viewmodels;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.career.CareerEndReason;

public class CareerOverViewModel {

    public final String managerName;
    public final String endReason;
    public final int finalReputation;
    public final String lastClubName;
    public final int finalSeason;

    public CareerOverViewModel(Career career) {
        if (career == null) {
            throw new IllegalStateException("No hay una carrera disponible.");
        }

        if (!career.isFinished()) {
            throw new IllegalStateException(
                    "La pantalla de carrera finalizada requiere una carrera terminada."
            );
        }

        CareerEndReason reason = career.getEndReason();

        if (reason == null) {
            throw new IllegalStateException(
                    "La carrera terminó sin un motivo de finalización."
            );
        }

        if (career.getCurrentClub() == null) {
            throw new IllegalStateException(
                    "No hay un último club disponible."
            );
        }

        if (career.getCurrentSeason() == null) {
            throw new IllegalStateException(
                    "No hay una temporada final disponible."
            );
        }

        this.managerName = career.getManager().getName();
        this.endReason = reason.getMessage();
        this.finalReputation = career.getManager().getReputation();
        this.lastClubName = career.getCurrentClub().getName();
        this.finalSeason = career.getCurrentSeason().getYear();
    }
}