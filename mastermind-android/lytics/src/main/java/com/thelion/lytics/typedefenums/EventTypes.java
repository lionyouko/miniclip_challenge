package com.thelion.lytics.typedefenums;

import androidx.annotation.StringDef;

public class EventTypes {
    public static final String INIT = "init";
    public static final String SESSION = "session";
    public static final String MATCH = "match";

    @StringDef({INIT,SESSION, MATCH})
    public @interface EventTypeGiven{}

    private final String eventType;

    public EventTypes(@EventTypeGiven String eventType){
        this.eventType = eventType;
    }

    public String getEventType(){
        return this.eventType;
    }
}
