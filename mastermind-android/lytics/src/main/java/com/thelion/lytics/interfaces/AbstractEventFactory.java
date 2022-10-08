package com.thelion.lytics.interfaces;

import com.thelion.lytics.events.Event;

public interface AbstractEventFactory {

    void setCreateEventStrategy(CreateEventStrategy createEventStrategy);
    Event createEvent();
}
