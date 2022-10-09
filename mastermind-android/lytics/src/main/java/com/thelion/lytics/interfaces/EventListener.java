package com.thelion.lytics.interfaces;

import com.thelion.lytics.events.Event;

public interface EventListener {

    void onEvent(Event event);
}
