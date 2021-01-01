package com.example.financemanager.ui.budget;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.financemanager.database.budget.Budget;
import com.example.financemanager.repository.BudgetRepository;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;

public class BudgetViewModel extends AndroidViewModel {

    public BudgetViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BudgetRepository(application);
        Calendar calendar = Calendar.getInstance();
        String month = getMonthFromInt(calendar.get(Calendar.MONTH));
        mYear = calendar.get(Calendar.YEAR);
        monthLiveData.setValue(month);
        allBudgets = Transformations.switchMap(monthLiveData, monthLiveData ->
                mRepository.getAllBudgets(monthLiveData, mYear));
    }

    private final int mYear;
    private final BudgetRepository mRepository;
    private MutableLiveData<String> monthLiveData = new MutableLiveData<>();
    private LiveData<List<Budget>> allBudgets;

    public LiveData<List<Budget>> getAllBudgets() {
        return allBudgets;
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

    public void filter(String month) {
        monthLiveData.setValue(month);
    }

}