package com.example.aditi.todo;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if (intent.getAction() != null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.

            long currentTime = System.currentTimeMillis();

            TodoOpenHelper todoOpenHelper = TodoOpenHelper.getTodoOpenHelperInstance(context);
            SQLiteDatabase database = todoOpenHelper.getReadableDatabase();
            Cursor cursor = database.query(TodoOpenHelper.TODO_TABLE_NAME, null, null, null, null, null, null);
            int titleIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_TITLE);
            int categoryIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_CATEGORY);
            int descriptionIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_DESCRIPTION);
            int idIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_ID);
            int dateIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_DATE);
            int deadlineIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_DEADLINE);
            int deadlinePassedIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_DEADLINE_PASSED);
            int doneIndex = cursor.getColumnIndex(TodoOpenHelper.TODO_DONE);
            while(cursor.moveToNext())
            {
                String title = cursor.getString(titleIndex);
                String category = cursor.getString(categoryIndex);
                String description = cursor.getString(descriptionIndex);
                int _id = cursor.getInt(idIndex);
                long date = cursor.getLong(dateIndex);
                long deadline = cursor.getLong(deadlineIndex);
                int deadlinePassed = cursor.getInt(deadlinePassedIndex);
                int done = cursor.getInt(doneIndex);
                Todo todo = new Todo(title, category, description, _id, date, deadline, deadlinePassed, done);
                if (deadline <= currentTime && deadlinePassed == 0)
                {
                    setNotification(todo, context);
                }
                else if (deadline > currentTime && deadlinePassed == 0)
                {
                    setAlarm(todo, context);
                }

            }
        }
        else
        {
            Todo todo = (Todo) intent.getSerializableExtra(IntentConstants.OBJECT);

            setNotification(todo, context);
        }


    }

    private void setNotification(Todo todo, Context context)
    {
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle("My Todos : Reminder!")
                .setAutoCancel(true)
                .setContentText(todo.title + " (" + todo.category + ")")
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary));

        /*int uniqueId = (int) intent.getLongExtra(IntentConstants.ID, 0);*/

        int uniqueId = todo.id;
        Intent resultIntent = new Intent(context, AfterAlarmActivity.class);
        resultIntent.putExtra(IntentConstants.OBJECT, todo);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, uniqueId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(resultPendingIntent);
        NotificationManager nNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nNotificationManager.notify(uniqueId, nBuilder.build());
    }

    private void setAlarm(Todo todo, Context context)
    {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        i.putExtra(IntentConstants.OBJECT, todo);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) todo.id, i, 0);
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(todo.deadline);
        am.set(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), pendingIntent);
    }
}
/*.setContentText(intent.getStringExtra(IntentConstants.TITLE) + " (" + intent.getStringExtra(IntentConstants.CATEGORY) + ")" )*/