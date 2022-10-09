package com.thelion.lytics.concretefactories;

import com.thelion.lytics.concretestrategies.MatchEventStrategy;
import com.thelion.lytics.events.Event;
import com.thelion.lytics.helpers.ParametersHolder;
import com.thelion.lytics.interfaces.AbstractEventFactory;
import com.thelion.lytics.interfaces.CreateEventStrategy;
import com.thelion.lytics.typedefenums.EventTypes;

public class MatchEventFactory extends AbstractEventFactory {

    private static MatchEventFactory INSTANCE;
    private CreateEventStrategy strategy;
    private MatchEventFactory() {
        this.strategy = new MatchEventStrategy();
    }

    public static AbstractEventFactory getInstance() {
        if (INSTANCE == null)
            INSTANCE = new MatchEventFactory();
        return INSTANCE;
    }

    @Override
    public Event createEvent(@EventTypes.EventTypeGiven String eventType, ParametersHolder parameters) {
        return null;
    }

}
