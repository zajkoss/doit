package com.example.android.doit.dataProjects;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.doit.dataProjects.projectContract.projectEntry;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_COLOR_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_ID_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_PROJECT_NAME;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.TABLE_NAME_PROJECT;


public class projectHelperDatabase extends SQLiteOpenHelper {

    private final static int DATABASE_PROJECT_VERSION  = 4;
   private final static String DATABASE_PROJECT_NAME = "projects.db";

    public projectHelperDatabase(Context context) {
        super(context, DATABASE_PROJECT_NAME, null, DATABASE_PROJECT_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         String DATABASE_PROJECT_CREATE = "CREATE TABLE " + TABLE_NAME_PROJECT + "( " + COLUMN_ID_PROJECT +
                                            " INTEGER PRIMARY KEY AUTOINCREMENT, "+ COLUMN_PROJECT_NAME + " TEXT, "
                                                + COLUMN_COLOR_PROJECT + " INTEGER DEFAULT 0);";


        db.execSQL(DATABASE_PROJECT_CREATE);
        /*
        * Frist row post.0 Empty project
        */
        String insert = "INSERT INTO " + TABLE_NAME_PROJECT + "(" + COLUMN_ID_PROJECT + "," + COLUMN_PROJECT_NAME + "," + COLUMN_COLOR_PROJECT + ")"
        + " VALUES (1,'None',0) " ;
        db.execSQL(insert);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
