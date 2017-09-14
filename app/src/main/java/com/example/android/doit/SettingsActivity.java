package com.example.android.doit;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.doit.dataProjects.projectHelperDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.os.Build.VERSION_CODES.M;
import static com.example.android.doit.R.id.add_project;
import static com.example.android.doit.data.TaskContract.TaskEntry.COLUMN_ID_PROJECT_TASK;
import static com.example.android.doit.data.TaskContract.TaskEntry.CONTENT_URI;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_COLOR_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_ID_PROJECT;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.COLUMN_PROJECT_NAME;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.CONTENT_URI_PROJECTS;
import static com.example.android.doit.dataProjects.projectContract.projectEntry.TABLE_NAME_PROJECT;



public class SettingsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    SimpleCursorAdapter curosorAdapter;
    ContentValues values_project = new ContentValues();
    int id_choose = 0;
    boolean add_project_ = false;
    Cursor c ;
    ListView project_listview;

    //Colors for spinner
    public String[] colors_string = {"Blue","Orange","Red","Black"};
    int[] colors_color = {R.color.project_blue,R.color.project_orange,R.color.project_red,R.color.project_black};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        project_listview = (ListView) findViewById(R.id.project_listview);
        onClickAddProject();
        setupColorSpinner();
        OnClickItemProjectsListView();
        getSupportLoaderManager().initLoader(0,null,this);

        String[] from = {COLUMN_PROJECT_NAME,COLUMN_COLOR_PROJECT};
        int[] to = {R.id.listitem_project_name,R.id.listitem_project_circle};

        curosorAdapter = new SimpleCursorAdapter(this,R.layout.listitem_project,null,from,to,0);
        curosorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {



            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                        if (view.getId() == R.id.listitem_project_circle) {
                            int i = cursor.getInt(cursor.getColumnIndex(COLUMN_COLOR_PROJECT));
                            TextView tx = (TextView) view.findViewById(R.id.listitem_project_circle);
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
                        if (view.getId() == R.id.listitem_project_name) {
                            String s = cursor.getString(cursor.getColumnIndex(COLUMN_PROJECT_NAME));
                            TextView tt = (TextView) view.findViewById(R.id.listitem_project_name);
                            tt.setText(s);
                        }
                    return true;
                }
        });
        project_listview.setAdapter(curosorAdapter);
    }

    private void OnClickItemProjectsListView(){

        project_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cur = (Cursor) parent.getItemAtPosition(position);
                int which_task = cur.getInt(cur.getColumnIndex(COLUMN_ID_PROJECT));
                id_choose = which_task;
                openButtonSheet();

            }
        });

    }


    /*
    * Add - button
    * */
    private void onClickAddProject(){

        final Button add_project = (Button) findViewById(R.id.add_project);
        add_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getProjectName();
                if(add_project_ == true) {
                    getContentResolver().insert(CONTENT_URI_PROJECTS,values_project);
                    curosorAdapter.swapCursor(c);
                    curosorAdapter.changeCursor(c);
                    setNullProjectName();

                }
            }
        });
    }



    /*
    * Spinner with color */
    private void setupColorSpinner(){

        Spinner color_spinner = (Spinner) findViewById(R.id.spinner_color);
        color_spinner.setAdapter(new ColorAdapter(this,R.layout.listitem_project,colors_string));

        color_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                switch (selection){
                    case "Blue":
                        values_project.put(COLUMN_COLOR_PROJECT,R.color.project_blue);
                        break;
                    case "Orange":
                        values_project.put(COLUMN_COLOR_PROJECT,R.color.project_orange);
                        break;
                    case "Red":
                        values_project.put(COLUMN_COLOR_PROJECT,R.color.project_red);
                        break;
                    case "Black":
                        values_project.put(COLUMN_COLOR_PROJECT,R.color.project_black);
                        break;
                    default:
                        values_project.put(COLUMN_COLOR_PROJECT,0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getProjectName(){

        EditText ed_nameproject = (EditText) findViewById(R.id.add_project_name_project);
        String value = ed_nameproject.getText().toString().trim();

        if(value.isEmpty() || value == ""){
            add_project_ = false;
            Snackbar.make(findViewById(android.R.id.content),R.string.empty_project,Snackbar.LENGTH_SHORT).setAction("Empty project",null).show();
        }else {
            values_project.put(COLUMN_PROJECT_NAME, value);
            add_project_ = true;
        }
    }

    private void setNullProjectName() {
        EditText ed_nameproject = (EditText) findViewById(R.id.add_project_name_project);
        ed_nameproject.setText("");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {COLUMN_ID_PROJECT,COLUMN_PROJECT_NAME,COLUMN_COLOR_PROJECT};
        String selection = COLUMN_ID_PROJECT + ">=?";
        String[] selectionArgs = {String.valueOf(2)};
        return new CursorLoader(this,CONTENT_URI_PROJECTS,projection,selection,selectionArgs,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            curosorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        curosorAdapter.swapCursor(null);

    }

    class ColorAdapter extends ArrayAdapter {
        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public ColorAdapter(@NonNull Context context, @LayoutRes int resource, String[] objects) {
            super(context, resource, objects);
        }

        public View getCustomView(int position, View convertView,
                                  ViewGroup parent){

            View view = LayoutInflater.from(getContext()).inflate(R.layout.listitem_project,parent,false);
            TextView name_color = (TextView) view.findViewById(R.id.listitem_project_name);
            TextView circle_color = (TextView) view.findViewById(R.id.listitem_project_circle);

            name_color.setText(colors_string[position]);

            GradientDrawable gd = (GradientDrawable) circle_color.getBackground();
            gd.setColor(ContextCompat.getColor(view.getContext(),colors_color[position]));

            return view;
        }
    }

    //===== OPEN BOTTON SHEET ====
    public void openButtonSheet() {

        View view = getLayoutInflater().inflate(R.layout.procjet_bottomsheet, null);

        final Dialog mBottomSheetDialog = new Dialog(SettingsActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        LinearLayout finish_project = (LinearLayout) view.findViewById(R.id.project_bottomsheet_finish);

        finish_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Alert dialog

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                alertDialogBuilder
                        .setTitle("Are you sure?")
                        .setCancelable(false)
                        .setMessage("All tasks with this project will be deleted")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri currentUri = ContentUris.withAppendedId(CONTENT_URI_PROJECTS,id_choose);
                                getContentResolver().delete(currentUri, null,null);

                                String selection_maindb = COLUMN_ID_PROJECT_TASK + "=?";
                                String selectionArgs_maindb[] = {String.valueOf(id_choose)};

                                //Delete all task with this project
                                getContentResolver().delete(CONTENT_URI,selection_maindb,selectionArgs_maindb);
                                Snackbar.make(findViewById(android.R.id.content),R.string.finish_project,Snackbar.LENGTH_SHORT).setAction("Finished project",null).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                mBottomSheetDialog.dismiss();

            }
        });


    }
}



