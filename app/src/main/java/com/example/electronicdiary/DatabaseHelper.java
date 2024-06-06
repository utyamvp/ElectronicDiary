package com.example.electronicdiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "electronic_diary.db";
    public static final int DATABASE_VERSION = 4;

    // Таблица Groups
    public static final String TABLE_GROUPS = "Groups";
    public static final String GROUP_ID = "group_id";
    public static final String GROUP_NAME = "name";
    public static final String GROUP_DISCIPLINE_ID = "discipline_id";

    // Таблица Students
    public static final String TABLE_STUDENTS = "Students";
    public static final String STUDENT_ID = "student_id";
    public static final String STUDENT_FIRST_NAME = "first_name";
    public static final String STUDENT_LAST_NAME = "last_name";
    public static final String STUDENT_RECORD_BOOK_NUMBER = "record_book_number";
    public static final String STUDENT_GROUP_ID = "group_id";

    // Таблица Disciplines
    public static final String TABLE_DISCIPLINES = "Disciplines";
    public static final String DISCIPLINE_ID = "discipline_id";
    public static final String DISCIPLINE_NAME = "name";

    // Таблица Assignments
    public static final String TABLE_ASSIGNMENTS = "Assignments";
    public static final String ASSIGNMENT_ID = "assignment_id";
    public static final String ASSIGNMENT_TITLE = "title";
    public static final String ASSIGNMENT_DESCRIPTION = "description";
    public static final String ASSIGNMENT_DISCIPLINE_ID = "discipline_id";

    // Таблица Grades
    public static final String TABLE_GRADES = "Grades";
    public static final String GRADE_ID = "grade_id";
    public static final String GRADE_STUDENT_ID = "student_id";
    public static final String GRADE_ASSIGNMENT_ID = "assignment_id";
    public static final String GRADE_GRADE = "grade";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "Creating tables...");
        // Создание таблицы Groups
        String createGroupsTable = "CREATE TABLE " + TABLE_GROUPS + " ("
                + GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GROUP_NAME + " TEXT NOT NULL, "
                + GROUP_DISCIPLINE_ID + " INTEGER, "
                + "FOREIGN KEY (" + GROUP_DISCIPLINE_ID + ") REFERENCES " + TABLE_DISCIPLINES + "(" + DISCIPLINE_ID + "))";
        db.execSQL(createGroupsTable);

        // Создание таблицы Students
        String createStudentsTable = "CREATE TABLE " + TABLE_STUDENTS + " ("
                + STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STUDENT_FIRST_NAME + " TEXT NOT NULL, "
                + STUDENT_LAST_NAME + " TEXT NOT NULL, "
                + STUDENT_RECORD_BOOK_NUMBER + " TEXT NOT NULL, "
                + STUDENT_GROUP_ID + " INTEGER, "
                + "FOREIGN KEY (" + STUDENT_GROUP_ID + ") REFERENCES " + TABLE_GROUPS + "(" + GROUP_ID + "))";
        db.execSQL(createStudentsTable);

        // Создание таблицы Disciplines
        String createDisciplinesTable = "CREATE TABLE " + TABLE_DISCIPLINES + " ("
                + DISCIPLINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DISCIPLINE_NAME + " TEXT NOT NULL)";
        db.execSQL(createDisciplinesTable);

        // Создание таблицы Assignments
        String createAssignmentsTable = "CREATE TABLE " + TABLE_ASSIGNMENTS + " ("
                + ASSIGNMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ASSIGNMENT_TITLE + " TEXT NOT NULL, "
                + ASSIGNMENT_DESCRIPTION + " TEXT, "
                + ASSIGNMENT_DISCIPLINE_ID + " INTEGER, "
                + "FOREIGN KEY (" + ASSIGNMENT_DISCIPLINE_ID + ") REFERENCES " + TABLE_DISCIPLINES + "(" + DISCIPLINE_ID + "))";
        db.execSQL(createAssignmentsTable);

        // Создание таблицы Grades
        String createGradesTable = "CREATE TABLE " + TABLE_GRADES + " ("
                + GRADE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GRADE_STUDENT_ID + " INTEGER, "
                + GRADE_ASSIGNMENT_ID + " INTEGER, "
                + GRADE_GRADE + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + GRADE_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + STUDENT_ID + "), "
                + "FOREIGN KEY (" + GRADE_ASSIGNMENT_ID + ") REFERENCES " + TABLE_ASSIGNMENTS + "(" + ASSIGNMENT_ID + "))";
        db.execSQL(createGradesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE " + TABLE_STUDENTS + " ADD COLUMN " + STUDENT_FIRST_NAME + " TEXT NOT NULL DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_STUDENTS + " ADD COLUMN " + STUDENT_LAST_NAME + " TEXT NOT NULL DEFAULT ''");
            db.execSQL("ALTER TABLE " + TABLE_STUDENTS + " ADD COLUMN " + STUDENT_RECORD_BOOK_NUMBER + " TEXT NOT NULL DEFAULT ''");
        }
    }
}
