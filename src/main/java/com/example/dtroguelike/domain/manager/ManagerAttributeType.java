package com.example.dtroguelike.domain.manager;

import com.example.dtroguelike.domain.event.EffectType;

import java.util.function.ToIntFunction;

/**
 * Catalogo de los atributos numericos del Manager (ver {@link ManagerAttributes}),
 * junto con el {@link EffectType} que los representa en el sistema de
 * eventos/efectos y un nombre de despliegue para la UI.
 *
 * Centraliza la relacion "atributo de Manager" &lt;-&gt; "EffectType" para que
 * el resto del codigo (por ejemplo, el generador del evento de
 * pretemporada) no tenga que duplicar esa asociacion.
 */
public enum ManagerAttributeType {

    TACTICS(EffectType.MANAGER_TACTICS, "Táctica", ManagerAttributes::getTactics),
    LEADERSHIP(EffectType.MANAGER_LEADERSHIP, "Liderazgo", ManagerAttributes::getLeadership),
    MANAGEMENT(EffectType.MANAGER_MANAGEMENT, "Gestión", ManagerAttributes::getManagement),
    NEGOTIATION(EffectType.MANAGER_NEGOTIATION, "Negociación", ManagerAttributes::getNegotiation),
    YOUTH_DEVELOPMENT(EffectType.MANAGER_YOUTH_DEVELOPMENT, "Desarrollo juvenil", ManagerAttributes::getYouthDevelopment),
    MOTIVATION(EffectType.MANAGER_MOTIVATION, "Motivación", ManagerAttributes::getMotivation);

    private final EffectType effectType;
    private final String displayName;
    private final ToIntFunction<ManagerAttributes> reader;

    ManagerAttributeType(EffectType effectType, String displayName, ToIntFunction<ManagerAttributes> reader) {
        this.effectType = effectType;
        this.displayName = displayName;
        this.reader = reader;
    }

    public EffectType getEffectType() {
        return effectType;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Lee el valor actual de este atributo desde el {@link ManagerAttributes} dado. */
    public int readValue(ManagerAttributes attributes) {
        return reader.applyAsInt(attributes);
    }

    /** Encuentra el {@link ManagerAttributeType} asociado a un {@link EffectType}, si existe. */
    public static ManagerAttributeType fromEffectType(EffectType effectType) {
        for (ManagerAttributeType attribute : values()) {
            if (attribute.effectType == effectType) {
                return attribute;
            }
        }
        throw new IllegalArgumentException("El EffectType " + effectType + " no corresponde a un atributo de Manager.");
    }
}
