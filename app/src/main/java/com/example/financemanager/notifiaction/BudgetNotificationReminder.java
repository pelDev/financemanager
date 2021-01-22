package com.example.financemanager.notifiaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.financemanager.MainActivity;
import com.example.financemanager.R;

import java.util.Calendar;

import static com.example.financemanager.Constants.DESTINATION_FRAGMENT;
import static com.example.financemanager.Constants.MOVE_TO_BUDGET;

public class BudgetNotificationReminder extends Worker {
    private static final int NOTIFICATION_ID = 1;
    private static final String MONTHLY_REMINDER = "notify-finance-manager";

    public BudgetNotificationReminder(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public void makeBudgetReminderNotification() {
        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            //String description = Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel(MONTHLY_REMINDER, "Budget Notification", importance);
            channel.setDescription("");

            // Add the channel
            NotificationManager notificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create the notification
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(DESTINATION_FRAGMENT, MOVE_TO_BUDGET);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(),
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), MONTHLY_REMINDER)
                .setSmallIcon(R.drawable.ic_budget)
                .setContentTitle("Reminder")
                .setContentText("This is the right time to set your budget for this month.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0])
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Show the notification
        NotificationManagerCompat.from(getApplicationContext()).notify(NOTIFICATION_ID, builder.build());

    }

    @NonNull
    @Override
    public Result doWork() {
        Calendar calendar = Calendar.getInstance();

        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        // send user reminder when it is the first day of the month.
        if (dayOfMonth == 1) {
            makeBudgetReminderNotification();
            return Result.success();
        } else {
            return Result.failure();
        }
    }
}
