package com.example.financemanager.notifiaction;

import android.app.IntentService;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.financemanager.database.income.Income;
import com.example.financemanager.database.recurrentIncome.RecurrentIncome;
import com.example.financemanager.repository.IncomeRepository;
import com.example.financemanager.repository.RIncomeRepository;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static com.example.financemanager.Constants.INCOME_WORKER_NAME;
import static com.example.financemanager.Constants.KEY_WORKER_NAME;
import static com.example.financemanager.Shared.createInputDataForWorkName;
import static com.example.financemanager.Shared.getMonthFromInt;
import static com.example.financemanager.Shared.logInfo;

public class YesOrNoService extends IntentService {

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_YES = "com.example.financemanager.notifiaction.action.YES";
    public static final String ACTION_NO = "com.example.financemanager.notifiaction.action.NO";

    private IncomeRepository mIncomeRepository;
    private RIncomeRepository mRepository;
    private final String TAG = getClass().getSimpleName();

    public YesOrNoService() {
        super("YesOrNoService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            final String incomeWorkerName = intent.getStringExtra(KEY_WORKER_NAME);
            mRepository = new RIncomeRepository(getApplicationContext());
            mIncomeRepository = new IncomeRepository(getApplicationContext());

            if (action.equals(ACTION_YES)) {
                handleActionYes(incomeWorkerName);
                cancelPeriodicIncomeWork(incomeWorkerName);
            } else if (action.equals(ACTION_NO)) {
                cancelPeriodicIncomeWork(incomeWorkerName);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionYes(String incomeWorkerName) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int monthInt = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        Income relatedIncome = mIncomeRepository.getIncomeById(
                Integer.parseInt(incomeWorkerName.split(INCOME_WORKER_NAME)[1]));
        Income newIncome = new Income(
                relatedIncome.getName(),
                day,
                getMonthFromInt(monthInt),
                monthInt,
                year,
                relatedIncome.getAmount(),
                relatedIncome.isRecurrent()
        );
        mIncomeRepository.insertIncome(newIncome);

        RecurrentIncome recurrentIncome = mRepository.getRIncome(incomeWorkerName);
        setUpIncomeForRecurrentWork(mIncomeRepository.getLastEntry(), recurrentIncome.getFrequency());
        NotificationManagerCompat.from(getApplicationContext()).cancel(RecurrentIncomeWorker.NOTIFICATION_ID);
    }

    private void setUpIncomeForRecurrentWork(int incomeId, String intervalString) {
        logInfo(TAG,"Setting up income " + incomeId + " for recurrent work!");
        // check if recurrent work already exists
        RecurrentIncome work = mRepository.getRIncome("income-worker" + incomeId);
        if (work != null) {
            logInfo(TAG,"Recurrent work for " + incomeId + " already exists.");
            return;
        }
        int interval;
        switch (intervalString) {
            case "Daily":
                interval = 1;
                break;
            case "Weekly":
                interval = 7;
                break;
            case "Monthly":
                interval = 30;
                break;
            default:
                interval = -1;
        }
        // add recurrent work
        PeriodicWorkRequest.Builder workRequest =
                new PeriodicWorkRequest.Builder(
                        RecurrentIncomeWorker.class,
                        interval,
                        TimeUnit.DAYS
                );
        workRequest.setInputData(createInputDataForWorkName(incomeId));
        WorkManager.getInstance(getApplication().getApplicationContext())
                .enqueueUniquePeriodicWork(INCOME_WORKER_NAME + incomeId,
                        ExistingPeriodicWorkPolicy.REPLACE,
                        workRequest.build());
        // add reference to database
        RecurrentIncome recurrentIncome =
                new RecurrentIncome(INCOME_WORKER_NAME + incomeId,
                        0, intervalString, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        mRepository.insertRIncome(recurrentIncome);

        logInfo(TAG,recurrentIncome.getWorkerName() +
                " has been queued for a " + intervalString + " interval.");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void cancelPeriodicIncomeWork(String incomeWorkerName) {
        // cancel work related to income
        WorkManager.getInstance(getApplicationContext())
                .cancelUniqueWork(incomeWorkerName);
        // update related income
        Income relatedIncome = mIncomeRepository.getIncomeById(
                Integer.parseInt(incomeWorkerName.split(INCOME_WORKER_NAME)[1])
        );
        relatedIncome.setRecurrent(false);
        mIncomeRepository.updateIncome(relatedIncome);
        // remove reference from database
        RecurrentIncome recurrentIncome = mRepository.getRIncome(incomeWorkerName);
        if (recurrentIncome != null)
            mRepository.deleteRIncome(recurrentIncome);

        NotificationManagerCompat.from(getApplicationContext()).cancel(RecurrentIncomeWorker.NOTIFICATION_ID);
    }
}