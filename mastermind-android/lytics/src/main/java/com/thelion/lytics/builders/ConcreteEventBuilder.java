package com.thelion.lytics.builders;

import com.thelion.lytics.events.ConcreteEvent;
import com.thelion.lytics.helpers.Parameter;

public class ConcreteEventBuilder {
    private ConcreteEvent event;

    private ConcreteEventBuilder() {
        event = new ConcreteEvent();
    }

    public static ConcreteEventBuilder builder() {
        return new ConcreteEventBuilder();
    }

    public ConcreteEventBuilder withName(String name) {
        event.setName(name);
        return this;
    }

    public <T> ConcreteEventBuilder withParameter(String key, T value) {
        event.addParameter(new Parameter<>(key, value));
        return this;
    }

    public ConcreteEvent build() {
        return this.event;
    }

}
