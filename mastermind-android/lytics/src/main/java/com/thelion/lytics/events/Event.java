package com.thelion.lytics.events;

import com.thelion.lytics.helpers.EventJsonFormatDiskStorer;
import com.thelion.lytics.helpers.Parameter;
import com.thelion.lytics.helpers.ParametersHolder;
import com.thelion.lytics.interfaces.EventStorer;
import com.thelion.lytics.typedefenums.EventTypes;

import org.json.JSONObject;

import java.util.List;

public abstract class Event {
    private @EventTypes.EventTypeGiven String NAME;
    private ParametersHolder parametersHolder;
    private EventStorer eventStorer;

    public Event(@EventTypes.EventTypeGiven String name, ParametersHolder parametersHolder){
        this.NAME = name;
        this.parametersHolder = parametersHolder;

        //defaults to write on disk as json
        this.eventStorer = new EventJsonFormatDiskStorer();
    }

    public @EventTypes.EventTypeGiven String getName() {
        return this.NAME;
    }

    public List<Parameter> getParameters()  {
        return this.parametersHolder.getParameters();
    }

    public abstract String asJSONString();

    public void recordEvent(){
        this.eventStorer.storeEvent(this);
    }

}
