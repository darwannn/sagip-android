package com.example.sagip;

import java.util.Timer;

public class TimerManager {
    private static Timer intervalTimer;

    public static Timer getIntervalTimer() {
        if (intervalTimer == null) {
            intervalTimer = new Timer();
        }
        return intervalTimer;
    }

    public static void cancelIntervalTimer() {
        if (intervalTimer != null) {
            intervalTimer.cancel();
            intervalTimer = null;
        }
    }
}
