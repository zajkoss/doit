package com.example.android.doit;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.doit.data.TaskDb;
import com.example.android.doit.dataProjects.projectHelperDatabase;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.R.attr.id;
import static android.R.attr.priority;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.example.android.doit.R.string.projects;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_ALLDAY;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_DATE;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_ID_PROJECT_TASK;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_PRIORITY;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION;
import static com.example.android.doit.data.TaskContract.TaskEntry.CONTENT_URI;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_COLOR_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_ID_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_PROJECT_NAME;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.CONTENT_URI_PROJECTS;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.TABLE_NAME_PROJECT;



public class TaskCursorAdapter extends CursorAdapter {


    public TaskCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        /*Time*/
        TextView time = (TextView) view.findViewById(R.id.time_listitem) ;
        int IsAllDay = cursor.getInt(cursor.getColumnIndex(COLUMN_ALLDAY));
        if(IsAllDay == 1){
            time.setVisibility(View.INVISIBLE);
        }else {
            time.setVisibility(View.VISIBLE);
            SimpleDateFormat time_sdp = new SimpleDateFormat("HH:mm");
            long date_sql = Long.parseLong(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
            Date date_datebase = new Date(date_sql);
            String date_finish = time_sdp.format(date_datebase);
            time.setText(date_finish);
        }



        /*Task description*/
        TextView task_des  = (TextView)  view.findViewById(R.id.task_listitem);
        String task_des_String = cursor.getString(cursor.getColumnIndex(COLUMN_TASK_DESCRIPTION));
        task_des.setText(task_des_String);

        /* Priority task */
        TextView priority = (TextView) view.findViewById(R.id.priority_listitem);
        int prio = cursor.getInt(cursor.getColumnIndex(COLUMN_PRIORITY));

        priority.setVisibility(View.VISIBLE);
        switch (prio){
            case 1:
                priority.setText("A");
                priority.setBackgroundColor(ContextCompat.getColor(view.getContext(),R.color.priority1));
                break;
            case 2:
                priority.setText("B");
                priority.setBackgroundColor(ContextCompat.getColor(view.getContext(),R.color.priority2));

                break;
            case 3:
                priority.setText("C");
                priority.setBackgroundColor(ContextCompat.getColor(view.getContext(),R.color.priority3));
                break;
            case 4:
                priority.setText("D");
                priority.setBackgroundColor(ContextCompat.getColor(view.getContext(),R.color.priority4));
                break;
            case 0:
                priority.setVisibility(View.GONE);
                break;
            default:
                priority.setVisibility(View.GONE);
        }


        /* Project  */
        TextView taskcursoradapter_project_name = (TextView) view.findViewById(R.id.project_listview);
        int id_priority = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PROJECT_TASK));

        /* Look for project */
        String sel = COLUMN_ID_PROJECT + "=?";
        String[] selArgs = {String.valueOf(id_priority)};

//        Uri project = ContentUris.withAppendedId(CONTENT_URI,id_priority);
        String[] pro = {COLUMN_ID_PROJECT,COLUMN_PROJECT_NAME,COLUMN_COLOR_PROJECT};
        cursor = context.getContentResolver().query(CONTENT_URI_PROJECTS,pro,sel,selArgs,null);
        cursor.moveToFirst();

        TextView circle = (TextView) view.findViewById(R.id.circle_listitem_main);

        /* For empty */
        if(id_priority == 1) {
            taskcursoradapter_project_name.setVisibility(View.GONE);
            circle.setVisibility(View.GONE);
        }else {
            /*Others */
            circle.setVisibility(View.VISIBLE);
            taskcursoradapter_project_name.setVisibility(View.VISIBLE);
            String project_name = cursor.getString(cursor.getColumnIndex(COLUMN_PROJECT_NAME));
            if (project_name.isEmpty()) {
                taskcursoradapter_project_name.setText("");
            } else {
                taskcursoradapter_project_name.setText(project_name);
            }
            int color_db = cursor.getInt(cursor.getColumnIndex(COLUMN_COLOR_PROJECT));
            GradientDrawable gd = (GradientDrawable) circle.getBackground();
            int color = 0;
            switch (color_db) {
                case R.color.project_blue:
                    color = R.color.project_blue;
                    break;
                case R.color.project_orange:
                    color = R.color.project_orange;
                    break;
                case R.color.project_red:
                    color = R.color.project_red;
                    break;
                case R.color.project_black:
                    color = R.color.project_black;
                    break;
            }
            gd.setColor(ContextCompat.getColor(view.getContext(), color));

        }

        cursor.close();

}}
