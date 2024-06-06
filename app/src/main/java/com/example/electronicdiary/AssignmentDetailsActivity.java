package com.example.electronicdiary;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

public class AssignmentDetailsActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_EDIT_ASSIGNMENT = 1;
    private static final int REQUEST_CODE_ATTACH_FILE = 2;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 3;

    private TextView textViewTitle;
    private TextView textViewDescription;
    private Button buttonEditAssignment;
    private Button buttonAttachFile;
    private Button buttonAddGrade;
    private long assignmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_assignment_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Assignment Details");

        // Изменение цвета стрелки назад
        DrawableCompat.setTint(toolbar.getNavigationIcon(), ContextCompat.getColor(this, android.R.color.black));

        textViewTitle = findViewById(R.id.text_view_title);
        textViewDescription = findViewById(R.id.text_view_description);
        buttonEditAssignment = findViewById(R.id.button_edit_assignment);
        buttonAttachFile = findViewById(R.id.button_attach_file);
        buttonAddGrade = findViewById(R.id.button_add_grade);

        // Получение данных из Intent
        assignmentId = getIntent().getLongExtra("assignment_id", -1L);
        String title = getIntent().getStringExtra("assignment_title");
        String description = getIntent().getStringExtra("assignment_description");

        // Установка данных в TextView
        textViewTitle.setText(title);
        textViewDescription.setText(description);

        buttonEditAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AssignmentDetailsActivity.this, EditAssignmentActivity.class);
                intent.putExtra("assignment_id", assignmentId);
                intent.putExtra("assignment_title", textViewTitle.getText().toString());
                intent.putExtra("assignment_description", textViewDescription.getText().toString());
                startActivityForResult(intent, REQUEST_CODE_EDIT_ASSIGNMENT);
            }
        });

        buttonAttachFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
            }
        });

        buttonAddGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Реализуйте логику добавления оценки
            }
        });
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        } else {
            openFilePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                Toast.makeText(this, "Permission denied to read storage", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_ATTACH_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ATTACH_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                Toast.makeText(this, "File attached: " + uri.toString(), Toast.LENGTH_SHORT).show();
                buttonAddGrade.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == REQUEST_CODE_EDIT_ASSIGNMENT && resultCode == RESULT_OK) {
            if (data != null) {
                String newTitle = data.getStringExtra("assignment_title");
                String newDescription = data.getStringExtra("assignment_description");
                textViewTitle.setText(newTitle);
                textViewDescription.setText(newDescription);

                // Обновите данные в базе данных
                // dbManager.updateAssignment(assignmentId, newTitle, newDescription);
            }
        }
    }

    private void loadAssignmentDetails() {
        // Загрузка деталей задания из базы данных по assignmentId
        // Пример:
        // String title = dbManager.getAssignmentTitleById(assignmentId);
        // String description = dbManager.getAssignmentDescriptionById(assignmentId);
        // textViewTitle.setText(title);
        // textViewDescription.setText(description);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
