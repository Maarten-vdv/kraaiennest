package com.kraaiennest.opvang.activities.main;

import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

    private String activity;

    public ExceptionHandler(String activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(@NonNull @NotNull Thread t, @NonNull @NotNull Throwable e) {
        Log.e(activity, "uncaughtException: "+ e.getMessage());
        Log.e(activity, "uncaughtException: ", e);
    }
}
