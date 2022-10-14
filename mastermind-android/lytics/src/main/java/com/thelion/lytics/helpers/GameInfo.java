package com.thelion.lytics.helpers;

public class GameInfo {
    private final int TO_MILLIS = 1000;
    private String currentUserId;
    private int timeFrequency;


    public GameInfo(String currentUserId, int timeFrequency) {
        this.currentUserId = currentUserId;
        this.timeFrequency = timeFrequency * TO_MILLIS;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public int getTimeFrequency() {
        return timeFrequency;
    }

    public void setTimeFrequency(int timeFrequency) {
        this.timeFrequency = timeFrequency;
    }
}
