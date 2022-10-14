package com.thelion.lytics.handlers;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.thelion.lytics.events.Event;
import com.thelion.lytics.helpers.EventJsonFormatDiskStorer;
import com.thelion.lytics.helpers.GameInfo;
import com.thelion.lytics.abstractclasses.EventHandler;
import com.thelion.lytics.abstractclasses.EventStorer;

import java.util.ArrayList;
import java.util.List;

/**
 * Default handler/dispatcher provided by the library. Its purpose is to comply to all requirements related
 * to time-frequency for saving events, file format, and others. A client can use it without needing to create their own
 * This class RequiresApi(api = Build.VERSION_CODES.O)
 */
public class EventConcreteHandler extends EventHandler {

    private GameInfo gameInfo;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public EventConcreteHandler(GameInfo gameInfo, Context c){
        super(new EventJsonFormatDiskStorer(gameInfo, c));
        this.gameInfo = gameInfo;
    }
    @Override
    public void onEvent(Event event) {
        this.getEventStorer().addEventsToStorageQueue(event);
    }

    /**
     * Handler may forcefully stop the clock of the storage in order to avoid power consumption.
     */
    public void stopStorageClock() {
        if (this.getEventStorer() instanceof EventJsonFormatDiskStorer)
            ((EventJsonFormatDiskStorer)this.getEventStorer()).stopFrequencyClock();
    }


    /**
     * Handler may forcefully restart the clock of the storage when app comes back.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void restartStorageClock(){
        if (this.getEventStorer() instanceof EventJsonFormatDiskStorer)
            ((EventJsonFormatDiskStorer)this.getEventStorer()).setFrequencyClock();
    }


    /**
     * Check if the internal clock to save events is running or not
     * @return true if cache timer is not running
     */
    public boolean isCacheTimerStopped() {
        return ((EventJsonFormatDiskStorer)this.getEventStorer()).isCacheTimerStopped();
    }


}
