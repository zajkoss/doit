package com.example.android.doit;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.TextView;
import java.util.List;



public class BaseTreeAdapter extends CursorTreeAdapter {

    public BaseTreeAdapter(Cursor cursor, Context context) {
        super(cursor, context);
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        return null;
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.card_view_list_item,parent,false);
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        TextView tt = (TextView) view.findViewById(R.id.list_group_name_header);
        tt.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
        tt.setTypeface(null, Typeface.BOLD);
        tt.setHintTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
        tt.setText(cursor.getString(cursor.getColumnIndex("days")));

        TextView tx = (TextView) view.findViewById(R.id.list_group_name_header_day);
        tx.setText(cursor.getString(cursor.getColumnIndex("date")));
        tx.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
//        int padding__left = 32;
//        int padding_right = 16;
//        int padding_topbottom = 12;
//        final float scale = context.getResources().getDisplayMetrics().density;
//            view.setPadding((int)(padding__left * scale + 0.5f),(int)(padding_topbottom * scale + 0.5f),
//                    (int)(padding_right * scale + 0.5f),(int)(padding_topbottom* scale + 0.5f));
            TaskCursorAdapter taskCursorAdapter = new TaskCursorAdapter(context,cursor);
            taskCursorAdapter.bindView(view,context,cursor);
    }
}

