package com.thelion.lytics.concretefactories;

import com.thelion.lytics.concretestrategies.SessionEventCreateStrategy;
import com.thelion.lytics.events.Event;
import com.thelion.lytics.helpers.ParametersHolder;
import com.thelion.lytics.interfaces.AbstractEventFactory;
import com.thelion.lytics.interfaces.CreateEventStrategy;
import com.thelion.lytics.typedefenums.EventTypes;

public class SessionEventFactory extends AbstractEventFactory {

    private static SessionEventFactory INSTANCE;
    private CreateEventStrategy strategy;

    public SessionEventFactory() {
        this.strategy = new SessionEventCreateStrategy();
    }

    public static AbstractEventFactory getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SessionEventFactory();
        return INSTANCE;
    }

    @Override
    public Event createEvent(@EventTypes.EventTypeGiven String eventType, ParametersHolder parameters) {
        return null;
    }
}
