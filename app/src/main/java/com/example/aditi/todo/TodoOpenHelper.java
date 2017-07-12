package com.example.aditi.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Aditi on 6/28/2017.
 */

public class TodoOpenHelper extends SQLiteOpenHelper {
    public final static String TODO_TABLE_NAME = "Todo";
    public final static String TODO_TITLE = "title";
    public final static String TODO_CATEGORY = "category";
    public final static String TODO_DESCRIPTION = "description";
    public final static String TODO_ID = "_id";
    public final static String TODO_DATE = "Date";
    public final static String TODO_DEADLINE = "deadline";
    public static TodoOpenHelper todoOpenHelper;

    public static TodoOpenHelper getTodoOpenHelperInstance(Context context)
    {
        if (todoOpenHelper == null)
            todoOpenHelper = new TodoOpenHelper(context);
        return todoOpenHelper;
    }

    private TodoOpenHelper(Context context) {
        super(context, "Todos.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table " + TODO_TABLE_NAME + " ( " + TODO_ID + " integer primary key autoincrement, " +
                TODO_TITLE + " text, " + TODO_CATEGORY + " text, " + TODO_DESCRIPTION + " text, " + TODO_DATE +
        " integer, " + TODO_DEADLINE + " integer );";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
