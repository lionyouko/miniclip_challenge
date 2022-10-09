package com.thelion.lytics.builders;

import com.thelion.lytics.domain.EventGenerator;
import com.thelion.lytics.typedefenums.EventTypes;

public class EventGeneratorBuilder {

    private EventGenerator eventGenerator;

    private EventGeneratorBuilder() {
        this.eventGenerator = new EventGenerator();
    }

    public static EventGeneratorBuilder builder(){
        return new EventGeneratorBuilder();
    }

    public EventGeneratorBuilder withInitFactory(){
        eventGenerator.addEventFactory(EventTypes.INIT);
        return this;
    }

    public EventGeneratorBuilder withSessionFactory(){
        eventGenerator.addEventFactory(EventTypes.SESSION);
        return this;
    }

    public EventGeneratorBuilder withMatchFactory(){
        eventGenerator.addEventFactory(EventTypes.MATCH);
        return this;
    }

    public EventGeneratorBuilder withAnyFactory(@EventTypes.EventTypeGiven String eventType) {
        eventGenerator.addEventFactory(eventType);
        return this;
    }

    public EventGenerator build(){
        
        return this.eventGenerator;
    }

}
