package com.example.dtroguelike.engine;

import com.example.dtroguelike.domain.career.Career;
import com.example.dtroguelike.domain.manager.ManagerAttributes;

/**
 * Encargado de la progresion (crecimiento) de los atributos del
 * Manager a lo largo del tiempo. Para el MVP solo se ofrece un
 * crecimiento minimo y previsible; el sistema completo (experiencia,
 * curva de edad, especializacion, etc.) queda pendiente.
 */
public class ProgressionEngine {

    /**
     * Aplica un crecimiento simple de atributos al finalizar una
     * temporada. Por ahora suma un punto fijo a tactica y liderazgo
     * como placeholder.
     *
     * TODO: implementar una progresion real basada en resultados,
     * estilo del DT, edad y experiencia acumulada.
     */
    public void applyManagerGrowth(Career career) {
        if (career == null || career.getManager() == null) {
            return;
        }
        ManagerAttributes attributes = career.getManager().getAttributes();
        attributes.addTactics(1);
        attributes.addLeadership(1);
    }
}
