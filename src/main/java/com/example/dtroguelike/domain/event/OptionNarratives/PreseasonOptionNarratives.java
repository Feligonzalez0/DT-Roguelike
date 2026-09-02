package com.example.dtroguelike.domain.event.OptionNarratives;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.example.dtroguelike.domain.manager.ManagerAttributeType;

public final class PreseasonOptionNarratives {

    private static final Map<ManagerAttributeType, List<String>> MESSAGES = Map.of(
        ManagerAttributeType.TACTICS, List.of(
            "Te anotás a una capacitación para subir tu licencia de entrenador.",
            "Pasás la pretemporada estudiando nuevos esquemas y variantes tácticas.",
            "Analizás partidos de entrenadores de élite buscando innovar en tus planteos."
        ),
        ManagerAttributeType.LEADERSHIP, List.of(
            "Participás de un seminario intensivo de liderazgo y manejo de grupos.",
            "Dedicás la pretemporada a fortalecer tu voz de mando frente al plantel.",
            "Trabajás junto a los referentes para solidificar el orden en el vestuario."
        ),
        ManagerAttributeType.MANAGEMENT, List.of(
            "Te reunís con la dirigencia para optimizar la estructura del club.",
            "Reorganizás las tareas de tu cuerpo técnico para ganar eficiencia en el día a día.",
            "Implementás un nuevo software para analizar las métricas y la fatiga del plantel."
        ),
        ManagerAttributeType.NEGOTIATION, List.of(
            "Estudiás técnicas de persuasión para convencer más fácil a futuros refuerzos.",
            "Te sentás con representantes de peso para aprender a destrabar contratos complicados.",
            "Dedicás tiempo a analizar el mercado de pases y pulir tu capacidad de regateo."
        ),
        ManagerAttributeType.YOUTH_DEVELOPMENT, List.of(
            "Pasás varias tardes observando entrenamientos de la Reserva y las inferiores.",
            "Diseñás un plan de transición para que los pibes se adapten rápido a Primera.",
            "Te reunís con los coordinadores de la cantera para alinear la metodología de trabajo."
        ),
        ManagerAttributeType.MOTIVATION, List.of(
            "Preparás un repertorio de charlas anímicas para los momentos críticos del torneo.",
            "Trabajás con un coach deportivo para aprender a levantar la moral tras una derrota.",
            "Organizás una jornada de convivencia para encender el hambre de gloria del equipo."
        )    
    );

    public static String randomMessage(ManagerAttributeType attribute, Random random) {
        List<String> messages = MESSAGES.get(attribute);

        if (messages == null || messages.isEmpty()) {
            return "Trabajás para mejorar " + attribute.getDisplayName() + ".";
        }

        return messages.get(random.nextInt(messages.size()));
    }
}