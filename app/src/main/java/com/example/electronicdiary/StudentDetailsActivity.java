package com.example.electronicdiary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class StudentDetailsActivity extends AppCompatActivity {

    private DatabaseManager dbManager;
    private long studentId;
    private long groupId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_student_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        studentId = intent.getLongExtra("student_id", -1);
        groupId = intent.getLongExtra("group_id", -1);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        // Обновляем данные студента на экране
        updateStudentDetails();

        Button editStudentButton = findViewById(R.id.button_edit_student);
        editStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentDetailsActivity.this, EditStudentActivity.class);
                intent.putExtra("student_id", studentId);
                startActivityForResult(intent, 1);
            }
        });

        Button deleteStudentButton = findViewById(R.id.button_delete_student);
        deleteStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.deleteStudent(studentId);
                Toast.makeText(StudentDetailsActivity.this, "Студент удален", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateStudentDetails() {
        TextView firstNameTextView = findViewById(R.id.text_student_first_name);
        TextView lastNameTextView = findViewById(R.id.text_student_last_name);
        TextView recordBookNumberTextView = findViewById(R.id.text_student_record_book_number);

        Cursor cursor = dbManager.fetchStudentById(studentId);
        if (cursor != null && cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_LAST_NAME));
            String recordBookNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_RECORD_BOOK_NUMBER));

            firstNameTextView.setText(firstName);
            lastNameTextView.setText(lastName);
            recordBookNumberTextView.setText(recordBookNumber);
            cursor.close();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Обновляем данные студента
            updateStudentDetails();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (dbManager != null) {
            dbManager.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbManager != null) {
            dbManager.close();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (dbManager != null) {
            dbManager.open();
        }
        // Обновите данные студента на экране
        updateStudentDetails();
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
