package com.example.electronicdiary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DisciplineDetailsActivity extends AppCompatActivity {

    private static final int ADD_ASSIGNMENT_REQUEST = 1;
    private DatabaseManager dbManager;
    private long disciplineId;
    private TextView disciplineNameTextView;
    private AssignmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_discipline_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Discipline Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        disciplineId = getIntent().getLongExtra("discipline_id", -1L);
        Log.d("DisciplineDetailsActivity", "Received Discipline ID: " + disciplineId);

        if (disciplineId == -1) {
            Log.e("DisciplineDetailsActivity", "Invalid discipline ID passed in intent");
            finish();
            return;
        }

        dbManager = new DatabaseManager(this);
        dbManager.open();

        disciplineNameTextView = findViewById(R.id.discipline_name_text_view);
        updateDisciplineName();

        RecyclerView recyclerView = findViewById(R.id.assignment_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AssignmentAdapter(this, null);
        adapter.setOnItemClickListener(new AssignmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(long assignmentId) {
                // Получение данных о задании из базы данных
                String title = dbManager.getAssignmentTitleById(assignmentId);
                String description = dbManager.getAssignmentDescriptionById(assignmentId);

                // Передача данных в AssignmentDetailsActivity
                Intent intent = new Intent(DisciplineDetailsActivity.this, AssignmentDetailsActivity.class);
                intent.putExtra("assignment_id", assignmentId);
                intent.putExtra("assignment_title", title);
                intent.putExtra("assignment_description", description);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);

        Button addAssignmentButton = findViewById(R.id.button_add_assignment);
        Button addGroupButton = findViewById(R.id.button_add_group);

        addAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisciplineDetailsActivity.this, AddAssignmentActivity.class);
                intent.putExtra("discipline_id", disciplineId);
                startActivityForResult(intent, ADD_ASSIGNMENT_REQUEST);
            }
        });

        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupSelectionDialogFragment dialogFragment = new GroupSelectionDialogFragment();
                dialogFragment.setDisciplineId(disciplineId);
                dialogFragment.show(getSupportFragmentManager(), "GroupSelectionDialogFragment");
            }
        });

        loadAssignments();
    }

    private void loadAssignments() {
        Cursor cursor = dbManager.fetchAssignmentsByDiscipline(disciplineId);
        adapter.swapCursor(cursor);
    }

    private void updateDisciplineName() {
        String disciplineName = dbManager.getDisciplineNameById(disciplineId);
        if (disciplineName != null) {
            disciplineNameTextView.setText(disciplineName);
        } else {
            disciplineNameTextView.setText("Название дисциплины не найдено");
            Log.e("DisciplineDetailsActivity", "Discipline with ID " + disciplineId + " does not exist in the database.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ASSIGNMENT_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra("title");
            String description = data.getStringExtra("description");

            dbManager.insertAssignment(title, description, (int) disciplineId);
            loadAssignments();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadAssignments(); // Обновить данные из базы данных
    }

    private void loadAssignmentDetails() {
        // Загрузка обновленных данных задания из базы данных и обновление UI
        // String title = dbManager.getAssignmentTitleById(assignmentId);
        // String description = dbManager.getAssignmentDescriptionById(assignmentId);
        // textViewAssignmentTitle.setText(title);
        // textViewAssignmentDescription.setText(description);
    }

    @Override
    protected void onDestroy() {
        if (dbManager != null) {
            dbManager.close();
        }
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

