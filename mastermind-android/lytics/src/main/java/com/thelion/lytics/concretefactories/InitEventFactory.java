package com.thelion.lytics.concretefactories;

import com.thelion.lytics.events.Event;
import com.thelion.lytics.interfaces.AbstractEventFactory;
import com.thelion.lytics.interfaces.CreateEventStrategy;

public class InitEventFactory implements AbstractEventFactory {

    @Override
    public void setCreateEventStrategy(CreateEventStrategy createEventStrategy) {

    }

    @Override
    public Event createEvent() {
        return null;
    }
}
