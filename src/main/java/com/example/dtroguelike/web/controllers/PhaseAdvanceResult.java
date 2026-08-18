package com.example.dtroguelike.web.controllers;

import com.example.dtroguelike.domain.career.Career;

public record PhaseAdvanceResult(
        Career career,
        CareerDestination destination
) {
}