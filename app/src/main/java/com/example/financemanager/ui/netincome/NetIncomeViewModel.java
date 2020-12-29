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

public class NetIncomeViewModel extends AndroidViewModel {

    private final IncomeRepository mRepository;
    public MutableLiveData<String> incomeAmount = new MutableLiveData<>();
    private LiveData<List<Income>> mAllIncomes;
    private MutableLiveData<Boolean> completed = new MutableLiveData<>();
    private MutableLiveData<Boolean> invalidAmount = new MutableLiveData<>();

    public void insertIncome() {
        if (validateForm()) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String month = getMonthFromInt(calendar.get(Calendar.MONTH));
            int year = calendar.get(Calendar.YEAR);
            Income income = new Income(
                    day,
                    month,
                    year,
                    Integer.parseInt(incomeAmount.getValue()));
            mRepository.insertIncome(income);
            completed.setValue(true);
            invalidAmount.setValue(false);
        } else {
            invalidAmount.setValue(true);
        }
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
