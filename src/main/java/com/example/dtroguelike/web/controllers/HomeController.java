package com.example.dtroguelike.web.controllers;

import java.util.Map;

/**
 * Controlador de la pagina de inicio.
 */
public class HomeController {

    public Map<String, Object> index() {
        return Map.of(
                "gameTitle", "DT Roguelike",
                "tagline", "Construi tu carrera como Director Tecnico. Una decision equivocada puede terminarla."
        );
    }
}
