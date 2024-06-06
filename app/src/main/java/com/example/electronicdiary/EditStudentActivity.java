package com.example.electronicdiary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class EditStudentActivity extends AppCompatActivity {

    private DatabaseManager dbManager;
    private long studentId;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText recordBookNumberEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_edit_student);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        studentId = intent.getLongExtra("student_id", -1);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        firstNameEditText = findViewById(R.id.edit_text_first_name);
        lastNameEditText = findViewById(R.id.edit_text_last_name);
        recordBookNumberEditText = findViewById(R.id.edit_text_record_book_number);

        Cursor cursor = dbManager.fetchStudentById(studentId);
        if (cursor != null && cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_LAST_NAME));
            String recordBookNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.STUDENT_RECORD_BOOK_NUMBER));

            firstNameEditText.setText(firstName);
            lastNameEditText.setText(lastName);
            recordBookNumberEditText.setText(recordBookNumber);
            cursor.close();
        }

        Button saveButton = findViewById(R.id.button_save_student);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String recordBookNumber = recordBookNumberEditText.getText().toString();

                dbManager.updateStudent(studentId, firstName, lastName, recordBookNumber);
                Toast.makeText(EditStudentActivity.this, "Данные студента обновлены", Toast.LENGTH_SHORT).show();

                // Устанавливаем результат и завершаем активность
                setResult(RESULT_OK);
                finish();
            }
        });
        Button deleteButton = findViewById(R.id.button_delete_student);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.deleteStudent(studentId);
                Toast.makeText(EditStudentActivity.this, "Студент удален", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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
