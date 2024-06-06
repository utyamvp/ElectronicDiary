package com.example.electronicdiary;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

public class GroupSelectionDialogFragment extends DialogFragment {

    private DatabaseManager dbManager;
    private long disciplineId;
    private GroupCursorAdapter adapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_group_selection, null);
        builder.setView(view)
                .setTitle("Выберите группу")
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        dbManager = new DatabaseManager(requireContext());
        dbManager.open();

        Cursor cursor = dbManager.fetchGroups();
        adapter = new GroupCursorAdapter(requireContext(), cursor, disciplineId, dbManager);

        ListView listView = view.findViewById(R.id.list_view_groups);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DatabaseManager.isGroupAssignedToDiscipline(id, disciplineId)) {
                    dbManager.removeGroupFromDiscipline(id, disciplineId);
                } else {
                    dbManager.insertGroupToDiscipline(id, disciplineId);
                }
                adapter.notifyDataSetChanged();
            }
        });

        return builder.create();
    }

    public void setDisciplineId(long disciplineId) {
        this.disciplineId = disciplineId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
