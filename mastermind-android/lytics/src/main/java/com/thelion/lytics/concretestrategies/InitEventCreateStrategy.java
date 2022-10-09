package com.thelion.lytics.concretestrategies;

import com.thelion.lytics.events.ConcreteEvent;
import com.thelion.lytics.events.Event;
import com.thelion.lytics.helpers.ParametersHolder;
import com.thelion.lytics.interfaces.CreateEventStrategy;
import com.thelion.lytics.typedefenums.EventTypes;

public class InitEventCreateStrategy implements CreateEventStrategy {


    @Override
    public Event createEvent(ParametersHolder parameters) {
        return new ConcreteEvent(EventTypes.INIT, parameters);
    }
}
