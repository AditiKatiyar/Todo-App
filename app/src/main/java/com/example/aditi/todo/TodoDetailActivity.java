package com.example.aditi.todo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class TodoDetailActivity extends AppCompatActivity {

    EditText titleText;
    EditText categoryText;
    EditText descriptionText;
    TextView deadlineText;
    TextView dateText;
    TextView statusText;
    ImageView imageView;
    TextView header;
    TodoOpenHelper todoOpenHelper;
    SQLiteDatabase database;
    String Position;
    Calendar mCalendar;
    long deadlineTime = 0;
    long uniqueId = 0;
    int deadlinePassed = 0;
    int done = 0;
    boolean alarmSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);

        titleText = (EditText) findViewById(R.id.titleText);
        categoryText = (EditText) findViewById(R.id.categoryText);
        descriptionText = (EditText) findViewById(R.id.descriptionText);
        dateText = (TextView) findViewById(R.id.dateText);
        deadlineText = (TextView) findViewById(R.id.deadlineText);
        imageView = (ImageView) findViewById(R.id.image);
        statusText = (TextView) findViewById(R.id.statusText);
        header = (TextView) findViewById(R.id.header);
        /*deadlineText.setOnClickListener(new View.OnClickListener() {
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
        });*/
        Button OKButton = (Button) findViewById(R.id.OKbutton);

        Intent intent = getIntent();
        final int idString = intent.getIntExtra(IntentConstants.ID, -1);
        Position = intent.getStringExtra(IntentConstants.POSITION);
        todoOpenHelper = TodoOpenHelper.getTodoOpenHelperInstance(TodoDetailActivity.this);
        database = todoOpenHelper.getWritableDatabase();

        // if todo is already in database
        if (idString != -1)
        {
            fillEditTextViews(idString);
            uniqueId = idString;
        }
        else
        {
            header.setText("Add New Todo");
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
                cv.put(TodoOpenHelper.TODO_DEADLINE_PASSED, deadlinePassed);
                cv.put(TodoOpenHelper.TODO_DONE, done);

                if (idString == -1) // add new
                {
                    uniqueId = insertTodo(cv);
                }
                else    // update existing todo
                {
                    updateTodo(cv, idString);
                }

                if (alarmSet && deadlineTime != 0)
                {
                    // setting alarm
                    AlarmManager am = (AlarmManager) TodoDetailActivity.this.getSystemService(Context.ALARM_SERVICE);
                    Intent i = new Intent(TodoDetailActivity.this, AlarmReceiver.class);
                    /*i.putExtra(IntentConstants.TITLE, title);
                    i.putExtra(IntentConstants.CATEGORY, category);
                    i.putExtra(IntentConstants.ID, uniqueId);*/
                    Todo todo = new Todo(title, category, description, (int) uniqueId, date, deadlineTime, deadlinePassed, done);
                    i.putExtra(IntentConstants.OBJECT, todo);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(TodoDetailActivity.this, (int) uniqueId, i, 0);
                    am.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent);
                }

                TodoDetailActivity.this.finish();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /*public void showDatePicker(Context context, int initialYear, int initialMonth, int initialDay) {

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

    }*/

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
        cursor.moveToNext();
        String title = cursor.getString(cursor.getColumnIndex(TodoOpenHelper.TODO_TITLE));
        titleText.setText(title);
        header.setText("Edit '" + title + "'");
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
            /*date = new Date(deadlineTime);
            text = format.format(date);
            deadlineText.setText("Deadline is " + text);*/
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(deadlineTime);
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("hh:mm a");
            String time = mSimpleDateFormat.format(mCalendar.getTime());
            deadlineText.setText("Deadline : " + mCalendar.get(Calendar.DAY_OF_MONTH) + "/" + mCalendar.get(Calendar.MONTH)
                    + "/" + mCalendar.get(Calendar.YEAR) + " at " + time);
        }
        deadlinePassed = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_DEADLINE_PASSED));
        done = cursor.getInt(cursor.getColumnIndex(TodoOpenHelper.TODO_DONE));
        if (deadlinePassed == 1)
        {
            if (done == 1)
            {
                statusText.setText("Done!");
                imageView.setBackgroundResource(R.drawable.tick);
            }
            else
            {
                statusText.setText("Not Done!");
                imageView.setBackgroundResource(R.drawable.cross);
            }
        }
        cursor.close();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    public long insertTodo(ContentValues cv)
    {
        long mUniqueId = database.insert(TodoOpenHelper.TODO_TABLE_NAME, null, cv);
        setResult(RESULT_OK);
        return mUniqueId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.set_alarm)
        {
            Intent intent = new Intent(this, SetAlarm.class);
            intent.putExtra(IntentConstants.TITLE, titleText.getText());
            intent.putExtra(IntentConstants.DEADLINE, deadlineTime);
            startActivityForResult(intent, IntentConstants.SET_ALARM);
        }
        else if (itemId == R.id.mark_as_done)
        {
            imageView.setBackgroundResource(R.drawable.tick);
            statusText.setText("Done!");
            done = 1;
            deadlinePassed = 1;
        }
        else if (itemId == R.id.mark_as_not_done)
        {
            imageView.setBackgroundResource(R.drawable.cross);
            statusText.setText("Not Done!");
            done = 0;
            deadlinePassed = 1;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentConstants.SET_ALARM && resultCode == RESULT_OK)
        {
            deadlineTime = data.getLongExtra(IntentConstants.ALARM_TIME_IN_EPOCH, 0);
            mCalendar = Calendar.getInstance();
            mCalendar.setTimeInMillis(deadlineTime);
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("hh:mm a");
            String time = mSimpleDateFormat.format(mCalendar.getTime());
            deadlineText.setText("Deadline : " + mCalendar.get(Calendar.DAY_OF_MONTH) + "/" + mCalendar.get(Calendar.MONTH)
                    + "/" + mCalendar.get(Calendar.YEAR) + " at " + time);
            alarmSet = true;
        }
    }
}
