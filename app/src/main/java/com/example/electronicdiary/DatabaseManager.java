package com.example.electronicdiary;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseManager {

    private DatabaseHelper dbHelper;
    private static SQLiteDatabase database;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    private boolean isOpen = false;

    public void open() throws SQLException {
        if (!isOpen) {
            database = dbHelper.getWritableDatabase();
            Log.d("DatabaseManager", "Database opened.");
            isOpen = true;
        }
    }

    public void close() {
        if (isOpen) {
            dbHelper.close();
            Log.d("DatabaseManager", "Database closed.");
            isOpen = false;
        }
    }

    public boolean isOpen() {
        return isOpen;
    }
    public void removeNameColumn() {
        database.beginTransaction();
        try {
            // Создание временной таблицы без столбца name
            database.execSQL("CREATE TABLE IF NOT EXISTS Students_temp (" +
                    "student_id INTEGER PRIMARY KEY, " +
                    "group_id INTEGER, " +
                    "first_name TEXT NOT NULL DEFAULT '', " +
                    "last_name TEXT NOT NULL DEFAULT '', " +
                    "record_book_number TEXT NOT NULL DEFAULT '')");

            // Копирование данных из старой таблицы в новую таблицу
            database.execSQL("INSERT INTO Students_temp (student_id, group_id, first_name, last_name, record_book_number) " +
                    "SELECT student_id, group_id, first_name, last_name, record_book_number FROM Students");

            // Удаление старой таблицы
            database.execSQL("DROP TABLE Students");

            // Переименование временной таблицы в оригинальное имя
            database.execSQL("ALTER TABLE Students_temp RENAME TO Students");

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public long insertStudent(String firstName, String lastName, String recordBookNumber, long groupId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.STUDENT_FIRST_NAME, firstName);
        contentValues.put(DatabaseHelper.STUDENT_LAST_NAME, lastName);
        contentValues.put(DatabaseHelper.STUDENT_RECORD_BOOK_NUMBER, recordBookNumber);
        contentValues.put(DatabaseHelper.STUDENT_GROUP_ID, groupId);

        Log.d("DatabaseManager", "Inserting student: " + contentValues.toString());
        return database.insert(DatabaseHelper.TABLE_STUDENTS, null, contentValues);
    }

    public Cursor fetchStudentsByGroup(long groupId) {
        String[] columns = {
                DatabaseHelper.STUDENT_ID + " AS _id",
                DatabaseHelper.STUDENT_FIRST_NAME,
                DatabaseHelper.STUDENT_LAST_NAME,
                DatabaseHelper.STUDENT_RECORD_BOOK_NUMBER
        };
        String selection = DatabaseHelper.STUDENT_GROUP_ID + "=?";
        String[] selectionArgs = {String.valueOf(groupId)};
        return database.query(DatabaseHelper.TABLE_STUDENTS, columns, selection, selectionArgs, null, null, null);
    }

    public Cursor fetchStudentById(long studentId) {
        String[] columns = {
                DatabaseHelper.STUDENT_FIRST_NAME,
                DatabaseHelper.STUDENT_LAST_NAME,
                DatabaseHelper.STUDENT_RECORD_BOOK_NUMBER
        };
        String selection = DatabaseHelper.STUDENT_ID + "=?";
        String[] selectionArgs = {String.valueOf(studentId)};
        return database.query(DatabaseHelper.TABLE_STUDENTS, columns, selection, selectionArgs, null, null, null);
    }

    public void updateStudent(long studentId, String firstName, String lastName, String recordBookNumber) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.STUDENT_FIRST_NAME, firstName);
        contentValues.put(DatabaseHelper.STUDENT_LAST_NAME, lastName);
        contentValues.put(DatabaseHelper.STUDENT_RECORD_BOOK_NUMBER, recordBookNumber);

        String whereClause = DatabaseHelper.STUDENT_ID + "=?";
        String[] whereArgs = {String.valueOf(studentId)};

        database.update(DatabaseHelper.TABLE_STUDENTS, contentValues, whereClause, whereArgs);
    }

    public void deleteStudent(long studentId) {
        String whereClause = DatabaseHelper.STUDENT_ID + "=?";
        String[] whereArgs = {String.valueOf(studentId)};
        database.delete(DatabaseHelper.TABLE_STUDENTS, whereClause, whereArgs);
    }


    public long insertDiscipline(String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.DISCIPLINE_NAME, name);
        return database.insert(DatabaseHelper.TABLE_DISCIPLINES, null, contentValues);
    }

    public long insertAssignment(String title, String description, int disciplineId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ASSIGNMENT_TITLE, title);
        values.put(DatabaseHelper.ASSIGNMENT_DESCRIPTION, description);
        values.put(DatabaseHelper.ASSIGNMENT_DISCIPLINE_ID, disciplineId);
        return database.insert(DatabaseHelper.TABLE_ASSIGNMENTS, null, values);
    }

    public Cursor fetchDisciplines() {
        String[] columns = {
                DatabaseHelper.DISCIPLINE_ID,
                DatabaseHelper.DISCIPLINE_NAME
        };
        return database.query(DatabaseHelper.TABLE_DISCIPLINES, columns, null, null, null, null, null);
    }

    public int updateDiscipline(long id, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.DISCIPLINE_NAME, name);
        return database.update(DatabaseHelper.TABLE_DISCIPLINES, contentValues, DatabaseHelper.DISCIPLINE_ID + " = " + id, null);
    }

    public void deleteDiscipline(long id) {
        database.delete(DatabaseHelper.TABLE_DISCIPLINES, DatabaseHelper.DISCIPLINE_ID + "=" + id, null);
    }

    public long insertGroup(String groupName, long disciplineId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.GROUP_NAME, groupName);
        if (disciplineId != -1) {
            values.put(DatabaseHelper.GROUP_DISCIPLINE_ID, disciplineId);
        }
        return database.insert(DatabaseHelper.TABLE_GROUPS, null, values);
    }


    @SuppressLint("Range")
    public Cursor fetchAssignmentsByDiscipline(long disciplineId) {
        String query = "SELECT " + DatabaseHelper.ASSIGNMENT_ID + " AS _id, " +
                DatabaseHelper.ASSIGNMENT_TITLE + ", " +
                DatabaseHelper.ASSIGNMENT_DESCRIPTION +
                " FROM " + DatabaseHelper.TABLE_ASSIGNMENTS +
                " WHERE " + DatabaseHelper.ASSIGNMENT_DISCIPLINE_ID + "=?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(disciplineId)});
        if (cursor != null) {
            Log.d("DatabaseManager", "Assignments loaded: " + cursor.getCount());
        } else {
            Log.e("DatabaseManager", "Failed to load assignments for disciplineId: " + disciplineId);
        }
        return cursor;
    }
    @SuppressLint("Range")
    public String getAssignmentTitleById(long assignmentId) {
        String[] columns = {DatabaseHelper.ASSIGNMENT_TITLE};
        String selection = DatabaseHelper.ASSIGNMENT_ID + "=?";
        String[] selectionArgs = {String.valueOf(assignmentId)};
        Cursor cursor = database.query(DatabaseHelper.TABLE_ASSIGNMENTS, columns, selection, selectionArgs, null, null, null);
        String assignmentTitle = null;
        if (cursor != null && cursor.moveToFirst()) {
            assignmentTitle = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ASSIGNMENT_TITLE));
            cursor.close();
        } else {
            Log.e("DatabaseManager", "Assignment ID: " + assignmentId + " not found.");
        }
        Log.d("DatabaseManager", "Assignment ID: " + assignmentId + ", Title: " + assignmentTitle);
        return assignmentTitle;
    }

    @SuppressLint("Range")
    public String getAssignmentDescriptionById(long assignmentId) {
        String[] columns = {DatabaseHelper.ASSIGNMENT_DESCRIPTION};
        String selection = DatabaseHelper.ASSIGNMENT_ID + "=?";
        String[] selectionArgs = {String.valueOf(assignmentId)};
        Cursor cursor = database.query(DatabaseHelper.TABLE_ASSIGNMENTS, columns, selection, selectionArgs, null, null, null);
        String assignmentDescription = null;
        if (cursor != null && cursor.moveToFirst()) {
            assignmentDescription = cursor.getString(cursor.getColumnIndex(DatabaseHelper.ASSIGNMENT_DESCRIPTION));
            cursor.close();
        } else {
            Log.e("DatabaseManager", "Assignment ID: " + assignmentId + " not found.");
        }
        Log.d("DatabaseManager", "Assignment ID: " + assignmentId + ", Description: " + assignmentDescription);
        return assignmentDescription;
    }

    public int updateAssignment(long assignmentId, String title, String description) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ASSIGNMENT_TITLE, title);
        values.put(DatabaseHelper.ASSIGNMENT_DESCRIPTION, description);
        String whereClause = DatabaseHelper.ASSIGNMENT_ID + "=?";
        String[] whereArgs = {String.valueOf(assignmentId)};
        return database.update(DatabaseHelper.TABLE_ASSIGNMENTS, values, whereClause, whereArgs);
    }

    public int insertGroupToDiscipline(long groupId, long disciplineId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.GROUP_DISCIPLINE_ID, disciplineId);
        String whereClause = DatabaseHelper.GROUP_ID + "=?";
        String[] whereArgs = {String.valueOf(groupId)};
        return database.update(DatabaseHelper.TABLE_GROUPS, values, whereClause, whereArgs);
    }

    @SuppressLint("Range")
    public String getDisciplineNameById(long disciplineId) {
        String[] columns = {DatabaseHelper.DISCIPLINE_NAME};
        String selection = DatabaseHelper.DISCIPLINE_ID + "=?";
        String[] selectionArgs = {String.valueOf(disciplineId)};
        Cursor cursor = database.query(DatabaseHelper.TABLE_DISCIPLINES, columns, selection, selectionArgs, null, null, null);
        String disciplineName = null;
        if (cursor != null && cursor.moveToFirst()) {
            disciplineName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DISCIPLINE_NAME));
            cursor.close();
        } else {
            Log.e("DatabaseManager", "Discipline ID: " + disciplineId + " not found.");
        }
        Log.d("DatabaseManager", "Discipline ID: " + disciplineId + ", Name: " + disciplineName);
        return disciplineName;
    }




    public void removeStudentFromGroup(long studentId) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.STUDENT_GROUP_ID, (Long) null);
        String whereClause = DatabaseHelper.STUDENT_ID + "=?";
        String[] whereArgs = {String.valueOf(studentId)};
        database.update(DatabaseHelper.TABLE_STUDENTS, values, whereClause, whereArgs);
    }

    public Cursor fetchGroups() {
        String[] columns = new String[]{DatabaseHelper.GROUP_ID + " AS _id", DatabaseHelper.GROUP_NAME};
        return database.query(DatabaseHelper.TABLE_GROUPS, columns, null, null, null, null, null);
    }

    public void removeGroupFromDiscipline(long groupId, long disciplineId) {
        SQLiteDatabase db = database;

        // Удаляем запись из таблицы Groups
        ContentValues values = new ContentValues();
        values.putNull(DatabaseHelper.GROUP_DISCIPLINE_ID); // Устанавливаем GROUP_DISCIPLINE_ID в NULL
        String whereClause = DatabaseHelper.GROUP_ID + "=?";
        String[] whereArgs = {String.valueOf(groupId)};
        int rowsAffected = db.update(DatabaseHelper.TABLE_GROUPS, values, whereClause, whereArgs);

        if (rowsAffected > 0) {
            Log.d("DatabaseManager", "Group removed from discipline: " + groupId);
        } else {
            Log.e("DatabaseManager", "Failed to remove group from discipline: " + groupId);
        }
    }



    public static boolean isGroupAssignedToDiscipline(long groupId, long disciplineId) {
        String[] columns = {DatabaseHelper.GROUP_ID};
        String selection = DatabaseHelper.GROUP_ID + "=? AND " + DatabaseHelper.GROUP_DISCIPLINE_ID + "=?";
        String[] selectionArgs = {String.valueOf(groupId), String.valueOf(disciplineId)};
        Cursor cursor = database.query(DatabaseHelper.TABLE_GROUPS, columns, selection, selectionArgs, null, null, null);
        boolean assigned = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return assigned;
    }
    public Cursor fetchGroupsByDiscipline(long disciplineId) {
        String[] columns = {
                DatabaseHelper.GROUP_ID,
                DatabaseHelper.GROUP_NAME
        };
        String selection = DatabaseHelper.GROUP_DISCIPLINE_ID + "=?";
        String[] selectionArgs = {String.valueOf(disciplineId)};
        return database.query(DatabaseHelper.TABLE_GROUPS, columns, selection, selectionArgs, null, null, null);
    }
    public Cursor getTableInfo(String tableName) {
        String query = "PRAGMA table_info(" + tableName + ")";
        return database.rawQuery(query, null);
    }


    // Дополнительные методы для работы с другими таблицами
}
