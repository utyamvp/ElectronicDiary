package com.example.electronicdiary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    private Context context;
    private Cursor cursor;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(long assignmentId);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public AssignmentAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public AssignmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AssignmentViewHolder holder, int position) {
        if (cursor.moveToPosition(position)) {
            int titleIndex = cursor.getColumnIndex(DatabaseHelper.ASSIGNMENT_TITLE);
            int descriptionIndex = cursor.getColumnIndex(DatabaseHelper.ASSIGNMENT_DESCRIPTION);
            int idIndex = cursor.getColumnIndex("_id");

            if (titleIndex != -1 && descriptionIndex != -1 && idIndex != -1) {
                String title = cursor.getString(titleIndex);
                String description = cursor.getString(descriptionIndex);
                long id = cursor.getLong(idIndex);

                Log.d("AssignmentAdapter", "Binding assignment: " + title + " with ID: " + id);

                holder.titleTextView.setText(title);
                holder.descriptionTextView.setText(description);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
                            listener.onItemClick(id);
                        }
                    }
                });
            } else {
                Log.e("AssignmentAdapter", "Invalid column index");
            }
        }
    }

    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    class AssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        AssignmentViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(android.R.id.text1);
            descriptionTextView = itemView.findViewById(android.R.id.text2);
        }
    }
}