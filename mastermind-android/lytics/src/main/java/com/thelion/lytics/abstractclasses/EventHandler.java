package com.thelion.lytics.abstractclasses;

import com.thelion.lytics.events.Event;

/**
 *  Abstract class from where client developer can produce its own handlers that work with lytics
 */
public abstract class EventHandler {

    private EventStorer eventStorer;

    public EventHandler(EventStorer eventStorer){
        this.eventStorer = eventStorer;
    }

    public EventHandler () {
    }

    public abstract void onEvent(Event event);

    public void setEventStorer(EventStorer es){
        this.eventStorer = es;
    }

    public EventStorer getEventStorer(){
        return this.eventStorer;
    }

    public void setTimeFrequency(int timeFrequency){
        if (this.eventStorer != null){
            this.eventStorer.setTimeFrequency(timeFrequency);
        }
    }

}
