package com.example.android.doit.data;

import android.net.Uri;
import android.provider.BaseColumns;


public class TaskContract {
    private TaskContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.doit";
    public static final String PATH_TASK = "tasks";

    //one row
    public static final String PATH_TASK_ID = "tasks/#";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static class TaskEntry implements BaseColumns {


        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_TASK);

        public final static String TABLE_NAME = "tasks";
        public final static String _ID = "_id";
        public final static String COLUMN_TASK_DESCRIPTION = "task_description";
        public final static String COLUMN_PRIORITY = "priority";
        public final static String COLUMN_DATE = "date_";
        public final static String COLUMN_ID_PROJECT_TASK = "project_id";
        public final static String COLUMN_LOCATION  = "location";
        public final static String COLUMN_ALLDAY = "allday";


    }
}
