package com.example.dtroguelike.domain.career;

/**
 * Motivos por los que una carrera puede finalizar.
 */
public enum CareerEndReason {

    NO_OFFERS("Ningún club está interesado en contratarte.");

    private final String message;

    CareerEndReason(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}