package com.thelion.lytics.interfaces;

import com.thelion.lytics.events.Event;
import com.thelion.lytics.helpers.ParametersHolder;
import com.thelion.lytics.typedefenums.EventTypes;

public interface CreateEventStrategy {

    Event createEvent(ParametersHolder parameters);
}
