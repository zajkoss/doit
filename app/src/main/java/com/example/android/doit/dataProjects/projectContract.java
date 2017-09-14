package com.example.android.doit.dataProjects;

import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.android.doit.data.TaskContract.CONTENT_AUTHORITY;
import static com.example.android.doit.data.TaskContract.PATH_TASK;

public class projectContract {


    public static final String CONTENT_AUTHORITY_PROJECT = "com.example.android.doit";
    public static final String PATH_PROJECT = "projects";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY_PROJECT);
    //one row
    public static final String PATH_PROJECT_ID = "projects/#";

    public static class projectEntry implements BaseColumns{

        public static final Uri CONTENT_URI_PROJECTS = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PROJECT);



        public static String TABLE_NAME_PROJECT = "projects";
        public static String COLUMN_ID_PROJECT = BaseColumns._ID;
        public static String COLUMN_PROJECT_NAME = "project_name";
        public static String COLUMN_COLOR_PROJECT = "project_color";
    }
}
