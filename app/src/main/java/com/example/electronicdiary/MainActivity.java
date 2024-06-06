package com.example.electronicdiary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_ADD_DISCIPLINE = 1;

    private DrawerLayout drawerLayout; // Сайдбар
    private ActionBarDrawerToggle actionBarDrawerToggle; // Переключатель для сайдбара
    private DatabaseManager databaseManager; // Менеджер базы данных
    private ListView disciplineListView; // Список дисциплин
    private ArrayAdapter<String> adapter; // Адаптер для списка дисциплин
    private ArrayList<String> disciplines; // Список дисциплин
    private ArrayList<Integer> disciplineIds; // ID дисциплин

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar); // Находим тулбар
        setSupportActionBar(toolbar); // Устанавливаем тулбар в качестве action bar

        drawerLayout = findViewById(R.id.drawer_layout); // Находим сайдбар
        NavigationView navigationView = findViewById(R.id.nav_view); // Находим меню в сайдбаре
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close); // Создаем переключатель для сайдбара
        drawerLayout.addDrawerListener(actionBarDrawerToggle); // Устанавливаем слушатель на события открытия и закрытия сайдбара
        actionBarDrawerToggle.syncState(); // Синхронизируем состояние переключателя с состоянием сайдбара
        navigationView.setNavigationItemSelectedListener(this); // Устанавливаем слушатель на события выбора элементов меню

        disciplineListView = findViewById(R.id.discipline_list_view);
        disciplines = new ArrayList<>();
        disciplineIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, disciplines);
        disciplineListView.setAdapter(adapter);

        databaseManager = new DatabaseManager(this);
        databaseManager.open();

        loadDisciplines();

        disciplineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int disciplineId = disciplineIds.get(position);
                Intent intent = new Intent(MainActivity.this, DisciplineDetailsActivity.class);
                intent.putExtra("discipline_id", (long) disciplineId);
                startActivity(intent);
            }
        });
    }

    public void loadDisciplines() {
        disciplines.clear();
        disciplineIds.clear();
        Cursor cursor = databaseManager.fetchDisciplines();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String disciplineName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISCIPLINE_NAME));
                @SuppressLint("Range") int disciplineId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.DISCIPLINE_ID));
                disciplines.add(disciplineName);
                disciplineIds.add(disciplineId);
                Log.d("MainActivity", "Discipline loaded: " + disciplineName);
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        switch (item.getItemId()) {
            case R.id.nav_add_discipline:
                Intent intent = new Intent(MainActivity.this, AddDisciplineActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_DISCIPLINE);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.nav_add_student:
                Toast.makeText(this, "Добавить студента", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_add_group:
                Intent intentAddGroup = new Intent(MainActivity.this, AddGroupActivity.class);
                startActivity(intentAddGroup);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.nav_add_assignment:
                Toast.makeText(this, "Добавить задание", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_add_grade:
                Toast.makeText(this, "Добавить оценку", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_group_list:
                Intent groupListIntent = new Intent(MainActivity.this, GroupListActivity.class);
                startActivity(groupListIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
        item.setChecked(true);
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!databaseManager.isOpen()) {
            databaseManager.open();
            loadDisciplines();
        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
    }

    @Override
    protected void onDestroy() {
        databaseManager.close();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_DISCIPLINE && resultCode == RESULT_OK) {
            loadDisciplines(); // Обновляем список дисциплин после добавления новой
        }
    }
}