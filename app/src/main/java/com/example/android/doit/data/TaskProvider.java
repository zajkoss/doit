package com.example.android.doit.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.doit.MainActivity;
import com.example.android.doit.dataProjects.projectHelperDatabase;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.IllegalFormatException;

import static android.R.attr.value;
import static com.example.android.doit.R.id.date;
import static com.example.android.doit.data.TaskContract.CONTENT_AUTHORITY;
import static com.example.android.doit.data.TaskContract.PATH_TASK;
import static com.example.android.doit.data.TaskContract.PATH_TASK_ID;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_ALLDAY;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_DATE;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_ID_PROJECT_TASK;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_LOCATION;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_PRIORITY;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION;
import static com.example.android.doit.data.TaskContract.TaskEntry.CONTENT_URI;
import static com.example.android.doit.data.TaskContract.TaskEntry.TABLE_NAME;
import static com.example.android.doit.data.TaskContract.TaskEntry._ID;
import static com.example.android.doit.dataProjects.projectContract.CONTENT_AUTHORITY_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.PATH_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.PATH_PROJECT_ID;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_COLOR_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_ID_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_PROJECT_NAME;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.TABLE_NAME_PROJECT;


public class TaskProvider extends ContentProvider {

    private TaskDb mDataBase;
    private projectHelperDatabase mDataBaseProject;

    public static final int TASK = 100;
    public static final int TASK_ID = 101;
    public static final int PROJECT = 102;
    public static final int PROJECT_ID = 103;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY,PATH_TASK,TASK);
        sUriMatcher.addURI(CONTENT_AUTHORITY,PATH_TASK_ID,TASK_ID);
        sUriMatcher.addURI(CONTENT_AUTHORITY_PROJECT,PATH_PROJECT,PROJECT);
        sUriMatcher.addURI(CONTENT_AUTHORITY_PROJECT,PATH_PROJECT_ID,PROJECT_ID);
    }

    @Override
    public boolean onCreate() {
        mDataBase = new TaskDb(getContext());
        mDataBaseProject = new projectHelperDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDataBase.getReadableDatabase();
        SQLiteDatabase database_project = mDataBaseProject.getReadableDatabase();
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);

        switch (match){
            case TASK:

                projection = new String[]{_ID, COLUMN_TASK_DESCRIPTION, COLUMN_PRIORITY, COLUMN_DATE, COLUMN_ID_PROJECT_TASK, COLUMN_LOCATION,COLUMN_ALLDAY};
                if(sortOrder == null){
                    sortOrder = COLUMN_DATE + " ASC";
                }
                cursor = database.query(TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);

                break;
            case TASK_ID:
                selection = TaskContract.TaskEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TABLE_NAME,projection,selection,selectionArgs,null,null,null);
                break;
            case PROJECT:
                cursor = database_project.query(TABLE_NAME_PROJECT,projection,selection,selectionArgs,null,null,null);
                break;
            case PROJECT_ID:
                selection = PROJECT_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database_project.query(TABLE_NAME_PROJECT,projection,selection,selectionArgs,null,null,null);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case TASK:
                return insertTask(uri,values);
            case PROJECT:
                return insertProject(uri,values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }


    private Uri insertProject(Uri uri ,ContentValues values){
        SQLiteDatabase db = mDataBaseProject.getWritableDatabase();
        long id = db.insert(TABLE_NAME_PROJECT,null,values);
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);

    }
    private Uri insertTask(Uri uri,ContentValues values){

        SQLiteDatabase db = mDataBase.getWritableDatabase();

        long id = db.insert(TABLE_NAME,null,values);
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,id);


    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDataBase.getWritableDatabase();
        SQLiteDatabase db_project = mDataBaseProject.getWritableDatabase();
        int rowDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match){
            case TASK:
                rowDeleted = db.delete(TABLE_NAME,selection,selectionArgs);
                break;
            case TASK_ID:
                selection = TaskContract.TaskEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = db.delete(TABLE_NAME,selection,selectionArgs);
                break;
            case PROJECT:
                rowDeleted = db_project.delete(TABLE_NAME_PROJECT,selection,selectionArgs);
                break;
            case PROJECT_ID:
                selection = COLUMN_ID_PROJECT + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowDeleted = db_project.delete(TABLE_NAME_PROJECT,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if(rowDeleted != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match  = sUriMatcher.match(uri);
        switch (match){
            case TASK_ID:
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateTask(uri,values,selection,selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for" + uri);
        }

    }

    private int updateTask(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs){

        SQLiteDatabase db = mDataBase.getWritableDatabase();
        getContext().getContentResolver().notifyChange(uri,null);
        return db.update(TABLE_NAME,values,selection,selectionArgs);

    }
}


