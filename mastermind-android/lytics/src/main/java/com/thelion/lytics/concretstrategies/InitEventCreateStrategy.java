package com.thelion.lytics.concretstrategies;

import com.thelion.lytics.events.ConcreteEvent;
import com.thelion.lytics.events.Event;
import com.thelion.lytics.helpers.ParametersHolder;
import com.thelion.lytics.interfaces.CreateEventStrategy;

public class InitEventCreateStrategy implements CreateEventStrategy {
    @Override
    public Event createEvent(ParametersHolder parametersHolder) {
        return new ConcreteEvent("INIT", parametersHolder);
    }
}
