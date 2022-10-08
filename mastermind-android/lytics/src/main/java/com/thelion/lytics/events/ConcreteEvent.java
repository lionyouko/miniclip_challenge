package com.thelion.lytics.events;

import com.thelion.lytics.helpers.ParametersHolder;

public class ConcreteEvent extends Event {

    public ConcreteEvent(String name, ParametersHolder parametersHolder) {
        super(name, parametersHolder);
    }

}
