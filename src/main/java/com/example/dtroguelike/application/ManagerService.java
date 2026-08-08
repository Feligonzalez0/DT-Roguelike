package com.example.dtroguelike.application;

import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.manager.ManagerStyle;

/**
 * Caso de uso: creacion del Director Tecnico controlado por el jugador.
 */
public class ManagerService {

    public Manager createManager(String name, int age, String nationality, ManagerStyle style) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del DT no puede estar vacio");
        }
        if (style == null) {
            throw new IllegalArgumentException("Debe seleccionarse un estilo de DT");
        }
        return new Manager(name.trim(), age, nationality, style);
    }
}
