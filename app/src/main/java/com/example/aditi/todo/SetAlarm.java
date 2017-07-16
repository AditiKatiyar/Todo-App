package com.example.aditi.todo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SetAlarm extends AppCompatActivity {

    TextView dateTV;
    TextView timeTV;
    Button setAlarm;
    Calendar alarmCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        // fetching layout elements from xml
        dateTV = (TextView) findViewById(R.id.date_tv);
        timeTV = (TextView) findViewById(R.id.time_tv);
        setAlarm = (Button) findViewById(R.id.set_alarm_button);

        // fetching intent
        Intent intent = getIntent();
        String title = intent.getStringExtra(IntentConstants.TITLE);
        long deadline = intent.getLongExtra(IntentConstants.DEADLINE, 0);

        // setting text of textViews
        if (deadline != 0)
        {
            alarmCalendar.setTimeInMillis(deadline);
            dateTV.setText("Date : " + alarmCalendar.get(Calendar.DAY_OF_MONTH) + "/" + alarmCalendar.get(Calendar.MONTH)
                    + "/" + alarmCalendar.get(Calendar.YEAR));
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("hh:mm a");
            String time = mSimpleDateFormat.format(alarmCalendar.getTime());
            timeTV.setText("Time : " + time);
        }


        // creating my calendar
        final Calendar mCalendar = Calendar.getInstance();

        // setting onClick Listeners
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = mCalendar.get(Calendar.YEAR);
                int month = mCalendar.get(Calendar.MONTH);
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                showDatePicker(SetAlarm.this, year, month, day);
            }
        });

        timeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                int minutes = mCalendar.get(Calendar.MINUTE);
                showTimePicker(SetAlarm.this, hour, minutes);
            }
        });

        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dateTV.getText().length() == 0)
                {
                    dateTV.setError("Enter Date");
                }
                else if (timeTV.getText().length() == 0)
                {
                    timeTV.setError("Enter Time");
                }
                else if (alarmCalendar.compareTo(mCalendar) <= 0)
                {
                    Toast.makeText(SetAlarm.this, "Invalid time", Toast.LENGTH_SHORT).show();
                }
                else
                {


                    // creating intent to go back to previous activity
                    Intent intent = new Intent(SetAlarm.this, TodoDetailActivity.class);
                    intent.putExtra(IntentConstants.ALARM_TIME_IN_EPOCH, alarmCalendar.getTimeInMillis());
                    setResult(RESULT_OK, intent);
                    SetAlarm.this.finish();
                }

            }
        });
    }

    private void showTimePicker(Context context, int hour, int minutes) {
        TimePickerDialog mTimePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                alarmCalendar.set(Calendar.MINUTE, minute);
                SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("hh:mm a");
                String time = mSimpleDateFormat.format(alarmCalendar.getTime());
                timeTV.setText("Time : " + time);
            }
        }, hour, minutes, false);

        mTimePickerDialog.show();
    }

    private void showDatePicker(Context context, int initilaYear, int initialMonth, int initialDay) {
        DatePickerDialog mDatePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        alarmCalendar.set(year, month, dayOfMonth);
                        dateTV.setText("Date : " + dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, initilaYear, initialMonth, initialDay);
        mDatePickerDialog.show();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        this.finish();
    }
}
