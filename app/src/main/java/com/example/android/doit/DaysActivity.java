package com.example.android.doit;

import android.app.Dialog;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CursorTreeAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.R.attr.data;
import static android.R.attr.datePickerStyle;
import static android.graphics.Typeface.BOLD;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.widget.Toast.LENGTH_SHORT;
import static com.example.android.doit.R.id.date;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_ALLDAY;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_DATE;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_ID_PROJECT_TASK;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_LOCATION;
import static com.example.android.doit.data.TaskContract.TaskEntry.CONTENT_URI;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_PRIORITY;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_TASK_DESCRIPTION;
import static com.example.android.doit.data.TaskContract.TaskEntry._ID;


public class DaysActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{


    ExpandableListView expandableListView;
    BaseTreeAdapter bs;
    MainActivity mainAct;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days);
        setupBottomNavigationView();
        setupFloatingAcitonButton();
        setupExpandListView();

    }




    public void setupBottomNavigationView(){
        BottomNavigationView bottom_navigation_view = (BottomNavigationView) findViewById(R.id.Bottomnavigation);
//        bottom_navigation_view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        bottom_navigation_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.today_menu_item:
                        Intent i = new Intent(DaysActivity.this,MainActivity.class);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.days_menu_item:

                        break;
                }
                return true;

            }
        });
    }

    public void setupFloatingAcitonButton(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(DaysActivity.this,EditActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                openSetting();
                break;
            case R.id.action_notification:
                Intent i =  new Intent(DaysActivity.this,NotificationActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openSetting(){
        //Create new Intnet and stari it
        Intent i =  new Intent(DaysActivity.this,SettingsActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    public ArrayList<String> getHeaders(int list) {
        HashMap<String,String> mapNextDays = new HashMap<>();
        ArrayList<String> listHeaderNext7Dates = new ArrayList<>();
        ArrayList<String> listHeaderNext7Days = new ArrayList<>();
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        for (int i = 0 ; i < 7; i++){
            if(i != 0){
                day = day + 1;
            }
            String my_year = "" + year;
            String my_month = "" + (month+1);
            String my_day = "" + day;
            Date d = null;
            if(year < 10){
                my_year = "0" + my_year;
            }if(month < 10){
                my_month = "0" + my_month;
            }if(day < 10){
                my_day = "0" + my_day;
            }
            String complete_date = "" + my_day + "." + my_month + "." + my_year ;
            SimpleDateFormat format_date_ = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat format_date = new SimpleDateFormat("EEEE", Locale.ENGLISH);
            SimpleDateFormat format_date_nextdays = new SimpleDateFormat("dd MMM",Locale.ENGLISH);
            try {
               d = format_date_.parse(complete_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String date_go = "";
            String day_go = "";
            if(i == 0){
                String s = format_date_nextdays.format(d);
                date_go = "" + s ;
                day_go = "Today";
            }else if(i == 1) {
                String s = format_date_nextdays.format(d);
                date_go = "" + s;
                day_go = "Tomorrow";
            }else{
                    date_go = format_date_nextdays.format(d);
                    day_go = format_date.format(d);
            }
            mapNextDays.put(date_go,day_go);
            listHeaderNext7Days.add(day_go);
            listHeaderNext7Dates.add(date_go);

        }

        if(list == 0)
            return listHeaderNext7Dates;
        if(list == 1)
            return listHeaderNext7Days;
        else
            return null;


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void setupExpandListView(){


        getSupportLoaderManager().initLoader(0,null,this);



        String[] columns = {"_id","date","days"};

        String[] array_dates = new String[7];
        String[] array_days = new String[7];

        array_days = getHeaders(1).toArray(array_days);
        array_dates = getHeaders(0).toArray(array_dates);





        MatrixCursor matrixCursor = new MatrixCursor(columns);
        for(int i = 0 ; i < array_days.length ; i++){
            matrixCursor.addRow(new String[]{"" + i,array_dates[i],array_days[i]});

        }


        expandableListView = (ExpandableListView) findViewById(R.id.expand_listview);



        bs = new BaseTreeAdapter(matrixCursor,this);
        expandableListView.setAdapter(bs);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Cursor c = bs.getChild(groupPosition, childPosition);
                int which_task = c.getInt(c.getColumnIndex(_ID));
                openButtonSheet(DaysActivity.this,which_task);
                return true;
            }
        });



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

    public void goCursors() {
        /*
        * Today time miliseconds
        */
        String projection[] = {
                _ID,
                COLUMN_TASK_DESCRIPTION,
                COLUMN_PRIORITY,
                COLUMN_DATE,
                COLUMN_ID_PROJECT_TASK,
                COLUMN_LOCATION,
                COLUMN_ALLDAY
        };
        String selection = COLUMN_DATE + ">=? AND " + COLUMN_DATE + "<=?" ;
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
        String[] selectionArgs = {start,end};

        //1
        Cursor crs = getContentResolver().query(CONTENT_URI,projection,selection,selectionArgs,COLUMN_DATE + " ASC");
        bs.setChildrenCursor(0,crs);
        //Others
        int count = 1;
        while (count < 7){
            mili_start += 86400000;
            mili += 86400000;
            start =  "" + mili_start;
            end = "" + mili;
            String[] selectionArgs2 = {start,end};
            crs = getContentResolver().query(CONTENT_URI,projection,selection,selectionArgs2,COLUMN_DATE + " ASC");
            bs.setChildrenCursor(count,crs);
            count ++;
        }

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


        return new CursorLoader(this,CONTENT_URI,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            goCursors();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }





}
