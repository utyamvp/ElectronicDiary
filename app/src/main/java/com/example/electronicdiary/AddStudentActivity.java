package com.example.electronicdiary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class AddStudentActivity extends AppCompatActivity {

    private DatabaseManager dbManager;
    private long groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_add_student);
        checkTableStructure();

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        groupId = intent.getLongExtra("group_id", -1);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        EditText firstNameEditText = findViewById(R.id.edit_text_first_name);
        EditText lastNameEditText = findViewById(R.id.edit_text_last_name);
        EditText recordBookNumberEditText = findViewById(R.id.edit_text_record_book_number);

        Button addStudentButton = findViewById(R.id.button_add_student);
        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String recordBookNumber = recordBookNumberEditText.getText().toString().trim();

                if (firstName.isEmpty() || lastName.isEmpty() || recordBookNumber.isEmpty()) {
                    Toast.makeText(AddStudentActivity.this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
                } else {
                    long studentId = dbManager.insertStudent(firstName, lastName, recordBookNumber, groupId);
                    if (studentId != -1) {
                        Toast.makeText(AddStudentActivity.this, "Студент добавлен", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddStudentActivity.this, "Ошибка при добавлении студента", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public void checkTableStructure() {
        DatabaseManager dbManager = new DatabaseManager(this);
        dbManager.open();

        Cursor cursor = dbManager.getTableInfo(DatabaseHelper.TABLE_STUDENTS);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int cid = cursor.getInt(cursor.getColumnIndex("cid"));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("type"));
                @SuppressLint("Range") int notNull = cursor.getInt(cursor.getColumnIndex("notnull"));
                @SuppressLint("Range") String defaultValue = cursor.getString(cursor.getColumnIndex("dflt_value"));
                @SuppressLint("Range") int primaryKey = cursor.getInt(cursor.getColumnIndex("pk"));

                Log.d("TableInfo", "Column ID: " + cid);
                Log.d("TableInfo", "Name: " + name);
                Log.d("TableInfo", "Type: " + type);
                Log.d("TableInfo", "Not Null: " + notNull);
                Log.d("TableInfo", "Default Value: " + defaultValue);
                Log.d("TableInfo", "Primary Key: " + primaryKey);
            }
            cursor.close();
        }

        dbManager.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbManager != null) {
            dbManager.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
