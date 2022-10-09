package com.thelion.lytics.interfaces;

import com.thelion.lytics.events.Event;
import com.thelion.lytics.helpers.ParametersHolder;
import com.thelion.lytics.typedefenums.EventTypes;

public abstract class AbstractEventFactory {

    public abstract Event createEvent(@EventTypes.EventTypeGiven String eventType, ParametersHolder parameters);
}
