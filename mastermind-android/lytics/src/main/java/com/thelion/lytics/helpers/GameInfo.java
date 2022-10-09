package com.thelion.lytics.helpers;

public class GameInfo {
    private int currentUserId;
    private long timeFrequency;

    public GameInfo(int currentUserId, long timeFrequency) {
        this.currentUserId = currentUserId;
        this.timeFrequency = timeFrequency;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    public long getTimeFrequency() {
        return timeFrequency;
    }

    public void setTimeFrequency(long timeFrequency) {
        this.timeFrequency = timeFrequency;
    }
}
