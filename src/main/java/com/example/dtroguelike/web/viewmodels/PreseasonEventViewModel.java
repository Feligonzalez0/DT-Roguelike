package com.example.dtroguelike.web.viewmodels;

import com.example.dtroguelike.domain.event.Effect;
import com.example.dtroguelike.domain.event.Event;
import com.example.dtroguelike.domain.event.EventOption;
import com.example.dtroguelike.domain.manager.ManagerAttributeType;

import java.util.List;

/**
 * Representacion plana del evento de pretemporada para la vista
 * Mustache: titulo, descripcion y las opciones de atributo a elegir.
 */
public class PreseasonEventViewModel {

    public final String title;
    public final String description;
    public final List<OptionViewModel> options;

    public PreseasonEventViewModel(Event event) {
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.options = event.getOptions().stream()
                .map(OptionViewModel::new)
                .toList();
    }

    public static class OptionViewModel {
        public final String id;
        public final String description;
        public final String attributeName; 
        public final int delta;

        public OptionViewModel(EventOption option) {
            this.id = option.getId();
            this.description = option.getDescription();

            Effect effect = option.getSuccessOutcome().getEffects().getFirst();
            ManagerAttributeType attribute = ManagerAttributeType.fromEffectType(effect.getType());
            this.attributeName = attribute.getDisplayName();
            
            this.delta = effect.getAmount();
        }
    }
}
