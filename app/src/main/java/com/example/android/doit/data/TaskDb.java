package com.example.android.doit.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.R.attr.version;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_ALLDAY;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_DATE;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_ID_PROJECT_TASK;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_LOCATION;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_PRIORITY;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION;
import static com.example.android.doit.data.TaskContract.TaskEntry.TABLE_NAME;
import static com.example.android.doit.data.TaskContract.TaskEntry._ID;



public class TaskDb extends SQLiteOpenHelper{

    private final static String DATABASE_NAME = "task.db";
    private final static int DATABASE_VERSION = 19;


    public TaskDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE = "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_TASK_DESCRIPTION + " TEXT, " +
                        COLUMN_PRIORITY + " INTEGER, " + COLUMN_DATE + " TEXT DEFAULT 0, " + COLUMN_ID_PROJECT_TASK + " INTEGER, "
                        + COLUMN_LOCATION + " TEXT, " + COLUMN_ALLDAY + " INTEGER DEFAULT 0 );";


        db.execSQL(SQL_CREATE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }
}
