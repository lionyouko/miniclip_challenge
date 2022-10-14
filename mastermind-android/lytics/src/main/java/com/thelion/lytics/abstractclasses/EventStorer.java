package com.thelion.lytics.abstractclasses;

import com.thelion.lytics.events.Event;

import java.io.File;
import java.util.List;

/**
 *  Abstract class that a client developer can produce its own event storers that work with lytics
 *  This class has lots of similar functions, I am letting all of them n the sense that
 *  the library would provide different ways to deal with events coming from exterior.
 *  A client maybe would like to have a list of events utside the storer, for example,
 *  while other may want to have them in storer and them to save them.
 */
public abstract class EventStorer {

    private static final String DISK_CACHE_PATH = "/lytics_cache/";

    public EventStorer(){}

    /**
     * Adds one or more events to storage queue if a dev client opt to use storer
     * as the class holding a list of elements to save
     * @param events
     */
    public abstract void addEventsToStorageQueue(Event... events);

    /**
     * Store an unique event
     * @param e event to store
     */
    public abstract void storeEvent(Event e);

    /**
     * Store events if the client wants to save them in storer before putting somewhere else
     */
    public abstract void storeEvents();


    /**
     * Store events if they come in a list outside the storage
     * @param events the events to be stored, cached, wrote in disk
     */
    public abstract void storeEvents(List<Event> events);


    /**
     * Storer needs to know the frequency to save the files
     * @param timeFrequency the frequency in seconds that events must be saved in cache (disk)
     */
    public abstract void setTimeFrequency(int timeFrequency);

    /**
     * Utility function to show that in fact files are being stored by getting them
     * @return list of files that contain events in json format
     */
    public abstract File[] getStoredEventFiles();

    /**
     * The partial disk cache is the last portion of the disk cache where files will be saved
     *
     * @return the string of the last part of cache path
     */
    public String getPartialDiskCachePath (){
        return this.DISK_CACHE_PATH;
    };


}
