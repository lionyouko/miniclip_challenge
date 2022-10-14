package com.thelion.lytics.interfaces;

import com.thelion.lytics.events.Event;

/**
 * A listener for events the user may need to create using the library
 * The class implementing this listener shouldat least also use a handler to deal with it.
 * But client is free to choose.
 * The recommended way is have a instance of Lytics to handle the events for you.
 */
public interface LyticEventListener {

    /**
     * Reacts to new events that comes from another sources.
     * @param event an event to be processed
     */
    void onEvent(Event event);
}
