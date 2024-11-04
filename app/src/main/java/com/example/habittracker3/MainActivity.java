package com.example.habittracker3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.app.TimePickerDialog;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ArrayList<String> habitList;
    private ArrayAdapter<String> adapter;
    private EditText editTextHabitName;
    private EditText editTextHabitDescription;
    private EditText editTextHabitFrequency;
    private Button buttonAddHabit;
    private Button buttonSetTime;
    private TextView textViewSelectedTime;
    private String selectedTime = "";
    private CheckBox[] checkBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        habitList = new ArrayList<>();

        // Initialize UI elements
        editTextHabitName = findViewById(R.id.editTextHabitName);
        editTextHabitDescription = findViewById(R.id.editTextHabitDescription);
        editTextHabitFrequency = findViewById(R.id.editTextHabitFrequency);
        buttonAddHabit = findViewById(R.id.buttonAddHabit);
        buttonSetTime = findViewById(R.id.buttonSetTime);
        textViewSelectedTime = findViewById(R.id.textViewSelectedTime);
        ListView listViewHabits = findViewById(R.id.listViewHabits);

        // Initialize Checkboxes
        checkBoxes = new CheckBox[]{
                findViewById(R.id.checkBoxMonday),
                findViewById(R.id.checkBoxTuesday),
                findViewById(R.id.checkBoxWednesday),
                findViewById(R.id.checkBoxThursday),
                findViewById(R.id.checkBoxFriday),
                findViewById(R.id.checkBoxSaturday),
                findViewById(R.id.checkBoxSunday)
        };

        // Set up the adapter for the ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, habitList);
        listViewHabits.setAdapter(adapter);

        // Set up the button click listener for setting time
        buttonSetTime.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(MainActivity.this,
                    (view, hourOfDay, minute) -> {
                        selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                        textViewSelectedTime.setText(selectedTime);
                    }, 0, 0, true);
            timePicker.show();
        });

        // Set up the button click listener for adding habit
        buttonAddHabit.setOnClickListener(v -> {
            String habitName = editTextHabitName.getText().toString().trim();
            String habitDescription = editTextHabitDescription.getText().toString().trim();
            String frequencyStr = editTextHabitFrequency.getText().toString().trim();
            addHabit(habitName, habitDescription, frequencyStr, selectedTime);
            resetInputs();
        });

        // Set up the long click listener for deleting habit
        listViewHabits.setOnItemLongClickListener((parent, view, position, id) -> {
            String habitDetails = habitList.get(position);
            int habitId = extractHabitId(habitDetails);
            deleteHabit(habitId);
            return true;
        });

        // Load existing habits into the ListView
        loadHabits();
    }

    // Reset input fields after adding a habit
    private void resetInputs() {
        editTextHabitName.setText("");
        editTextHabitDescription.setText("");
        editTextHabitFrequency.setText("");
        textViewSelectedTime.setText("No time set");
        selectedTime = "";
        resetCheckBoxes();
    }

    // Reset all checkboxes
    private void resetCheckBoxes() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(false);
        }
    }

    private void addHabit(String name, String description, String frequencyStr, String time) {
        if (name.isEmpty() || description.isEmpty() || time.isEmpty() || frequencyStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields before adding a habit", Toast.LENGTH_SHORT).show();
            return;
        }

        int frequency = parseFrequency(frequencyStr);
        if (frequency <= 0) {
            Toast.makeText(this, "Please enter a valid positive number for frequency", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedDays = countSelectedDays();
        if (selectedDays == 0) {
            Toast.makeText(this, "Please select at least one day", Toast.LENGTH_SHORT).show();
            return;
        }

        if (frequency > selectedDays) {
            Toast.makeText(this, "Frequency cannot be greater than the number of selected days", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.addHabit(name, description, frequency, time);
        scheduleAllNotifications(name, description, time);
        loadHabits();
    }

    private int parseFrequency(String frequencyStr) {
        try {
            return Integer.parseInt(frequencyStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int countSelectedDays() {
        int selectedDays = 0;
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) selectedDays++;
        }
        return selectedDays;
    }

    private void scheduleAllNotifications(String name, String description, String time) {
        for (int i = 0; i < checkBoxes.length; i++) {
            if (checkBoxes[i].isChecked()) {
                scheduleNotification(this, name, description, time, i + 1); // 1-based index for days
            }
        }
    }

    private void loadHabits() {
        new AsyncTask<Void, Void, Cursor>() {
            @Override
            protected Cursor doInBackground(Void... voids) {
                return dbHelper.getAllHabits();
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                habitList.clear();
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_HABIT_ID));
                        String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HABIT_NAME));
                        String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HABIT_DESCRIPTION));
                        int frequency = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_HABIT_FREQUENCY));
                        String time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HABIT_TIME));
                        String habitDetails = "ID: " + id + "\nName: " + name + "\nDescription: " + description +
                                "\nFrequency: " + frequency + " times/week\nTime: " + time;
                        habitList.add(habitDetails);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    private void deleteHabit(int habitId) {
        dbHelper.deleteHabit(habitId);
        loadHabits();
    }

    private void scheduleNotification(Context context, String title, String content, String time, int dayOfWeek) {
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, dayOfWeek, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Adjust for setting the time to the next occurrence
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private int extractHabitId(String habitDetails) {
        String[] parts = habitDetails.split("\n");
        if (parts.length > 0) {
            String idPart = parts[0].split(": ")[1]; // Extracts ID from "ID: {id}"
            try {
                return Integer.parseInt(idPart);
            } catch (NumberFormatException e) {
                return -1; // Return -1 if parsing fails
            }
        }
        return -1; // Default if the format is unexpected
    }
}
