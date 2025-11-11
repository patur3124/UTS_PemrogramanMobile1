package com.example.taskify;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.content.res.ResourcesCompat;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Task> taskList;
    private TaskAdapter adapter;
    private SharedPreferences prefs;
    private static final String PREFS_NAME = "task_prefs";
    private static final String KEY_TASKS = "tasks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewTasks);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        taskList = loadTasks();

        adapter = new TaskAdapter(this, taskList, this::saveTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {

            // Buat layout vertical
            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            int paddingPx = (int) (16 * getResources().getDisplayMetrics().density);
            layout.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

            // TextInputLayout
            TextInputLayout inputLayout = new TextInputLayout(this);

            inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_FILLED);
            inputLayout.setBoxBackgroundColorResource(R.color.white); // bisa ganti sesuai tema
            inputLayout.setBoxCornerRadii(12, 12, 12, 12); // rounded corners

            // TextInputEditText
            TextInputEditText editText = new TextInputEditText(this);
            editText.setTextSize(16);
            editText.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
            editText.setBackground(null);
            editText.setTypeface(ResourcesCompat.getFont(this, R.font.poppins_regular));

            inputLayout.addView(editText);
            layout.addView(inputLayout);

            // Dialog
            new AlertDialog.Builder(this)
                    .setTitle("Tambah Tugas")
                    .setView(layout)
                    .setPositiveButton("Tambah", (dialog, which) -> {
                        String text = editText.getText().toString().trim();
                        if (!text.isEmpty()) {
                            taskList.add(new Task(text, false));
                            adapter.notifyItemInserted(taskList.size() - 1);
                            saveTasks();
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });
    }

    private void saveTasks() {
        JSONArray jsonArray = new JSONArray();
        for (Task task : taskList) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("text", task.getText());
                obj.put("done", task.isDone());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(obj);
        }
        prefs.edit().putString(KEY_TASKS, jsonArray.toString()).apply();
    }

    private ArrayList<Task> loadTasks() {
        ArrayList<Task> list = new ArrayList<>();
        String json = prefs.getString(KEY_TASKS, null);
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    list.add(new Task(obj.getString("text"), obj.getBoolean("done")));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
