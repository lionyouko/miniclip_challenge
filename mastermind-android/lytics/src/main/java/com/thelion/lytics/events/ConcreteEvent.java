package com.thelion.lytics.events;

import com.thelion.lytics.helpers.Parameter;
import com.thelion.lytics.helpers.ParametersHolder;

/**
 * Very simple concrete event that inherits from event abstract class.
 * It is just for demonstration purposes. It would be unnecessarily to create an concrete event of
 * each kind if they all are name and parameters, even though they have different parameters.
 * This lets it open to have parameters as pleased.
 */
public class ConcreteEvent extends Event {

    /**
     * The public constructor for a concrete event
     * @param name the name (but it is actually the type) of the event
     * @param parameters - the parameters of the events
     */
    public ConcreteEvent(String name, ParametersHolder parameters) {
        super(name, parameters);
    }

    /**
     * The public constructor for a concrete event
     * @param name the name (but it is actually the type) of the event
     */
    public ConcreteEvent(String name) {
        super(name);
    }

    /**
     * The public constructor for a concrete event
     */
    public ConcreteEvent() {
        super();
    }

    /**
     * Each event class can return a string in json format of its state.
     * @return string containing name and parameter of the event in json format
     */
    @Override
    public String asJSONString() {
        StringBuilder jsonString = new StringBuilder();
        jsonString.append("'"+ this.getName() + "'");
        jsonString.append(":");
        jsonString.append("{");
        for (Parameter p : this.getParameters()) {
            jsonString.append("'"+ p.getKey() + "'");
            jsonString.append(":");

            if (p.getValue() instanceof String) {
                jsonString.append("'"+ p.getValue() + "'");
            } else {
                jsonString.append(p.getValue());
            }

            jsonString.append(",");
        }
        jsonString.deleteCharAt(jsonString.length() - 1);
        jsonString.append("}");
        return jsonString.toString();
    }


}
