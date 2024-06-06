package com.example.electronicdiary;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupCursorAdapter extends CursorAdapter {

    private long disciplineId;
    private DatabaseManager dbManager;

    public GroupCursorAdapter(Context context, Cursor cursor, long disciplineId) {
        super(context, cursor, 0);
        this.disciplineId = disciplineId;
        this.dbManager = new DatabaseManager(context); // Создаем экземпляр DatabaseManager
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_group2, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = view.findViewById(R.id.text_group_name);
        ImageView checkImageView = view.findViewById(R.id.image_check);

        String groupName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        textView.setText(groupName);

        long groupId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        boolean isGroupAssigned = dbManager.isGroupAssignedToDiscipline(groupId, disciplineId);

        // Устанавливаем видимость галочки в зависимости от статуса приписки группы
        checkImageView.setVisibility(isGroupAssigned ? View.VISIBLE : View.GONE);

        // Устанавливаем обработчик нажатия на элемент списка
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Проверяем текущее состояние группы
                if (isGroupAssigned) {
                    // Если группа уже приписана к дисциплине, удаляем привязку
                    dbManager.removeGroupFromDiscipline(groupId, disciplineId);
                    checkImageView.setVisibility(View.GONE); // Скрываем галочку
                } else {
                    // Если группа не приписана к дисциплине, добавляем привязку
                    dbManager.insertGroupToDiscipline(groupId, disciplineId);
                    checkImageView.setVisibility(View.VISIBLE); // Отображаем галочку
                }
            }
        });
    }

}
