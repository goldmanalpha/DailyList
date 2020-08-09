package com.goldmanalpha.dailydo;

import android.util.Log;

import java.util.Arrays;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Log.e("Uncaught Exception",
                throwable.getMessage() + " " + thread.getClass().getName() +
                "  Error in " + Arrays.toString(throwable.getCause().getStackTrace()));
        if (uncaughtExceptionHandler != null) {
            // let Android know what happened
            uncaughtExceptionHandler.uncaughtException(thread, throwable);
        } else {
            // kill process
            System.exit(-1);
        }
    }
}