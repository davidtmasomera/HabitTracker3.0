package com.example.habittracker3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Author: David T Masomera
 *
 * DatabaseHelper class to manage SQLite database for Habit Tracker app.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "HabitTracker.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_HABITS = "habits";

    // Users Table Columns
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SURNAME = "surname";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_CELL_NUMBER = "cell_number";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Habits Table Columns
    public static final String COLUMN_HABIT_ID = "habit_id";
    public static final String COLUMN_HABIT_NAME = "habit_name";
    public static final String COLUMN_HABIT_DESCRIPTION = "habit_description";
    public static final String COLUMN_HABIT_FREQUENCY = "habit_frequency";
    public static final String COLUMN_HABIT_TIME = "habit_time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_SURNAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_CELL_NUMBER + " TEXT,"
                + COLUMN_USERNAME + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Habits table
        String CREATE_HABITS_TABLE = "CREATE TABLE " + TABLE_HABITS + "("
                + COLUMN_HABIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HABIT_NAME + " TEXT,"
                + COLUMN_HABIT_DESCRIPTION + " TEXT,"
                + COLUMN_HABIT_FREQUENCY + " INTEGER,"
                + COLUMN_HABIT_TIME + " TEXT"
                + ")";
        db.execSQL(CREATE_HABITS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed and create fresh ones
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS);
        onCreate(db);
    }

    // User Registration
    public boolean registerUser(String name, String surname, String email, String cellNumber, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SURNAME, surname);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_CELL_NUMBER, cellNumber);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1; // Returns true if inserted successfully
    }

    // User Login
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return result; // Returns true if the user exists
    }

    // Add a Habit
    public boolean addHabit(String name, String description, int frequency, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HABIT_NAME, name);
        values.put(COLUMN_HABIT_DESCRIPTION, description);
        values.put(COLUMN_HABIT_FREQUENCY, frequency);
        values.put(COLUMN_HABIT_TIME, time);

        long result = db.insert(TABLE_HABITS, null, values);
        db.close();
        return result != -1;
    }

    // Get All Habits
    public Cursor getAllHabits() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_HABITS, null, null, null, null, null, COLUMN_HABIT_ID + " DESC");
    }

    // Delete a Habit
    public void deleteHabit(int habitId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HABITS, COLUMN_HABIT_ID + "=?", new String[]{String.valueOf(habitId)});
        db.close();
    }

    // Utility function to close the database
    @Override
    public synchronized void close() {
        super.close();
    }
}
