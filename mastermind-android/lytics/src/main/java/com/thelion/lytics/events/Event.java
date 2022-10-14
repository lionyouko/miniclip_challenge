package com.thelion.lytics.events;

import android.os.Build;
import android.util.Log;

import com.thelion.lytics.helpers.Parameter;
import com.thelion.lytics.helpers.ParametersHolder;

import java.time.Instant;
import java.util.List;


/**
 * Abstract class from where other events may be created
 * A client may want to create new type of event, it just needs to add those types in Event Type Creator.
 *
 */
public abstract class Event {
    private String NAME;
    private ParametersHolder parametersHolder;
    private long creationTimeStamp;


    /**
     * Constructor if you want to provide your own parameter holder
     * @param name
     * @param parametersHolder
     */
    public Event(String name, ParametersHolder parametersHolder){
        this.NAME = name;
        this.parametersHolder = parametersHolder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.creationTimeStamp = Instant.now().getEpochSecond();

        }

    }

    /**
     * Simpler constructor with a name for the event
     * @param name the name - type of the custom event
     */
    public Event(String name){
        this.NAME = name;
        this.parametersHolder = new ParametersHolder();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.creationTimeStamp = Instant.now().getEpochSecond();

        }
    }

    /**
     * Simpler construtor for a desired event builder
     */
    public Event(){
        this.parametersHolder = new ParametersHolder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.creationTimeStamp = Instant.now().getEpochSecond();

        }
    }

    public void setName(String name) {
        this.NAME = name;
    }
    public String getName() {
        return this.NAME;
    }

    public List<Parameter> getParameters()  {
        return this.parametersHolder.getParameters();
    }


    public void addParameter(Parameter p){
        this.parametersHolder.addParameters(p);
    }

    public long getTimeStamp(){
        return this.creationTimeStamp;
    }

    public abstract String asJSONString();


}
