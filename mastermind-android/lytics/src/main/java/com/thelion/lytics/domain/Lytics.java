package com.thelion.lytics.domain;

import com.thelion.lytics.events.Event;
import com.thelion.lytics.handlers.EventConcreteHandler;
import com.thelion.lytics.helpers.GameInfo;
import com.thelion.lytics.interfaces.EventHandler;
import com.thelion.lytics.interfaces.EventListener;

import java.util.ArrayList;
import java.util.List;

public class Lytics implements EventListener {
    private GameInfo gameInfo;
    private List<EventHandler> eventHandlers;

    public Lytics(GameInfo gameInfo){
        this.gameInfo = gameInfo;

        // defaults to an array list with at least one EventConcreteHandler
        this.eventHandlers = new ArrayList<>();
        this.eventHandlers.add(new EventConcreteHandler());
    }

    public GameInfo getGameInfo() {
        return gameInfo;
    }

    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    public void addEventHandler(EventHandler eventHandler) {
        this.eventHandlers.add(eventHandler);
    }

    @Override
    public void onEvent(Event event){
        for (EventHandler ev : eventHandlers) {
            ev.onEvent(event, gameInfo);
        }
    }
}
