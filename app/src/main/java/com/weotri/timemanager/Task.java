package com.weotri.timemanager;

import android.util.Log;

/**
 * Created by SepLite on 7/19/16.
 */

public class Task {
    public static final int NOT_STARTED = 0;
    public static final int ACTIVE = 1;
    public static final int PAUSED = 2;
    public static final int ENDED = 3;

    public int estimate;
    private int elapsed = 0;
    private long id;
    public String name;
    private long resumeTime;
    private long startTime;
    private int status;
    private int timeUntilPaused = 0;

    public Task(String name, int estimate, int status) {
        this.name = name;
        this.estimate = estimate;
        this.status = status;
        id = 0;
    }

    public Task(String name, int estimate){
        this.name = name;
        this.estimate = estimate;
        status = NOT_STARTED;
        id = 0;
    }

    public long getID() {
        return id;
    }

    public String getTime(){
        if(status == ACTIVE) {
            Log.d("debug", resumeTime + " " + System.currentTimeMillis() + " " + timeUntilPaused);
            long millisecondsPassed = System.currentTimeMillis() - resumeTime;
            elapsed = ((int) millisecondsPassed) / 1000 + timeUntilPaused;
        }
        return elapsed / 60 + ":" + elapsed % 60;
    }

    public int getStatus(){
        return status;
    }

    public void pause(){
        status = PAUSED;
        timeUntilPaused = elapsed;
    }

    public void resume(){
        status = ACTIVE;
        resumeTime = System.currentTimeMillis();
    }

    public void start(){
        status = ACTIVE;
        resumeTime = startTime = System.currentTimeMillis();
    }

    public void end(){
        status = ENDED;
    }
}
