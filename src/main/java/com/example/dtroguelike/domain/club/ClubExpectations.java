package com.example.dtroguelike.domain.club;

/**
 * Expectativas depositadas en el DT para una temporada dada.
 * Preparada para un sistema de objetivos mas completo en el futuro;
 * por ahora contiene datos simples.
 */
public class ClubExpectations {

    private int boardExpectation;   // 0-100, exigencia de la directiva
    private int fansExpectation;    // 0-100, exigencia de la hinchada
    private int minimumExpectedPosition; // posicion minima aceptable en la liga
    private boolean expectedToWinLeague;

    public ClubExpectations(int boardExpectation, int fansExpectation,
                             int minimumExpectedPosition, boolean expectedToWinLeague) {
        this.boardExpectation = boardExpectation;
        this.fansExpectation = fansExpectation;
        this.minimumExpectedPosition = minimumExpectedPosition;
        this.expectedToWinLeague = expectedToWinLeague;
    }

    public static ClubExpectations basedOnReputation(int clubReputation) {
        // Regla simple: a mayor reputacion del club, mayor exigencia.
        int board = GameMath.clampToPercentage(clubReputation);
        int fans = GameMath.clampToPercentage(clubReputation + 10);
        int minPosition = clubReputation > 70 ? 3 : (clubReputation > 40 ? 8 : 15);
        boolean mustWinLeague = clubReputation > 85;
        return new ClubExpectations(board, fans, minPosition, mustWinLeague);
    }

    public int getBoardExpectation() {
        return boardExpectation;
    }

    public int getFansExpectation() {
        return fansExpectation;
    }

    public int getMinimumExpectedPosition() {
        return minimumExpectedPosition;
    }

    public boolean isExpectedToWinLeague() {
        return expectedToWinLeague;
    }

    /** Pequeño helper interno para no depender de GameConstants aca. */
    private static final class GameMath {
        static int clampToPercentage(int value) {
            return Math.max(0, Math.min(100, value));
        }
    }
}
