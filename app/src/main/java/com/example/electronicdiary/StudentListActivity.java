package com.example.electronicdiary;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class StudentListActivity extends AppCompatActivity {

    private DatabaseManager dbManager;
    private long groupId;
    private StudentCursorAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_student_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        groupId = intent.getLongExtra("group_id", -1);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        ListView listView = findViewById(R.id.list_view_students);
        adapter = new StudentCursorAdapter(this, null);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(StudentListActivity.this, StudentDetailsActivity.class);
                detailIntent.putExtra("student_id", id);
                detailIntent.putExtra("group_id", groupId);
                startActivity(detailIntent);
            }
        });

        Button addStudentButton = findViewById(R.id.button_add_student);
        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentListActivity.this, AddStudentActivity.class);
                intent.putExtra("group_id", groupId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Открываем базу данных
        dbManager.open();
        // Обновляем курсор в адаптере после возвращения к активности
        Cursor cursor = dbManager.fetchStudentsByGroup(groupId);
        adapter.changeCursor(cursor);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
