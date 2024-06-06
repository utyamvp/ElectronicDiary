package com.example.electronicdiary;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class GroupListAdapter extends CursorAdapter {

    public GroupListAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_item_group3, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewGroupName = view.findViewById(R.id.text_view_group_name);
        String groupName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.GROUP_NAME));
        textViewGroupName.setText(groupName);
    }
}

