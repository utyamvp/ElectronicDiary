package com.example.electronicdiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class GradeEntryActivity extends AppCompatActivity {

    private EditText editTextGrade;
    private EditText editTextComment;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_entry);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Добавить оценку");

        editTextGrade = findViewById(R.id.edit_text_grade);
        editTextComment = findViewById(R.id.edit_text_comment);
        buttonSave = findViewById(R.id.button_save);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String grade = editTextGrade.getText().toString();
                String comment = editTextComment.getText().toString();

                if (!grade.isEmpty() && !comment.isEmpty()) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("grade", grade);
                    resultIntent.putExtra("comment", comment);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    // Покажите сообщение об ошибке, если поля пустые
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
