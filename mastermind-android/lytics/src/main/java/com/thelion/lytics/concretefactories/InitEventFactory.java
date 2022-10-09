package com.thelion.lytics.concretefactories;

import com.thelion.lytics.concretestrategies.InitEventCreateStrategy;
import com.thelion.lytics.events.Event;
import com.thelion.lytics.helpers.ParametersHolder;
import com.thelion.lytics.interfaces.AbstractEventFactory;
import com.thelion.lytics.interfaces.CreateEventStrategy;
import com.thelion.lytics.typedefenums.EventTypes;

public class InitEventFactory extends AbstractEventFactory {

    private static InitEventFactory INSTANCE;
    private CreateEventStrategy strategy;

    private InitEventFactory() {
        this.strategy = new InitEventCreateStrategy();
    }

    public static AbstractEventFactory getInstance() {
        if (INSTANCE == null)
            INSTANCE = new InitEventFactory();
        return INSTANCE;
    }

    @Override
    public Event createEvent(@EventTypes.EventTypeGiven String eventType, ParametersHolder parameters) {
        return strategy.createEvent(parameters);
    }
}
