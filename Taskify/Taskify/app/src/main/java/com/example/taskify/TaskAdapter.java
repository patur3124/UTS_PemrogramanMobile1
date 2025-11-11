package com.example.taskify;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.content.res.ResourcesCompat;
import android.widget.LinearLayout;
import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Task> taskList;
    private final OnTaskChangedListener listener;

    public interface OnTaskChangedListener {
        void onDataChanged();
    }

    public TaskAdapter(Context context, ArrayList<Task> taskList, OnTaskChangedListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = taskList.get(position);

        // lepas listener lama agar tidak dobel trigger
        holder.checkboxDone.setOnCheckedChangeListener(null);

        holder.textTask.setText(task.getText());
        holder.checkboxDone.setChecked(task.isDone());
        updateStrikeText(holder.textTask, task.isDone());

        // tandai / batal tandai selesai
        holder.checkboxDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setDone(isChecked);
            updateStrikeText(holder.textTask, isChecked);
            listener.onDataChanged();
        });

        // edit tugas
        holder.btnEdit.setOnClickListener(v -> {
            // Buat layout vertical
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            int paddingPx = (int) (16 * context.getResources().getDisplayMetrics().density);
            layout.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

            // TextInputLayout
            TextInputLayout inputLayout = new TextInputLayout(context);
            inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_FILLED);
            inputLayout.setBoxBackgroundColorResource(R.color.white); // bisa disesuaikan tema
            inputLayout.setBoxCornerRadii(12, 12, 12, 12);

            // TextInputEditText
            TextInputEditText editText = new TextInputEditText(context);
            editText.setText(task.getText());
            editText.setTextSize(16);
            editText.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            editText.setBackground(null);
            editText.setTypeface(ResourcesCompat.getFont(context, R.font.poppins_regular));

            inputLayout.addView(editText);
            layout.addView(inputLayout);

            // AlertDialog
            new AlertDialog.Builder(context)
                    .setTitle("Edit Tugas")
                    .setView(layout)
                    .setPositiveButton("Simpan", (dialog, which) -> {
                        String updatedText = editText.getText().toString().trim();
                        if (!updatedText.isEmpty()) {
                            task.setText(updatedText);
                            notifyItemChanged(position);
                            listener.onDataChanged();
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });


        // hapus tugas (klik lama pada item)
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Hapus Tugas")
                    .setMessage("Yakin ingin menghapus tugas ini?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        taskList.remove(position);
                        notifyItemRemoved(position);
                        listener.onDataChanged();
                    })
                    .setNegativeButton("Batal", null)
                    .show();
            return true;
        });
    }

    private void updateStrikeText(TextView textView, boolean isDone) {
        if (isDone) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkboxDone;
        TextView textTask;
        ImageButton btnEdit;

        public ViewHolder(View itemView) {
            super(itemView);
            checkboxDone = itemView.findViewById(R.id.checkboxDone);
            textTask = itemView.findViewById(R.id.textTask);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
