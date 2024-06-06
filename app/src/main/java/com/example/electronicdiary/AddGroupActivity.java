package com.example.electronicdiary;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class AddGroupActivity extends AppCompatActivity {

    private EditText groupNameEditText;
    private Button addButton;
    private DatabaseManager databaseManager;
    private long disciplineId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_add_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        groupNameEditText = findViewById(R.id.group_name_edit_text);
        addButton = findViewById(R.id.add_group_button);

        databaseManager = new DatabaseManager(this);
        databaseManager.open();

        disciplineId = getIntent().getLongExtra("discipline_id", -1);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = groupNameEditText.getText().toString().trim();
                if (!groupName.isEmpty()) {
                    long result = databaseManager.insertGroup(groupName, disciplineId);
                    if (result != -1) {
                        Toast.makeText(AddGroupActivity.this, "Группа добавлена", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AddGroupActivity.this, "Ошибка при добавлении группы", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddGroupActivity.this, "Введите название группы", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        databaseManager.close();
        super.onDestroy();
    }
}
