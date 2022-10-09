package com.thelion.lytics.events;

import com.google.gson.Gson;
import com.thelion.lytics.helpers.ParametersHolder;
import com.thelion.lytics.typedefenums.EventTypes;
import org.json.JSONObject;

public class ConcreteEvent extends Event {

    public ConcreteEvent(@EventTypes.EventTypeGiven String name, ParametersHolder parametersHolder) {
        super(name, parametersHolder);
    }

    @Override
    public String asJSONString() {
        return null;
    }


}
