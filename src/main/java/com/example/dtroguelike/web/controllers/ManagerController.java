package com.example.dtroguelike.web.controllers;

import com.example.dtroguelike.application.CareerService;
import com.example.dtroguelike.application.ManagerService;
import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.manager.Manager;
import com.example.dtroguelike.domain.manager.ManagerStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la creacion del Director Tecnico y el arranque de
 * una nueva carrera.
 */
public class ManagerController {

    private final ManagerService managerService;
    private final CareerService careerService;

    public ManagerController(ManagerService managerService, CareerService careerService) {
        this.managerService = managerService;
        this.careerService = careerService;
    }

    /** Datos para renderizar el formulario de creacion. */
    public Map<String, Object> createForm() {
        List<Map<String, String>> styles = new ArrayList<>();
        for (ManagerStyle style : ManagerStyle.values()) {
            styles.add(Map.of(
                    "value", style.name(),
                    "label", style.getDisplayName(),
                    "description", style.getDescription()
            ));
        }
        return Map.of("styles", styles);
    }

    /**
     * Procesa el formulario de creacion: crea el Manager, arranca la
     * carrera y la deja como carrera activa.
     */
    public Career handleCreate(String name, String ageRaw, String nationality, String styleRaw) {
        int age = parseAgeOrDefault(ageRaw);
        ManagerStyle style = ManagerStyle.valueOf(styleRaw);
        Manager manager = managerService.createManager(name, age, nationality, style);
        return careerService.startNewCareer(manager);
    }

    private int parseAgeOrDefault(String ageRaw) {
        try {
            return Integer.parseInt(ageRaw);
        } catch (Exception e) {
            return com.example.dtroguelike.domain.common.GameConstants.DEFAULT_MANAGER_AGE;
        }
    }
}
