package com.example.financemanager.notifiaction;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.financemanager.MainActivity;
import com.example.financemanager.R;
import com.example.financemanager.database.recurrentIncome.RecurrentIncome;
import com.example.financemanager.repository.IncomeRepository;
import com.example.financemanager.repository.RIncomeRepository;

import static com.example.financemanager.Constants.DESTINATION_FRAGMENT;
import static com.example.financemanager.Constants.INCOME_WORKER_NAME;
import static com.example.financemanager.Constants.KEY_WORKER_NAME;
import static com.example.financemanager.Constants.MOVE_TO_ADD_INCOME;

public class RecurrentIncomeWorker extends Worker {

    public RecurrentIncomeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private static final int NOTIFICATION_ID = 2;
    private static final String INCOME_HELPER = "income-helper";

    public void makeIncomeNotification(String name) {
        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            //String description = Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel(INCOME_HELPER, "Income Notif", importance);
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
        intent.putExtra(DESTINATION_FRAGMENT, MOVE_TO_ADD_INCOME);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(getApplicationContext(),
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), INCOME_HELPER)
                .setSmallIcon(R.drawable.ic_action_income)
                .setContentTitle("Reminder")
                .setContentText("Have you received your " + name + " income?")
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
        Context context = getApplicationContext();
        final RIncomeRepository rIncomeRepository =
                new RIncomeRepository(context);

        final IncomeRepository incomeRepository =
                new IncomeRepository(context);

        String incomeWorkName = getInputData().getString(KEY_WORKER_NAME);

        String[] arr = incomeWorkName.split(INCOME_WORKER_NAME);

        String incomeName = incomeRepository.getIncomeById(Integer.parseInt(arr[1]))
                .getName();
        if (!TextUtils.isEmpty(incomeWorkName)
                && !TextUtils.isEmpty(incomeName)) {
            // retrieve work reference
            RecurrentIncome recurrentIncome =
                    rIncomeRepository.getRIncome(incomeWorkName);

            if (recurrentIncome.getFrequency().equals("Daily")) {
                makeIncomeNotification(incomeName);
            }
            return Result.success();
        }
        return Result.failure();
    }
}
