package com.example.sagip;

import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {
    private Timer intervalTimer;

    private static TimerManager instance;

    private TimerManager() {
        // Private constructor to prevent instantiation
    }

    public static TimerManager getInstance() {
        if (instance == null) {
            instance = new TimerManager();
        }
        return instance;
    }

    public void startIntervalTimer(TimerTask task, long delay, long period) {
        if (intervalTimer != null) {
            intervalTimer.cancel();
        }
        intervalTimer = new Timer();
        intervalTimer.scheduleAtFixedRate(task, delay, period);
    }

    public void cancelIntervalTimer() {
        if (intervalTimer != null) {
            intervalTimer.cancel();
            intervalTimer = null;
        }
    }
}
