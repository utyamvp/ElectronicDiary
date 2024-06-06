package com.example.electronicdiary;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

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
    private static final int REQUEST_CODE_GRADE_ENTRY = 3;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 4;

    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewGrade;
    private TextView textViewComment;
    private DatabaseManager dbManager;
    private Button buttonEditAssignment;
    private Button buttonAttachFile;
    private Button buttonAddGrade;
    private long assignmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_assignment_details);

        dbManager = new DatabaseManager(this); // Инициализация dbManager
        dbManager.open();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Assignment Details");

        DrawableCompat.setTint(toolbar.getNavigationIcon(), ContextCompat.getColor(this, android.R.color.black));

        textViewTitle = findViewById(R.id.text_view_title);
        textViewDescription = findViewById(R.id.text_view_description);
        textViewGrade = findViewById(R.id.text_view_grade);
        textViewComment = findViewById(R.id.text_view_comment);
        buttonEditAssignment = findViewById(R.id.button_edit_assignment);
        buttonAttachFile = findViewById(R.id.button_attach_file);
        buttonAddGrade = findViewById(R.id.button_add_grade);

        assignmentId = getIntent().getLongExtra("assignment_id", -1L);
        String title = getIntent().getStringExtra("assignment_title");
        String description = getIntent().getStringExtra("assignment_description");

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
                Intent intent = new Intent(AssignmentDetailsActivity.this, GradeEntryActivity.class);
                intent.putExtra("assignment_id", assignmentId);
                startActivityForResult(intent, REQUEST_CODE_GRADE_ENTRY);
            }
        });

        loadAssignmentDetails();
    }


    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
        } else {
            openFilePicker();
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
        } else if (requestCode == REQUEST_CODE_GRADE_ENTRY && resultCode == RESULT_OK) {
            if (data != null) {
                String grade = data.getStringExtra("grade");
                String comment = data.getStringExtra("comment");

                textViewGrade.setText("Оценка: " + grade);
                textViewComment.setText("Комментарий: " + comment);
                dbManager.insertGrade(assignmentId, grade, comment);
            }
        }
    }

    private void loadAssignmentDetails() {
        // Загрузка деталей задания из базы данных по assignmentId
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }
}
