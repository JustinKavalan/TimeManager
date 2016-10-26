package com.weotri.timemanager;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * Created by SepLite on 7/19/16.
 */

public class Task{
    public static final int NOT_STARTED = 0;
    public static final int ACTIVE = 1;
    public static final int PAUSED = 2;
    public static final int ENDED = 3;

    public int estimate;
    private int elapsed;
    private long id;
    public String name;
    private long resumeTime;
    private long startTime;
    private int status;
    private int timeUntilPaused;

    public Task(String name, int estimate, int status) {
        this.name = name;
        this.estimate = estimate;
        this.status = status;
        id = -1;
    }

    public Task(String name, int estimate){
        this.name = name;
        this.estimate = estimate;
        status = NOT_STARTED;
        id = -1;
    }

    public Task(String name, int elapsed, int estimate, long startTime, int status, long id, int timeUntilPaused, long resumeTime){
        this.name = name;
        this.elapsed = elapsed;
        this.estimate = estimate;
        this.startTime = startTime;
        this.status = status;
        this.id = id;
        this.timeUntilPaused = timeUntilPaused;
        this.resumeTime = resumeTime;
    }

    public Task(String name){
        this.name = name;
        status = NOT_STARTED;
        id = -1;
    }

    public int getElapsed(){
        return elapsed;
    }

    public long getID() {
        return id;
    }

    public long getResumeTime(){
        return resumeTime;
    }

    public long getStartTime(){
        return startTime;
    }

    public int getStatus(){
        return status;
    }
    
    public String getStatusString(Context context){
        int status = getStatus();
        switch (status){
            case NOT_STARTED:
                return context.getString(R.string.NOT_STARTED);
            case ACTIVE:
                return context.getString(R.string.ACTIVE);
            case PAUSED:
                return context.getString(R.string.PAUSED);
            case ENDED:
                return context.getString(R.string.STOPPED);
            default:
                return context.getString(R.string.UNKNOWN);
        }
    }

    public String getTime(){
        if(status == ACTIVE) {
            Log.d("debug", resumeTime + " " + System.currentTimeMillis() + " " + timeUntilPaused);
            long millisecondsPassed = System.currentTimeMillis() - resumeTime;
            elapsed = ((int) millisecondsPassed) / 1000 + timeUntilPaused;
        }
        return elapsed / 60 + ":" + elapsed % 60;
    }

    public long getTimeUntilPaused(){
        return timeUntilPaused;
    }

    public void pause(){
        status = PAUSED;
        timeUntilPaused = elapsed;
    }

    public void resume(){
        status = ACTIVE;
        resumeTime = System.currentTimeMillis();
    }

    public void setName(String newName){
        name = newName;
    }

    public boolean setID(long id){
        if(this.id == -1){
            this.id = id;
            return true;
        }
        Log.d("info", "Tried to set ID when ID was already set!");
        return false;
    }

    public void start(){
        status = ACTIVE;
        resumeTime = startTime = System.currentTimeMillis();
    }

    public void end(){
        status = ENDED;
    }
}
