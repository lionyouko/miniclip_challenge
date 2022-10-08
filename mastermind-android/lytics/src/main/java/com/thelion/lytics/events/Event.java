package com.thelion.lytics.events;

import com.thelion.lytics.helpers.ParametersHolder;

public abstract class Event {
    private String NAME;
    private ParametersHolder parametersHolder;

    public Event(String name, ParametersHolder parametersHolder){
        this.NAME = name;
        this.parametersHolder = parametersHolder;
    }

}
