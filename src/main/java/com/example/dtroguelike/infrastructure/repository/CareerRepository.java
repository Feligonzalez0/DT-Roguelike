package com.example.dtroguelike.infrastructure.repository;

import com.example.dtroguelike.domain.career.Career;

import java.util.Optional;

/**
 * Abstraccion de persistencia de la carrera actual. Al no haber
 * usuarios ni base de datos en el MVP, solo existe una carrera activa
 * a la vez, pero mantener esta interfaz permite agregar SQLite (u otra
 * persistencia) mas adelante sin tocar la logica del juego.
 */
public interface CareerRepository {

    void save(Career career);

    Optional<Career> findCurrent();

    void clear();
}
