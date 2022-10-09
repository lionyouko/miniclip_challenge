package com.thelion.lytics.interfaces;

import com.thelion.lytics.events.Event;
import com.thelion.lytics.helpers.GameInfo;

public interface EventHandler {

    void onEvent(Event event, GameInfo gameInfo);
}
