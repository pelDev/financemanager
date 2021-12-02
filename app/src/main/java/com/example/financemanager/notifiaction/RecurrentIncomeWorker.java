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

import java.util.Calendar;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.app.PendingIntent.getActivity;
import static android.app.PendingIntent.getService;
import static com.example.financemanager.Constants.DESTINATION_FRAGMENT;
import static com.example.financemanager.Constants.INCOME_WORKER_NAME;
import static com.example.financemanager.Constants.KEY_WORKER_NAME;
import static com.example.financemanager.Constants.MOVE_TO_ADD_INCOME;
import static com.example.financemanager.notifiaction.YesOrNoService.ACTION_NO;
import static com.example.financemanager.notifiaction.YesOrNoService.ACTION_YES;

public class RecurrentIncomeWorker extends Worker {

    public RecurrentIncomeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public static final int NOTIFICATION_ID = 2;
    private static final String INCOME_HELPER = "income-helper";

    public void makeIncomeNotification(String name, String incomeWorkName) {
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
                getActivity(getApplicationContext(),
                        0, intent, FLAG_UPDATE_CURRENT);

        // create pending intent for actions
        Intent yesIntent = new Intent(getApplicationContext(), YesOrNoService.class);
        yesIntent.putExtra(KEY_WORKER_NAME, incomeWorkName);
        yesIntent.setAction(ACTION_YES);
        PendingIntent yesPendingIntent = getService(
                getApplicationContext(), 1,yesIntent, FLAG_UPDATE_CURRENT
        );

        Intent noIntent = new Intent(getApplicationContext(), YesOrNoService.class);
        noIntent.putExtra(KEY_WORKER_NAME, incomeWorkName);
        noIntent.setAction(ACTION_NO);
        PendingIntent noPendingIntent = getService(
                getApplicationContext(), 2, noIntent, FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), INCOME_HELPER)
                .setSmallIcon(R.drawable.ic_action_income)
                .setContentTitle("Reminder")
                .setContentText("Have you received your " + name + " income?")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(0, "Yes", yesPendingIntent)
                .addAction(0, "No", noPendingIntent)
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

            switch (recurrentIncome.getFrequency()) {
                case "Daily":
                    if (recurrentIncome.getStartDay() != Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                        makeIncomeNotification(incomeName, incomeWorkName);
                    break;
                case "Weekly":
                    if (recurrentIncome.getDaysPassed() == 6) {
                        makeIncomeNotification(incomeName, incomeWorkName);
                    } else {
                        recurrentIncome.setDaysPassed(recurrentIncome.getDaysPassed() + 1);
                        rIncomeRepository.updateRIncome(recurrentIncome);
                    }
                    break;
                case "Monthly":
                    int month = Calendar.getInstance().get(Calendar.MONTH);
                    int startDay = recurrentIncome.getStartDay();
                    int daysPassed = recurrentIncome.getDaysPassed();
                    int today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    if (startDay > 28 && daysPassed >= 27 && month == Calendar.FEBRUARY
                            && today >= 28) {
                        makeIncomeNotification(incomeName, incomeWorkName);
                    } else if (startDay == 31 && daysPassed == 29) {
                        if (month == Calendar.APRIL || month == Calendar.JUNE
                                || month == Calendar.SEPTEMBER || month == Calendar.NOVEMBER) {
                            makeIncomeNotification(incomeName, incomeWorkName);
                        }
                    } else if (daysPassed > 0 && today == startDay) {
                        makeIncomeNotification(incomeName, incomeWorkName);
                    } else {
                        recurrentIncome.setDaysPassed(recurrentIncome.getDaysPassed() + 1);
                        rIncomeRepository.updateRIncome(recurrentIncome);
                    }
                    break;
            }
            return Result.success();
        }
        return Result.failure();
    }
}
