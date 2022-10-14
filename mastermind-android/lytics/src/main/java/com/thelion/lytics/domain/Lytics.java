package com.thelion.lytics.domain;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.thelion.lytics.abstractclasses.EventHandler;
import com.thelion.lytics.abstractclasses.EventStorer;
import com.thelion.lytics.events.Event;
import com.thelion.lytics.handlers.EventConcreteHandler;
import com.thelion.lytics.helpers.GameInfo;
import com.thelion.lytics.interfaces.LyticEventListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Lytics class is a class to receive all events generated from a event generator.
 * Each lytics class is an event listener and is subscribed to event generators.
 * It needs the game info that will contains basic data like user id and time-frequency
 * It goes on pair with event generator: event generator produces events of various kind
 * and lytics will consume them and save them on disk, por example, depending on how handler is programmed.
 *
 * This class needs Build.VERSION.SDK_INT >= Build.VERSION_CODES.O.
 *
 * This class provides a default event handler, but more can be added.
 */
public class Lytics implements LyticEventListener, DefaultLifecycleObserver {
    private GameInfo gameInfo;
    private EventHandler eventHandler;
    private EventStorer eventStorer;
    private Context appContext;

    /**
     * The public construct for Lytics
     * @param gameInfo - information about the game, like user id, time-frequency, or maybe more
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Lytics(GameInfo gameInfo, Context c){
        this.gameInfo = gameInfo;
        this.appContext = c;

        // defaults to EventConcreteHandler with default event storage
        this.eventHandler = new EventConcreteHandler(gameInfo, this.appContext);
        this.eventHandler.setTimeFrequency(gameInfo.getTimeFrequency());

    }

    /**
     * getter for game info
     * @return current gameinfo this class holds
     */
    public GameInfo getGameInfo() {
        return gameInfo;
    }

    /**
     * setter for game info
     * @param gameInfo - change the current gameinfo t this class holds (it may end not being used)
     */
    public void setGameInfo(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }

    /**
     * (not used)
     * One may feel implementing more event handlers in future, so this class accepts more handlers.
     * if the event handler provided has no storer, it will defaults to add one that is the current storer of the lytics instance
     * so a person using the library may want to keep track of that
     *
     * @param eventHandler a handler that implements EventHandler interface
     */
    private void setEventHandler(EventHandler eventHandler) {
        if (eventHandler.getEventStorer() == null) {
            eventHandler.setEventStorer(this.eventStorer);
        }
        this.eventHandler = eventHandler;
    }

    /**
     * (not used)
     * change event storer to a new one (the handler will also get the new event storer)
     * @param eventStorer - a storer capable to save events where developer client desires
     */
    private void setEventStorage(EventStorer eventStorer) {
        this.eventStorer = eventStorer;
        eventHandler.setEventStorer(this.eventStorer);

    }

    /**
     * Change time frequency requirements regarding saving events for all handlers
     *
     * @param timeFrequency
     */
    public void changeTimeFrequency(int timeFrequency){
        this.gameInfo.setTimeFrequency(timeFrequency);
        // in other to use the foreach, the sdk must be at minimum N
        this.eventHandler.setTimeFrequency(timeFrequency);

    }

    /**
     * The LyticEventListener function. When onEvent is called, and the event is not null, handler receives the event and gameInfo.
     * @param event a concrete event that implements Event abstract class
     */
    @Override
    public void onEvent(Event event){
        getStoredEventFiles();
        if (event == null)
                return;
        this.eventHandler.onEvent(event);
    }

    /**
     * One can get the stored event files stored by lytics app
     * I left this function like this, without arguments, but the general way would be to have an user_id
     * as argument and to search and return only files that starts with user id, just like in the body below
     * @return list of files with events
     */
    public List<File> getStoredEventFiles(){
        File[] files = this.eventHandler.getEventStorer().getStoredEventFiles();
        List<File> result = new ArrayList<>();
        int cnt = 0;
        for (File f: files) {
            if (f.getName().startsWith(this.gameInfo.getCurrentUserId())) {
                Log.d("Lytics", "content: " + readFileContent(f.toString()));
                result.add(f);
            }
        }
        return result;
    }

    /**
     * Helper function to show content of a saved event file
     * @return the content of the file
     */
    private String readFileContent(String path) {
        byte[] encoded = new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                encoded = Files.readAllBytes(Paths.get(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new String(encoded);
    }

    /*
    ------------------------- lifecycle functions to observe a parent lifecycle  -------------------------
    */

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        Log.d("Lytics", "onResume");
        //Just as example
//        if (((EventConcreteHandler)this.eventHandler).isCacheTimerStopped()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                ((EventConcreteHandler)this.eventHandler).restartStorageClock();
//            }
//        }
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onPause(owner);
        Log.d("Lytics", "onPause");

    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onCreate(owner);
        Log.d("Lytics", "onCreate");
        //Just as example

    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        Log.d("Lytics", "onStart");

    }

    /**
     * When app goes to the background, storage is called
     * @param owner lifecycle owner is the current activity or fragment lytics instance is running with
     */
    @Override
    public void onStop(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStop(owner);
        Log.d("Lytics", "onStop");
        this.eventHandler.getEventStorer().storeEvents();
        // ((EventConcreteHandler)this.eventHandler).stopStorageClock();
    }

    /**
     * When app is closed, storage is called
     * I am aware that a rotation in the screen trigger this function (I will comment about it in reasoning section)
     * @param owner lifecycle owner is the current activity or fragment lytics instance is running with
     */
    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onDestroy(owner);
        Log.d("Lytics", "onDestroy");
        this.eventHandler.getEventStorer().storeEvents();
        ((EventConcreteHandler)this.eventHandler).stopStorageClock();

    }
}
