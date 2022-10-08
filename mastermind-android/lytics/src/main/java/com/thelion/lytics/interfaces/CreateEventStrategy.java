package com.thelion.lytics.interfaces;

import com.thelion.lytics.events.Event;
import com.thelion.lytics.helpers.ParametersHolder;

public interface CreateEventStrategy {

    Event createEvent(ParametersHolder parametersHolder);
}
