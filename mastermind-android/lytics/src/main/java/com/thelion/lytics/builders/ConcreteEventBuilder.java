package com.thelion.lytics.builders;

import com.thelion.lytics.events.ConcreteEvent;
import com.thelion.lytics.helpers.Parameter;

/**
 * An event builder for concrete events in which any parameters plus a name can be put
 */
public class ConcreteEventBuilder {
    private ConcreteEvent event;

    private ConcreteEventBuilder() {
        event = new ConcreteEvent();
    }

    /**
     *  a builder of events
     * @return the builder of events
     */
    public static ConcreteEventBuilder builder() {
        return new ConcreteEventBuilder();
    }

    /**
     * sets a name for event
     * @param name name of the event (like init, session, match)
     * @return the builder of event
     */
    public ConcreteEventBuilder withName(String name) {
        event.setName(name);
        return this;
    }

    /**
     * Adds a new parameter to the concrete event
     * @param key a key (string) for the parameter key
     * @param value a value if any type for the content of the parameter
     * @param <T> Any type for the value of the parameter
     * @return the builder with a event added with new parameter
     */
    public <T> ConcreteEventBuilder withParameter(String key, T value) {
        event.addParameter(new Parameter<>(key, value));
        return this;
    }

    public ConcreteEvent build() {
        return this.event;
    }

}
