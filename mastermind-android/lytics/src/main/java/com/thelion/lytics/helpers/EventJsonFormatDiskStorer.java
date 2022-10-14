package com.thelion.lytics.helpers;

import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.thelion.lytics.abstractclasses.EventStorer;
import com.thelion.lytics.events.Event;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Saves, in cache, the events in a file in format json. One can recover the files using this class.
 */
public class EventJsonFormatDiskStorer extends EventStorer {

    private long DEFAULT_FUTURE_MAX_TIME = Long.MAX_VALUE; // max value to have a very long future before to stop
    private final int DEFAULT_TIME_FREQUENCY = 10000; // saves the file each DEFAULT_TIME_FREQUENCY milliseconds
    private int timeFrequency = DEFAULT_TIME_FREQUENCY;
    private List<Event> eventsToStore;
    private GameInfo gameInfo;
    private CountDownTimer cacheTimer;
    private boolean cacheTimerStopped = true;
    private String cachePath;
    private ExecutorService writeToCacheThread;
    private Context appContext;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public EventJsonFormatDiskStorer(GameInfo gameInfo, Context appContext) {
        super();
        this.appContext = appContext;
        this.gameInfo = gameInfo;
        // complete path to where files will be stored
        this.cachePath = appContext.getFilesDir().getAbsolutePath();


        // Parallel execution for writing files in disk
        writeToCacheThread = Executors.newSingleThreadExecutor();

        eventsToStore = new ArrayList<Event>();

        setFrequencyClock();
        //startFrequencyClock();

        //FileHelper.clearFiles(this.cachePath);

    }

    /**
     * The frequency that events must be saved in cache whenever app is running non-stop
     * @param timeFrequency the frequency in seconds that events must be saved in cache (disk)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setTimeFrequency(int timeFrequency) {
        this.timeFrequency = timeFrequency;

        stopFrequencyClock();
        setFrequencyClock();
        //startFrequencyClock();
    }

    /**
     * Single function to accept one or more events to the queue that will be saved in a file
     * @param events events of any custom type cumplying abstract Event class.
     */
    @Override
    public void addEventsToStorageQueue(Event... events) {
        eventsToStore.addAll(Arrays.asList(events));
    }

    /**
     * The difference in time between the time of the file created in disk and the time that event was created
     *
     * @return the value of that difference
     */
    private long computeEventAge(long fileCreationTime, long eventTimeStamp) {
        return (fileCreationTime - eventTimeStamp); // forced cast
    }


    /**
     * Just a helper functions to see if the events are indeed being saved. If one saves, one will want to check
     * it later on.
     * @return list of files with events in them.
     */
    @Override
    public File[] getStoredEventFiles() {
        return FileHelper.loadFromCache(this.cachePath);
    }


    /**
     * Helper function to write all queued events in a file.
     * This function uses a parallel thread an write all events as json strings in a json formatted file in disk.
     * If no events exist in the queue, this function does nothing
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized void writeOnCache(){

        // creating te filename of the cached file plus adding the event age after setting up the name
        // i am aware that the creation here isnt the same as creation right below.
        // I will believe that operations of creating the filename and writing to the file will not differ extensively,
        // so it can be considered more or less the same time.

        String fileName = FileHelper.createStandardFileName(this.gameInfo.getCurrentUserId());
        addComputedEventAge(fileName);

        //formatting the string to write on disk file
        StringBuilder jsonFileContent = new StringBuilder();
        jsonFileContent.append("{");
        for (Event e : eventsToStore) {
            jsonFileContent.append(e.asJSONString());
            jsonFileContent.append(",");
        }
        jsonFileContent.deleteCharAt(jsonFileContent.length() - 1);
        jsonFileContent.append("}");

        //executing a parallel thread to write the jsonfilecontent
        writeToCacheThread.execute(() -> {


                if (this.eventsToStore.isEmpty()) {
                    Log.d("EventFormatDiskStorer", "empty queue called by countdown timer");
                    return;
                }
                FileOutputStream outputStream = null;
                try {
                    outputStream = appContext.openFileOutput(fileName, Context.MODE_PRIVATE);
                    outputStream.write(jsonFileContent.toString().getBytes());


                    Log.d("EventFormatDiskStorer", "saved " + this.eventsToStore.size() + " events");
                    // getStoredEventFiles();
                    cleanEventQueues();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if(outputStream != null) {
                            outputStream.flush();
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        });

    }

    /**
     * Helper function to put event age in each of the events queued before storing them
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addComputedEventAge(String fileName){
        for (Event e : eventsToStore) {
            e.addParameter(new Parameter("event_age", computeEventAge(FileHelper.recoverTStampFromStandardFileName(fileName), e.getTimeStamp())));
        }
    }

    /**
     * Helper function to clean event queue if the events where saved in disk
     */
    private void cleanEventQueues(){
        this.eventsToStore.clear();
    }

    /**
     * Set the internal clock to save the events on disk storage (it uses the internal time frequency)
     * It will need to be called again if the time frequency is changed.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setFrequencyClock() {
        // It will only stop in the max horizon of time
        this.cacheTimer = new CountDownTimer(Instant.ofEpochMilli(DEFAULT_FUTURE_MAX_TIME).toEpochMilli(), this.timeFrequency) {
            @Override
            public void onTick(long l) {
                writeOnCache();
            }

            @Override
            public void onFinish() {

            }
        };

        startFrequencyClock();
    }

    /**
     * Helper function to stop the automatically save event feature from the concrete disk storer
     */
    public void stopFrequencyClock(){
        if (cacheTimer != null) {
            cacheTimer.cancel();
            this.cacheTimerStopped = true;
        }
    }

    /**
     * Helper function to start the automatically save event feature from the concrete disk storer
     */
    private void startFrequencyClock(){
        if (cacheTimer != null) {
            cacheTimer.start();
            this.cacheTimerStopped = false;
        }
    }


    public boolean isCacheTimerStopped() {
        return this.cacheTimerStopped;
    }



    /**
     * Store an event in disk as json formatted string
     * @param event Custom event from exterior
     *
     */
    @Override
    public void storeEvent(Event event) {
        // not used
    }


    /**
     * Store events forcefully instead of waiting the internal clock
     * Event queue may be empty, so no file will be written in disk as no event exist to be saved
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void storeEvents() {
        // Storer may be forced to store events if something makes it to
        writeOnCache();
    }

    @Override
    public void storeEvents(List<Event> events) {
        // not used
    }




}
