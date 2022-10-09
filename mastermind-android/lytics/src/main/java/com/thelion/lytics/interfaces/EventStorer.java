package com.thelion.lytics.interfaces;

import com.thelion.lytics.events.Event;

public abstract class EventStorer {

    private static final String DISK_CACHE_PATH = "/lytics_cached_ads/";

    public abstract boolean storeEvent(Event e);

}
