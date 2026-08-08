package com.example.dtroguelike.domain.manager;

/**
 * Estilo elegido por el jugador para su Director Tecnico.
 * Cada estilo otorga un set de atributos iniciales distinto.
 */
public enum ManagerStyle {
    STRATEGIST("Estratega", "Prioriza la tactica y el analisis del rival."),
    LEADER("Lider", "Prioriza el liderazgo y la conduccion del plantel."),
    MANAGER("Gestor", "Prioriza la gestion institucional y la negociacion."),
    DEVELOPER("Formador", "Prioriza el desarrollo de juveniles."),
    MOTIVATOR("Motivador", "Prioriza la motivacion y el clima interno.");

    private final String displayName;
    private final String description;

    ManagerStyle(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Genera los atributos iniciales del Manager segun su estilo.
     * Valores simples y explicables: un atributo "fuerte" (70), dos
     * "moderados" (55) y el resto en el valor por defecto.
     */
    public ManagerAttributes initialAttributes() {
        ManagerAttributes attributes = ManagerAttributes.defaults();
        switch (this) {
            case STRATEGIST -> {
                attributes.setTactics(75);
                attributes.setManagement(55);
            }
            case LEADER -> {
                attributes.setLeadership(75);
                attributes.setMotivation(55);
            }
            case MANAGER -> {
                attributes.setManagement(75);
                attributes.setNegotiation(60);
            }
            case DEVELOPER -> {
                attributes.setYouthDevelopment(75);
                attributes.setTactics(55);
            }
            case MOTIVATOR -> {
                attributes.setMotivation(75);
                attributes.setLeadership(60);
            }
        }
        return attributes;
    }
}
