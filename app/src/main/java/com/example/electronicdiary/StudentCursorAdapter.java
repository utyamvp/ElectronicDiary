package com.example.electronicdiary;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class StudentCursorAdapter extends CursorAdapter {

    public StudentCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_student, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView firstNameTextView = view.findViewById(R.id.text_student_first_name);
        TextView lastNameTextView = view.findViewById(R.id.text_student_last_name);

        String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
        String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));

        firstNameTextView.setText(firstName);
        lastNameTextView.setText(lastName);
    }
}
