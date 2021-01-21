package com.example.financemanager.ui.netincome;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.financemanager.database.income.Income;
import com.example.financemanager.repository.IncomeRepository;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class NetIncomeViewModel extends AndroidViewModel {

    private final IncomeRepository mRepository;
    public MutableLiveData<String> incomeAmount = new MutableLiveData<>();
    private final LiveData<List<Income>> mAllIncomes;
    private final MutableLiveData<Boolean> completed = new MutableLiveData<>();
    private final MutableLiveData<Boolean> invalidAmount = new MutableLiveData<>();
    public MutableLiveData<Boolean> isRecurrent = new MutableLiveData<>();
    public int incomePosition = -1;
    public Income income = null;

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
                income.setAmount(Integer.parseInt(incomeAmount.getValue()));
                income.setRecurrent(isRecurrent.getValue());
                mRepository.updateIncome(income);
            }
            completed.setValue(true);
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
                    day,
                    month,
                    monthInt,
                    year,
                    Integer.parseInt(Objects.requireNonNull(incomeAmount.getValue())),
                    Objects.requireNonNull(isRecurrent.getValue()));
            mRepository.insertIncome(income);
            completed.setValue(true);
            invalidAmount.setValue(false);
        } else {
            invalidAmount.setValue(true);
        }
    }

    public void yesToRecurrent() {
        isRecurrent.setValue(true);
    }

    public void noToRecurrent() {
        isRecurrent.setValue(false);
    }

    private boolean validateForm() {
        return incomeAmount.getValue() != null && !incomeAmount.getValue().equals("");
    }

    public NetIncomeViewModel(@NonNull Application application) {
        super(application);
        mRepository = new IncomeRepository(application);
        mAllIncomes = mRepository.getAllIncomes();
        completed.setValue(false);
    }

    public Income getIncomeById(int id) {

        return mRepository.getIncomeById(id);
    }

    public MutableLiveData<Boolean> getIsRecurrent() {
        return isRecurrent;
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

    private String getMonthFromInt(int month) {
        String monthString = "";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (month >= 0 && month <= 11) {
            monthString = months[month];
        }
        return monthString;
    }
}
