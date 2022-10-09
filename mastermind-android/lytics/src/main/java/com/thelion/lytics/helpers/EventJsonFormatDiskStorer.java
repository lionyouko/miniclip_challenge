package com.thelion.lytics.helpers;

import com.thelion.lytics.events.Event;
import com.thelion.lytics.interfaces.EventStorer;

public class EventJsonFormatDiskStorer extends EventStorer {



    @Override
    public boolean storeEvent(Event event) {
        String jsoEventString = event.asJSONString();
        return false;
    }

    private void writeOnCache(String jsonEventAsString){

    }
}
