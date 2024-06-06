package com.example.electronicdiary;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class GroupListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ListView groupListView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DatabaseManager dbManager;
    private GroupListAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_group_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        groupListView = findViewById(R.id.group_list_view);

        dbManager = new DatabaseManager(this);
        dbManager.open();

        loadGroups();

        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GroupListActivity.this, StudentListActivity.class);
                intent.putExtra("group_id", id); // Передаем идентификатор группы
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadGroups() {
        Cursor cursor = dbManager.fetchGroups();
        if (cursor != null) {
            adapter = new GroupListAdapter(this, cursor);
            groupListView.setAdapter(adapter);
        }
    }

    @Override
    protected void onDestroy() {
        dbManager.close();
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(homeIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                break;
            case R.id.nav_add_discipline:
                Intent intent = new Intent(GroupListActivity.this, AddDisciplineActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.nav_add_student:
                Toast.makeText(this, "Добавить студента", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_add_group:
                Intent intentAddGroup = new Intent(GroupListActivity.this, AddGroupActivity.class);
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
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
        item.setChecked(true);
        navigationView.getMenu().findItem(R.id.nav_group_list).setChecked(true);
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
        dbManager.open(); // Открываем базу данных
        loadGroups(); // Выполняем запрос к базе данных
        dbManager.close(); // Закрываем базу данных после использования
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_group_list).setChecked(true);
    }

}
