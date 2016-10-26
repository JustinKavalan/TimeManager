package com.weotri.timemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.lang.annotation.Target;
import java.util.ArrayList;

/**
 * Created by cchou on 7/29/16.
 */
public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "TimeManager.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TasksDB.TABLE_NAME + " (" +
                    TasksDB._ID + " INTEGER PRIMARY KEY," +
                    TasksDB.COLUMN_NAME + TEXT_TYPE + COMMA_SEP +
                    TasksDB.COLUMN_NAME_ESTIMATE + TEXT_TYPE + COMMA_SEP +
                    TasksDB.COLUMN_NAME_ELAPSED + TEXT_TYPE + COMMA_SEP +
                    TasksDB.COLUMN_NAME_START_TIME + TEXT_TYPE + COMMA_SEP +
                    TasksDB.COLUMN_NAME_STATUS + TEXT_TYPE + COMMA_SEP +
                    TasksDB.COLUMN_NAME_TIME_UNTIL_PAUSED + TEXT_TYPE + COMMA_SEP +
                    TasksDB.COLUMN_NAME_RESUME_TIME + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TasksDB.TABLE_NAME;

    public static abstract class TasksDB implements BaseColumns {
            public static final String TABLE_NAME = "Tasks";
            public static final String COLUMN_NAME = "Name";
            public static final String COLUMN_NAME_ESTIMATE = "Estimate";
            public static final String COLUMN_NAME_ELAPSED = "Elapsed";
            public static final String COLUMN_NAME_START_TIME = "Start";
            public static final String COLUMN_NAME_STATUS = "Status";
            public static final String COLUMN_NAME_TIME_UNTIL_PAUSED = "TimeUntilPaused";
            public static final String COLUMN_NAME_RESUME_TIME = "ResumeTime";
    }


    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    public void addToDB(Task task){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put(TasksDB.COLUMN_NAME, task.name);
        content.put(TasksDB.COLUMN_NAME_ELAPSED, task.getElapsed());
        content.put(TasksDB.COLUMN_NAME_STATUS, task.getStatus());
        content.put(TasksDB.COLUMN_NAME_ESTIMATE, task.estimate);
        content.put(TasksDB.COLUMN_NAME_START_TIME, task.getStartTime());
        content.put(TasksDB.COLUMN_NAME_TIME_UNTIL_PAUSED, task.getTimeUntilPaused());
        content.put(TasksDB.COLUMN_NAME_RESUME_TIME, task.getResumeTime());
        long rowID = db.insert(TasksDB.TABLE_NAME, null, content);
        db.close();
        task.setID(rowID);
        Log.d("info", "Name:" + task.name + " Elapsed:" + task.getElapsed() + "Time until paused:" + task.getTimeUntilPaused() +  "task added to DB");
    }

    public void deleteRow(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        // Define 'where' part of query.
        String selection = TasksDB._ID + " LIKE ?";
// Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(id) };
// Issue SQL statement.
        db.delete(TasksDB.TABLE_NAME, selection, selectionArgs);

    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
        Log.d("info", "Database upgraded");
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<Task> readDB(){
        ArrayList<Task> content = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {TasksDB.COLUMN_NAME, TasksDB.COLUMN_NAME_ELAPSED, TasksDB.COLUMN_NAME_ESTIMATE,
                TasksDB.COLUMN_NAME_START_TIME, TasksDB.COLUMN_NAME_STATUS, TasksDB._ID, TasksDB.COLUMN_NAME_TIME_UNTIL_PAUSED, TasksDB.COLUMN_NAME_RESUME_TIME};
        Cursor c = db.query(
                TasksDB.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        c.moveToFirst();

        if(c.getCount() > 0) {
            while (!c.isAfterLast()) {
                Task cur = new Task(c.getString(c.getColumnIndex(TasksDB.COLUMN_NAME)),
                        c.getInt(c.getColumnIndex(TasksDB.COLUMN_NAME_ELAPSED)),
                        c.getInt(c.getColumnIndex(TasksDB.COLUMN_NAME_ESTIMATE)),
                        c.getLong(c.getColumnIndex(TasksDB.COLUMN_NAME_START_TIME)),
                        c.getInt(c.getColumnIndex(TasksDB.COLUMN_NAME_STATUS)),
                        c.getLong(c.getColumnIndex(TasksDB._ID)),
                        c.getInt(c.getColumnIndex(TasksDB.COLUMN_NAME_TIME_UNTIL_PAUSED)),
                        c.getLong(c.getColumnIndex(TasksDB.COLUMN_NAME_RESUME_TIME)));
                content.add(cur);
                c.moveToNext();
            }
        }

        db.close();
        return content;
    }

    public void updateDB(Task task){
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues content = new ContentValues();
        content.put(TasksDB.COLUMN_NAME, task.name);
        content.put(TasksDB.COLUMN_NAME_ELAPSED, task.getElapsed());
        content.put(TasksDB.COLUMN_NAME_STATUS, task.getStatus());
        content.put(TasksDB.COLUMN_NAME_ESTIMATE, task.estimate);
        content.put(TasksDB.COLUMN_NAME_START_TIME, task.getStartTime());
        content.put(TasksDB.COLUMN_NAME_TIME_UNTIL_PAUSED, task.getTimeUntilPaused());
        content.put(TasksDB.COLUMN_NAME_RESUME_TIME, task.getResumeTime());

        String selection = TasksDB._ID + " LIKE ?";
        String[] selectionArgs = { task.getID() + "" };

        db.update(
                TasksDB.TABLE_NAME,
                content,
                selection,
                selectionArgs);


        Log.d("info", "Name:" + task.name + " Elapsed:" + task.getElapsed() + "Time until paused:" + task.getTimeUntilPaused() +  "task added to DB");
    }
}
