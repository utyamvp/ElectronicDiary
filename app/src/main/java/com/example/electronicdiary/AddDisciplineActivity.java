package com.example.electronicdiary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class AddDisciplineActivity extends AppCompatActivity {

    private EditText disciplineNameEditText;
    private Button addButton;
    private DatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_add_discipline);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        disciplineNameEditText = findViewById(R.id.discipline_name_edit_text);
        addButton = findViewById(R.id.add_discipline_button);

        databaseManager = new DatabaseManager(this);
        databaseManager.open();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String disciplineName = disciplineNameEditText.getText().toString().trim();
                if (!disciplineName.isEmpty()) {
                    long result = databaseManager.insertDiscipline(disciplineName);
                    if (result != -1) {
                        Toast.makeText(AddDisciplineActivity.this, "Дисциплина добавлена", Toast.LENGTH_SHORT).show();
                        // Возвращаем результат и закрываем Activity
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddDisciplineActivity.this, "Ошибка при добавлении дисциплины", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddDisciplineActivity.this, "Введите название дисциплины", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обработка нажатия на элемент в тулбаре
        switch (item.getItemId()) {
            case android.R.id.home: // Если нажата стрелка вверх (домик)
                onBackPressed(); // Вызываем метод onBackPressed(), чтобы вернуться на предыдущий экран
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        databaseManager.close();
        super.onDestroy();
    }
}
