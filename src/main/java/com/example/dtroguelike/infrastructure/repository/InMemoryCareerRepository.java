package com.example.dtroguelike.infrastructure.repository;

import com.example.dtroguelike.domain.career.Career;

import java.util.Optional;

/**
 * Implementacion en memoria de {@link CareerRepository}. Como el MVP no
 * tiene usuarios ni login, alcanza con guardar una unica carrera activa
 * en una referencia mutable.
 */
public class InMemoryCareerRepository implements CareerRepository {

    private Career currentCareer;

    @Override
    public void save(Career career) {
        this.currentCareer = career;
    }

    @Override
    public Optional<Career> findCurrent() {
        return Optional.ofNullable(currentCareer);
    }

    @Override
    public void clear() {
        this.currentCareer = null;
    }
}
