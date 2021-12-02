package com.example.financemanager.ui.netincome;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.financemanager.database.income.Income;
import com.example.financemanager.database.recurrentIncome.RecurrentIncome;
import com.example.financemanager.notifiaction.RecurrentIncomeWorker;
import com.example.financemanager.repository.IncomeRepository;
import com.example.financemanager.repository.RIncomeRepository;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.example.financemanager.Constants.INCOME_WORKER_NAME;
import static com.example.financemanager.Constants.KEY_WORKER_NAME;
import static com.example.financemanager.Shared.createInputDataForWorkName;
import static com.example.financemanager.Shared.getMonthFromInt;
import static com.example.financemanager.Shared.logInfo;

public class NetIncomeViewModel extends AndroidViewModel {

    private final IncomeRepository mRepository;
    private final RIncomeRepository mRIncomeRepository;
    public MutableLiveData<String> incomeAmount = new MutableLiveData<>();
    public MutableLiveData<String> incomeName = new MutableLiveData<>();
    private final LiveData<List<Income>> mAllIncomes;
    private final MutableLiveData<Boolean> completed = new MutableLiveData<>();
    private final MutableLiveData<Boolean> invalidAmount = new MutableLiveData<>();
    public MutableLiveData<Boolean> isRecurrent = new MutableLiveData<>();
    public int incomePosition = -1;
    public Income income = null;
    private int currentIncomeId;
    private final String TAG = getClass().getSimpleName();
    public String intervalString = "";

    public void savePressed() {
        if (incomePosition == -1)
            insertIncome();
        else if (incomePosition >= 0) {
            updateIncome();
        }
    }

    private void updateIncome() {
        if (validateForm()) {
            if (income != null) {
                income.setName(incomeName.getValue());
                income.setAmount(Integer.parseInt(incomeAmount.getValue()));
                income.setRecurrent(isRecurrent.getValue());
                mRepository.updateIncome(income);
                currentIncomeId = income.getId();
                setUpOrRemoveWork();
            }
            invalidAmount.setValue(false);
            completed.setValue(true);
        } else {
            invalidAmount.setValue(true);
        }
    }

    private void insertIncome() {
        if (validateForm()) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int monthInt = calendar.get(Calendar.MONTH);
            String month = getMonthFromInt(monthInt);
            int year = calendar.get(Calendar.YEAR);
            Income income = new Income(
                    Objects.requireNonNull(incomeName.getValue()),
                    day,
                    month,
                    monthInt,
                    year,
                    Integer.parseInt(Objects.requireNonNull(incomeAmount.getValue())),
                    Objects.requireNonNull(isRecurrent.getValue()));
            mRepository.insertIncome(income);
            invalidAmount.setValue(false);
            currentIncomeId = mRepository.getLastEntry();
            setUpOrRemoveWork();
            completed.setValue(true);
        } else {
            invalidAmount.setValue(true);
        }
    }

    private void setUpOrRemoveWork() {
        if (isRecurrent.getValue())
            setUpIncomeForRecurrentWork(currentIncomeId);
        else
            removeIncomeFromRecurrentWork(currentIncomeId);
    }

    private boolean validateForm() {
        return incomeAmount.getValue() != null
                && !incomeAmount.getValue().equals("")
                && !incomeName.getValue().equals("");
    }

    private void setUpIncomeForRecurrentWork(int incomeId) {
        logInfo(TAG,"Setting up income " + incomeId + " for recurrent work!");
        // check if recurrent work already exists
        RecurrentIncome work = mRIncomeRepository.getRIncome("income-worker" + incomeId);
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
        mRIncomeRepository.insertRIncome(recurrentIncome);

        logInfo(TAG,recurrentIncome.getWorkerName() +
                " has been queued for a " + intervalString + " interval.");
    }

    private void removeIncomeFromRecurrentWork(int incomeId) {
        logInfo(TAG, "Removing income " + incomeId + " from recurrent work!");
        // cancel work related to income
        WorkManager.getInstance(getApplication().getApplicationContext())
                .cancelUniqueWork(INCOME_WORKER_NAME + incomeId);
        // remove reference from database
        RecurrentIncome recurrentIncome = mRIncomeRepository
                .getRIncome(INCOME_WORKER_NAME + incomeId);
        if (recurrentIncome != null)
            mRIncomeRepository.deleteRIncome(recurrentIncome);

        logInfo(TAG, "Removed income " + incomeId + " from recurrent work!");
    }

    public NetIncomeViewModel(@NonNull Application application) {
        super(application);
        mRepository = new IncomeRepository(application);
        mRIncomeRepository = new RIncomeRepository(application);
        mAllIncomes = mRepository.getAllIncomes();
        completed.setValue(false);
    }

    public Income getIncomeById(int id) {

        return mRepository.getIncomeById(id);
    }

    public void setIsRecurrent(boolean isRecurrent) {
        this.isRecurrent.setValue(isRecurrent);
    }

    public MutableLiveData<Boolean> getCompleted() {
        return completed;
    }

    public LiveData<List<Income>> getAllIncomes() {
        return mAllIncomes;
    }

    public MutableLiveData<Boolean> getInvalidAmount() {
        return invalidAmount;
    }

}
