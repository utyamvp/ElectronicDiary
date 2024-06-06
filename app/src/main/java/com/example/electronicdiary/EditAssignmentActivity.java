package com.example.electronicdiary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class EditAssignmentActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDescription;
    private DatabaseManager dbManager;
    private Button buttonSaveChanges;
    private long assignmentId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_edit_assignment);

        // Инициализация dbManager
        dbManager = new DatabaseManager(this);
        dbManager.open(); // Открываем базу данных

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Assignment");

        // Изменение цвета стрелки назад
        DrawableCompat.setTint(toolbar.getNavigationIcon(), ContextCompat.getColor(this, android.R.color.black));

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        buttonSaveChanges = findViewById(R.id.button_save_changes);

        assignmentId = getIntent().getLongExtra("assignment_id", -1L);
        String title = getIntent().getStringExtra("assignment_title");
        String description = getIntent().getStringExtra("assignment_description");

        editTextTitle.setText(title);
        editTextDescription.setText(description);

        // Устанавливаем текст по умолчанию
        editTextTitle.setHint("Название задания");
        editTextDescription.setHint("Описание задания");

        // Очищаем hint при редактировании
        editTextTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextTitle.setHint("");
                } else {
                    if (editTextTitle.getText().toString().trim().isEmpty()) {
                        editTextTitle.setHint("Название задания");
                    }
                }
            }
        });

        editTextDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editTextDescription.setHint("");
                } else {
                    if (editTextDescription.getText().toString().trim().isEmpty()) {
                        editTextDescription.setHint("Описание задания");
                    }
                }
            }
        });

        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTitle = editTextTitle.getText().toString().trim();
                String newDescription = editTextDescription.getText().toString().trim();

                // Сохранение изменений в базе данных
                long assignmentId = getIntent().getLongExtra("assignment_id", -1L);
                if (assignmentId != -1) {
                    dbManager.updateAssignment(assignmentId, newTitle, newDescription);
                }

                // Возвращение измененных данных в предыдущую активность
                Intent resultIntent = new Intent();
                resultIntent.putExtra("assignment_title", newTitle);
                resultIntent.putExtra("assignment_description", newDescription);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
