package com.example.android.doit;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.android.doit.dataProjects.projectHelperDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static com.example.android.doit.R.id.date;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_ALLDAY;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_DATE;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_ID_PROJECT_TASK;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_LOCATION;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_PRIORITY;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION;

import static com.example.android.doit.data.TaskContract.TaskEntry.CONTENT_URI;
import static com.example.android.doit.data.TaskContract.TaskEntry._ID;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_COLOR_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_ID_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_PROJECT_NAME;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.CONTENT_URI_PROJECTS;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.TABLE_NAME_PROJECT;



public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    SharedPreferences preferences;

    private Spinner mSpinner;
    private Spinner mSpinnerProject;
    int i = 0;
    int idToUpradge = 0;
    String go_date_full = "";
    String go_date_currently = "";
    String go_day = "";
    String go_time = "";
    String time_datebase = "";
    String date_database = "";
    long date_mili;
    long date_mili_current;
    SimpleCursorAdapter curosorAdapter;
    private Uri currentTaskUri ;
    TextView time_view;
    TextView date_view;
    android.widget.DatePicker date_picker;
    TimePicker time_picker;


    ContentValues values = new ContentValues();

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        preferences = this.getSharedPreferences("com.example.android.doit",Context.MODE_PRIVATE);
        time_view = (TextView) findViewById(R.id.time);
        date_view = (TextView) findViewById(date);



        // Edit/Add Task
        Intent intent = getIntent();
        currentTaskUri = intent.getData();
        if(currentTaskUri == null){
            setTitle("New Task");
        }else{
            setTitle("Edit Task");
            getSupportLoaderManager().initLoader(0,null,this);
        }

        setCurrenltyTime();
        setCurrenltyDate();
        setupDatePicker();
        setupSpinner();
        setupSpinnerProject();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor,menu);
        return true;
    }

    /* Save task */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:

                setupETTask();
                setupLocation();
                values.put(COLUMN_PRIORITY,i);


                SimpleDateFormat sdp = new SimpleDateFormat("dd.MM.yyyy.HH:mm");
                go_date_full = date_database + time_datebase ;
                go_date_currently = go_day + "." + go_time;

                try {
                    Date current_date = sdp.parse(go_date_currently);
                    date_mili_current = current_date.getTime();
                    Date date = sdp.parse(go_date_full);
                    date_mili = date.getTime();

                } catch (ParseException e) {
                    Log.e("Parse Exception",e.toString());
                }

                if(date_mili < date_mili_current){

                    Snackbar.make(findViewById(android.R.id.content),R.string.no_back,Snackbar.LENGTH_SHORT).show();
                }else {

                    values.put(COLUMN_DATE, "" + date_mili);
                    //=============================================
                    // Edit / Save ?

                    if (currentTaskUri == null) {

                        getContentResolver().insert(CONTENT_URI, values);
                        if(preferences.getBoolean("com.example.android.doit.doNotification",true))
                            newNotification();

                        finish();
                    } else {
                        getContentResolver().update(currentTaskUri, values, null, null);
                        if(preferences.getBoolean("com.example.android.doit.doNotification",true))
                            updateNotification();
                        finish();
                    }
                    break;
                }
        }

        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void updateNotification(){
        Uri currentTaskUri = ContentUris.withAppendedId(CONTENT_URI,idToUpradge);
        Cursor crs = getContentResolver().query(currentTaskUri,null,null,null,null);
        crs.moveToNext();
        int id_task =  crs.getInt(crs.getColumnIndex(_ID));
        String task = crs.getString(crs.getColumnIndex(COLUMN_TASK_DESCRIPTION));
        String time = crs.getString(crs.getColumnIndex(COLUMN_DATE));
        long time_mili = Long.parseLong(time);
        long before = preferences.getLong("com.example.android.doit.when",300000);
        Log.i("Notification","" + id_task + " " + task +" " + time_mili + " " + before);
        scheduleNotification(getNotification(task,time_mili),time_mili,id_task,before);
    }




    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void newNotification(){
        Cursor crs = getContentResolver().query(CONTENT_URI,null,null,null,_ID + " ASC");
        crs.moveToLast();
        int id_task =  crs.getInt(crs.getColumnIndex(_ID));
        String task = crs.getString(crs.getColumnIndex(COLUMN_TASK_DESCRIPTION));
        String time = crs.getString(crs.getColumnIndex(COLUMN_DATE));
        long time_mili = Long.parseLong(time);
        long before = preferences.getLong("com.example.android.doit.when",300000);
        Log.i("Notification","" + id_task + " " + task +" " + time_mili + " " + before);
        scheduleNotification(getNotification(task,time_mili),time_mili,id_task,before);
    }


    private void scheduleNotification(Notification notification, long when, int id,long before) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = when - before;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public Notification getNotification(String content,long when) {
        Intent i = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivities(this,0, new Intent[]{i},0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(content);
        builder.setWhen(when);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            builder.setShowWhen(true);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }

    /* Field task */
    public void setupETTask(){

        EditText et = (EditText) findViewById(R.id.edittext_task);
        String s = et.getText().toString().trim();
        values.put(COLUMN_TASK_DESCRIPTION,s);


    }

    /* Field Location */
    public void setupLocation(){

        EditText et = (EditText) findViewById(R.id.edittext_location);
        String s = et.getText().toString().trim();
        values.put(COLUMN_LOCATION,s);

    }

    /* DataPicker | TimePicker*/
    public void setupDatePicker(){

        final LinearLayout time =  (LinearLayout) findViewById(R.id.choosetime_editlayout);
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                Set up dialog with timepicker (24h view)
                */
                final Dialog dialog = new Dialog(v.getContext());
                dialog.setContentView(R.layout.time_picker);
                time_picker = (TimePicker) dialog.findViewById(R.id.time_picker);
                time_picker.setIs24HourView(true);
                dialog.setTitle("Choose time");
                dialog.show();

                /*User click OK */
                TextView time_picker_OK = (TextView) dialog.findViewById(R.id.time_picker_OK);
                time_picker_OK.setOnClickListener(new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        /*Save time in veriables */
                        int hour = time_picker.getHour();
                        int minute = time_picker.getMinute();
                        String my_hour = "" + hour;
                        String my_minute = "" + minute;

                        if(minute < 10){
                            my_minute = "0" + minute;
                        }

                        time_datebase = my_hour + ":" + minute;
                        time_view.setText(my_hour + ":" + my_minute);
                        dialog.dismiss();
//                        time_picker.setHour(hour);
//                        time_picker.setMinute(minute);


                    }
                });

                TextView time_picker_ALLDAY = (TextView) dialog.findViewById(R.id.time_picker_ALLDAY);
                time_picker_ALLDAY.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        time_view.setText("All day");
                        time_datebase = "23:59";
                        dialog.dismiss();
                        values.put(COLUMN_ALLDAY,1);
                    }
                });



            }
        });




        final LinearLayout date_ = (LinearLayout) findViewById(R.id.choosedate_editlayout);
        date_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    final Dialog dialog = new Dialog(v.getContext());
                    dialog.setContentView(R.layout.date_picker);

                    date_picker = (android.widget.DatePicker) dialog.findViewById(R.id.date_picker) ;
                    date_picker.setMinDate(System.currentTimeMillis() - 1000);
                    dialog.setTitle("Choose date");
                    dialog.show();

                    TextView date_picker_OK = (TextView) dialog.findViewById(R.id.date_picker_OK);
                    date_picker_OK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int year = date_picker.getYear();
                            int month = date_picker.getMonth();
                            int dayOfMonth = date_picker.getDayOfMonth();

                            String my_year = "" + year;
                            String my_month = "" + (month+1);
                            String my_day = "" + dayOfMonth;

                            String go_date = "" + my_day + "." + my_month + "." + my_year + ".";
                            //Set Edit_Layout date
                            if(year < 10){
                                my_year = "0" + my_year;
                            }if(month < 9){
                                my_month = "0" + my_month;
                            }if(dayOfMonth < 10){
                                my_day = "0" + my_day;
                            }

                            String complete_date = "" + my_day + "." + my_month + "." + my_year;
                            date_database = complete_date + ".";
                            date_view.setText(complete_date);
                            dialog.dismiss();
                        }
                    });
            }
        });
    }



    /*Set up spinner with priorytetes*/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        mSpinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter SpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_priority_options, R.layout.spiner_priority_item);


        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(SpinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("A")) {
                        i = 1;
                    } else if (selection.equals("B")) {
                        i = 2;
                    } else if (selection.equals("C")) {
                        i = 3;
                    } else if (selection.equals("D")) {
                        i = 4;
                    }else if(selection.equals("brak")){
                        i =0 ;
                    }
                }

            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                 values.put(COLUMN_PRIORITY,0);
            }
        });
    }


    /*
    * Methods that set up currentyl date for textview with date
    */
    private void setCurrenltyDate(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String my_year = "" + year;
        String my_month = "" + (month+1);
        String my_day = "" + day;

        go_day = "" + my_day + "." + my_month + "." + my_year ;

        if(year < 10){
            my_year = "0" + my_year;
        }if(month < 10){
            my_month = "0" + my_month;
        }if(day < 10){
            my_day = "0" + my_day;
        }

        String complete_date = "" + my_day + "." + my_month + "." + my_year;
        date_view.setText(complete_date);
//        If user dont choose date for task , task will have currently date
        date_database = complete_date + ".";
    }

    /*
    Method that set up currentlty time for textview with time
    */
    private void setCurrenltyTime(){

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        String my_miunte = "" + minute;
        go_time = hour + ":" + minute;
        if(minute < 10){
            my_miunte = "0" + my_miunte;
        }
        String s = hour +":"+ my_miunte;
        time_datebase = s;
        time_view.setText(s);
    }

    private void setupSpinnerProject(){

        mSpinnerProject = (Spinner) findViewById(R.id.spinner_project);
        mSpinnerProject.setAdapter(getCursorAdapter());

        mSpinnerProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                values.put(COLUMN_ID_PROJECT_TASK,curosorAdapter.getItemId(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /*
    SimpleCursorAdpater for spinner with project, custom view.
    */
   private SimpleCursorAdapter getCursorAdapter(){

       /*
       Get values from databaseProjects
       */
       String[] from = {COLUMN_PROJECT_NAME,COLUMN_COLOR_PROJECT};
       int[] to = {R.id.listitem_project_name,R.id.listitem_project_circle};

       final Cursor c ;
       projectHelperDatabase phd = new projectHelperDatabase(this);
       SQLiteDatabase sql_phd = phd.getReadableDatabase();
       String[] projection = {COLUMN_ID_PROJECT,COLUMN_PROJECT_NAME,COLUMN_COLOR_PROJECT};
       c = sql_phd.query(TABLE_NAME_PROJECT,projection,null,null,null,null,null);

       curosorAdapter = new SimpleCursorAdapter(this,R.layout.listitem_project,c,from,to,0);

       /*
       Set custom view for adapter
        */
       curosorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
           @Override
           public boolean setViewValue(View view, Cursor cursor, int columnIndex) {


               TextView tx = (TextView) view.findViewById(R.id.listitem_project_circle);
               TextView tt = (TextView) view.findViewById(R.id.listitem_project_name);

                    if (view.getId() == R.id.listitem_project_circle) {

                        if(cursor.getPosition() == 0){
                            /*
                            * View gone for 1st project Empty without color
                            */
                            tx.setVisibility(View.GONE);
                        }else {
                            /* Others projects
                            */

                            tx.setVisibility(View.VISIBLE);
                            int i = cursor.getInt(cursor.getColumnIndex(COLUMN_COLOR_PROJECT));

                            GradientDrawable gd = (GradientDrawable) tx.getBackground();
                            Color color = new Color();
                            int color_int = R.color.project_blue;
                            switch (i) {
                                case R.color.project_blue:
                                    color_int = R.color.project_blue;
                                    break;
                                case R.color.project_orange:
                                    color_int = R.color.project_orange;
                                    break;
                                case R.color.project_red:
                                    color_int = R.color.project_red;
                                    break;
                                case R.color.project_black:
                                    color_int = R.color.project_black;
                                    break;
                            }
                            gd.setColor(ContextCompat.getColor(view.getContext(), color_int));

                    }
                }


               if(view.getId() == R.id.listitem_project_name){
                   String s = cursor.getString(cursor.getColumnIndex(COLUMN_PROJECT_NAME));
                   tt.setText(s);
               }
               return true;
           }
       });

       return curosorAdapter;
   }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {
                _ID,
                COLUMN_TASK_DESCRIPTION,
                COLUMN_PRIORITY,
                COLUMN_DATE,
                COLUMN_ID_PROJECT_TASK,
                COLUMN_LOCATION,
                COLUMN_ALLDAY
        };
        return new CursorLoader(this,currentTaskUri,projection,null,null,null);



    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(data.moveToNext()){
            /* Location and Task description follow-up */
            idToUpradge = data.getInt(data.getColumnIndex(_ID));
            EditText et = (EditText) findViewById(R.id.edittext_task);
            EditText et_loc = (EditText) findViewById(R.id.edittext_location);
            et.setText(data.getString(data.getColumnIndex(COLUMN_TASK_DESCRIPTION)));
            et_loc.setText(data.getString(data.getColumnIndex(COLUMN_LOCATION)));


            /* Time */
            String date_mili = data.getString(data.getColumnIndex(COLUMN_DATE));
            SimpleDateFormat date_format = new SimpleDateFormat("HH:mm");
            long long_date_mili = Long.parseLong(date_mili);
            Date date = new Date(long_date_mili);
            String formatted_time = date_format.format(date);
            if(data.getInt(data.getColumnIndex(COLUMN_ALLDAY)) == 1){
                time_view.setText("All day");
            }else {
                time_view.setText(formatted_time);
            }
            time_datebase = formatted_time;

            /* Date */

                date_format = new SimpleDateFormat("dd.MM.yyyy");
                String formatted_date = date_format.format(date);
                date_view.setText(formatted_date);
                date_database = formatted_date + ".";

            /* Spinner project */
            int spinnerPrio = data.getInt(data.getColumnIndex(COLUMN_PRIORITY));
            mSpinner.setSelection(spinnerPrio);

            /* Spinner project */
            int spinnerProject = data.getInt(data.getColumnIndex(COLUMN_ID_PROJECT_TASK));
            Cursor crs = getContentResolver().query(CONTENT_URI_PROJECTS,null,null,null,null);
            while(crs.moveToNext()){
                if(crs.getInt(crs.getColumnIndex(COLUMN_ID_PROJECT)) == spinnerProject)
                    mSpinnerProject.setSelection(crs.getPosition());
            }





        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }







}
