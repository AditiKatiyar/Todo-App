package com.example.aditi.todo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("My Todos : Reminder!")
                .setAutoCancel(true)
                .setContentText(intent.getStringExtra(IntentConstants.TITLE) + " (" + intent.getStringExtra(IntentConstants.CATEGORY) + ")" )
                .setColor(ContextCompat.getColor(context, R.color.lighterBlue));

        int uniqueId = (int) intent.getLongExtra(IntentConstants.ID, 0);
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, uniqueId, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        nBuilder.setContentIntent(resultPendingIntent);
        NotificationManager nNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nNotificationManager.notify(uniqueId, nBuilder.build());
    }
}
