package com.thelion.lytics.domain;

import com.thelion.lytics.concretefactories.InitEventFactory;
import com.thelion.lytics.concretefactories.MatchEventFactory;
import com.thelion.lytics.concretefactories.SessionEventFactory;
import com.thelion.lytics.events.Event;
import com.thelion.lytics.helpers.ParametersHolder;
import com.thelion.lytics.interfaces.AbstractEventFactory;
import com.thelion.lytics.interfaces.EventListener;
import com.thelion.lytics.typedefenums.EventTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class EventGenerator {

    private HashMap<String, AbstractEventFactory> eventFactories;
    private List<EventListener> eventGeneratorObservers;

    public EventGenerator() {
        eventFactories = new HashMap<>();
        eventGeneratorObservers = new ArrayList<>();
    }

    public void addEventFactory(@EventTypes.EventTypeGiven String eventType){
        switch (eventType) {
            case EventTypes.INIT:
                this.eventFactories.put(eventType, InitEventFactory.getInstance());
                break;
            case EventTypes.MATCH:
                this.eventFactories.put(eventType, MatchEventFactory.getInstance());
                break;
            case EventTypes.SESSION:
                this.eventFactories.put(eventType, SessionEventFactory.getInstance());
                break;
            default:
                break;
        }
    }

    public void emitEvent(@EventTypes.EventTypeGiven String eventType, ParametersHolder parameters) {
        Event e = Objects.requireNonNull(this.eventFactories.get(eventType)).createEvent(eventType,parameters);
        notifyListeners(e);

    }

    public void addObserver(EventListener eventListener){
        this.eventGeneratorObservers.add(eventListener);
    }

    private void notifyListeners(Event e){
        for (EventListener el : eventGeneratorObservers) {
            el.onEvent(e);
        }
    }
}
