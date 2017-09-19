package com.example.android.doit;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.doit.data.TaskContract;
import com.example.android.doit.data.TaskDb;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.action;
import static android.R.attr.cacheColorHint;
import static android.R.attr.data;
import static android.R.attr.delay;
import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.os.Build.VERSION_CODES.N;
import static android.view.View.Y;
import static android.widget.Toast.LENGTH_SHORT;
import static com.example.android.doit.data.TaskContract.*;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_ALLDAY;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_DATE;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_ID_PROJECT_TASK;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_LOCATION;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_PRIORITY;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION;
import static com.example.android.doit.data.TaskContract.TaskEntry.CONTENT_URI;
import static com.example.android.doit.data.TaskContract.TaskEntry.TABLE_NAME;
import static com.example.android.doit.data.TaskContract.TaskEntry._ID;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_COLOR_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_ID_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_PROJECT_NAME;
import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // New TaskCursorAdapter object_1
    public TaskCursorAdapter mAdapter;


    //*************************************************************

            //Method that create options menu use main_menu.xml
            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.main_menu,menu);
                return true;
            }

            /*
            * Create menu options
            */
            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_settings:
                        openSetting();
                        break;
                    case R.id.action_notification:
                        Intent i =  new Intent(MainActivity.this,NotificationActivity.class);
                        startActivity(i);
                        break;
                }
                return super.onOptionsItemSelected(item);
            }


            /*Method that open activity SettingsActivity
            * Used in Create Menu */
            private void openSetting(){
                //Create new Intnet and start it
                Intent i =  new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(i);
            }

    //*************************************************************


    // OnCreate method
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the content of the activity to activity_main layout
        setContentView(R.layout.activity_main);

        //Set up title on the toolbar
        setTitle("Today");
        getSupportLoaderManager().initLoader(0,null,this);

        //Set up UI
        setupBottomNavigationView();
        setupFloatingAcitonButton();

        // Set up TaskCursorAdapter
        //===================================================================
        ListView taskListView = (ListView) findViewById(R.id.list_view_today);
        mAdapter =  new TaskCursorAdapter(this,null);

        View empty_view = findViewById(R.id.empty_view);
        taskListView.setEmptyView(empty_view);

        taskListView.setAdapter(mAdapter);

        //===================================================================




        //
        taskListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cur = (Cursor) parent.getItemAtPosition(position);
                int which_task = cur.getInt(cur.getColumnIndex(_ID));
                openButtonSheet(MainActivity.this,which_task);
            }
        });




    }



    /*Method that find the BottomNavigationView and set up items
       */
    public void setupBottomNavigationView(){
        BottomNavigationView bottom_navigation_view = (BottomNavigationView) findViewById(R.id.activity_main_bottom_navigation);
        bottom_navigation_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.today_menu_item:
                        break;
                    case R.id.days_menu_item:

                        Intent i = new Intent(MainActivity.this,DaysActivity.class);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        break;
                }
               return true;

            }
        });
    }

    /*
         Method that find the FloatingActionButton and set up items
         Open EditAcitvity
    */
    public void setupFloatingAcitonButton(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i =  new Intent(MainActivity.this,EditActivity.class);
                startActivity(i);
            }
        });
    }






    //===== LOAD MANAGER =============

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String projection[] = {
                _ID,
                COLUMN_TASK_DESCRIPTION,
                COLUMN_PRIORITY,
                COLUMN_DATE,
                COLUMN_ID_PROJECT_TASK,
                COLUMN_LOCATION,
                COLUMN_ALLDAY,COLUMN_ID_PROJECT,COLUMN_PROJECT_NAME,COLUMN_COLOR_PROJECT

                };

        String selection = COLUMN_DATE + "<=?" ;
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.HH:mm");
        String date_ = day + "." + month + "." + year + ".23:59";
        String date__  =day + "." + month + "." + year + ".0:01";
        long mili = 1;
        long mili_start = 0;
        try {


            Date date2 = sdf.parse(date_);
            mili = date2.getTime();
            date2 = sdf.parse(date__);
            mili_start = date2.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String start =  "" + mili_start;
        String end = "" + mili;
        String[] selectionArgs = {end};


        return new CursorLoader(this,CONTENT_URI,projection,selection,selectionArgs,COLUMN_DATE + " ASC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }



    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
    }

    public void openButtonSheet(final Context context,int p){

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet,null);
        LinearLayout actionLocation = (LinearLayout) view.findViewById(R.id.location_bottomsheet);
        LinearLayout actionEdit = (LinearLayout) view.findViewById(R.id.edit_bottomsheet);
        LinearLayout actionDone = (LinearLayout) view.findViewById(R.id.done_bottomsheet);
        final Dialog mBottomSheetDialog = new Dialog(context,R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();
        final int position_choose = p;


        actionDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                Uri currentTaskUri = ContentUris.withAppendedId(CONTENT_URI,position_choose);
//                PendingIntent pi = EditActivity.alarmManagerHashMap.get(position_choose);
//                if(pi != null) {
//                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                    alarmManager.cancel(pi);
//                }


                Intent notificationIntent = new Intent(v.getContext(), NotificationPublisher.class);
                notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, position_choose);
                PendingIntent pi = PendingIntent.getBroadcast(v.getContext(), position_choose, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pi);




                getContentResolver().delete(currentTaskUri, null,null);
                mBottomSheetDialog.cancel();
                showMessage();
            }
        });

        actionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,EditActivity.class);
                Uri currentTaskUri = ContentUris.withAppendedId(CONTENT_URI,position_choose);
                intent.setData(currentTaskUri);
                mBottomSheetDialog.cancel();
                startActivity(intent);

            }
        });

        actionLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String projection[] = {_ID,COLUMN_LOCATION,COLUMN_DATE};
                Cursor c = getContentResolver().query(CONTENT_URI,projection,null,null,null);



                while(c.moveToNext()){
                    int id = c.getInt(c.getColumnIndex(_ID));
                    if(id == position_choose){
                        int p = c.getPosition();
                        c.moveToPosition(p);
                        break;
                    }

                }

                String address = c.getString(c.getColumnIndex(COLUMN_LOCATION));


                if(address.isEmpty() | address == null){

                    Snackbar.make(v,R.string.task_no_location,Snackbar.LENGTH_SHORT).setAction("No localization",null).show();

                }else {
                    String uri = String.format(Locale.ENGLISH, "geo:0,0?q=" + address, null, null);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }

            }
        });

    }

    private void showMessage(){

        Snackbar.make(findViewById(android.R.id.content),R.string.task_done,Snackbar.LENGTH_SHORT).show();
    }









}
