package com.example.aditi.todo;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TodoDetailActivity extends AppCompatActivity {

    EditText titleText;
    EditText categoryText;
    EditText descriptionText;
    TextView deadlineText;
    TextView dateText;
    TodoOpenHelper todoOpenHelper;
    SQLiteDatabase database;
    String Position;

    long deadlineTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);

        titleText = (EditText) findViewById(R.id.titleText);
        categoryText = (EditText) findViewById(R.id.categoryText);
        descriptionText = (EditText) findViewById(R.id.descriptionText);
        dateText = (TextView) findViewById(R.id.dateText);
        deadlineText = (TextView) findViewById(R.id.deadlineText);
        deadlineText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar newCalendar = Calendar.getInstance();
                if (deadlineTime != 0)
                {
                    newCalendar.setTimeInMillis(deadlineTime);
                }
                int month = newCalendar.get(Calendar.MONTH);  // Current month
                int year = newCalendar.get(Calendar.YEAR);   // Current year
                int day = newCalendar.get(Calendar.DAY_OF_MONTH);
                showDatePicker(TodoDetailActivity.this, year, month, day);
            }
        });
        Button OKButton = (Button) findViewById(R.id.OKbutton);

        Intent intent = getIntent();
        final int idString = intent.getIntExtra(IntentConstants.ID, -1);
        Position = intent.getStringExtra(IntentConstants.POSITION);
        todoOpenHelper = TodoOpenHelper.getTodoOpenHelperInstance(TodoDetailActivity.this);
        database = todoOpenHelper.getWritableDatabase();

        if (idString != -1)
        {
            fillEditTextViews(idString);
        }

        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleText.getText().toString();
                String category = categoryText.getText().toString();
                String description = descriptionText.getText().toString();

                if (title.trim().isEmpty())
                {
                    titleText.setError("Title cannot be empty");
                    return;
                }

                if (category.trim().isEmpty())
                {
                    categoryText.setError("Category cannot be empty");
                    return;
                }

                ContentValues cv = new ContentValues();
                cv.put(TodoOpenHelper.TODO_TITLE, title);
                cv.put(TodoOpenHelper.TODO_CATEGORY, category);
                cv.put(TodoOpenHelper.TODO_DESCRIPTION, description);
                long date = System.currentTimeMillis();
                cv.put(TodoOpenHelper.TODO_DATE, date);
                cv.put(TodoOpenHelper.TODO_DEADLINE, deadlineTime);

                if (idString == -1) // add new
                {
                    insertTodo(cv);
                }
                else    // update existing todo
                {
                    updateTodo(cv, idString);
                }
                TodoDetailActivity.this.finish();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void showDatePicker(Context context, int initialYear, int initialMonth, int initialDay) {

        // Creating datePicker dialog object
        // It requires context and listener that is used when a date is selected by the user.

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    //This method is called when the user has finished selecting a date.
                    // Arguments passed are selected year, month and day
                    @Override
                    public void onDateSet(DatePicker datepicker, int year, int month, int day) {

                        // To get epoch, You can store this date(in epoch) in database
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day);
                        deadlineTime = calendar.getTime().getTime();
                        // Setting date selected in the edit text
                        deadlineText.setText("Deadline is " + day + "/" + (month + 1) + "/" + year);
                    }
                }, initialYear, initialMonth, initialDay);

        //Call show() to simply show the dialog
        datePickerDialog.show();

    }

    private void updateTodo(ContentValues cv, int idString) {
        database.update(TodoOpenHelper.TODO_TABLE_NAME, cv, TodoOpenHelper.TODO_ID + " = " + idString, null);
        Intent i = new Intent();
        i.putExtra(IntentConstants.ID, idString);
        i.putExtra(IntentConstants.POSITION, Position);
        setResult(RESULT_OK, i);

    }

    private void fillEditTextViews(int id) {
        Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE_NAME, null, TodoOpenHelper.TODO_ID + " = " + id
                , null, null, null,null);
        /*Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext())
        {
            if (cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_ID)) == id)
            {
                break;
            }
        }*/
        cursor.moveToNext();
        titleText.setText(cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TITLE)));
        categoryText.setText(cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY)));
        descriptionText.setText(cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_DESCRIPTION)));
        long epochTime = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DATE));
        Date date = new Date(epochTime);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        format.getTimeZone();
        String text = format.format(date);
        dateText.setText("Last Modified: " + text);
        deadlineTime = cursor.getLong(cursor.getColumnIndex(TodoOpenHelper.TODO_DEADLINE));
        if (deadlineTime != 0)
        {
            date = new Date(deadlineTime);
            text = format.format(date);
            deadlineText.setText("Deadline is " + text);
        }
        cursor.close();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    public void insertTodo(ContentValues cv)
    {
        database.insert(TodoOpenHelper.TODO_TABLE_NAME, null, cv);
        setResult(RESULT_OK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.set_alarm)
        {

        }
        return true;
    }
}
