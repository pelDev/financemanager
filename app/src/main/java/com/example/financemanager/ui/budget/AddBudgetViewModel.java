package com.example.financemanager.ui.budget;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.financemanager.database.budget.Budget;
import com.example.financemanager.repository.BudgetRepository;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class AddBudgetViewModel extends AndroidViewModel {

    private final BudgetRepository mBudgetRepository;
    public MutableLiveData<String> budgetAmount = new MutableLiveData<>();
    private MutableLiveData<Boolean> completed = new MutableLiveData<>();
    public final String[] spinnerTexts = {"Select", "Food", "Fashion", "Housing", "Investment", "Education", "Entertainment",
            "Transportation", "Other"};
    public MutableLiveData<Integer> spinnerItemPos = new MutableLiveData<>();

    public AddBudgetViewModel(@NonNull Application application) {
        super(application);
        mBudgetRepository = new BudgetRepository(application);
        completed.setValue(false);
        spinnerItemPos.setValue(0);
    }

    public MutableLiveData<Boolean> getCompleted() {
        return completed;
    }

    public void insertBudget() {
        Calendar calendar = Calendar.getInstance();
        String month = getMonthFromInt(calendar.get(Calendar.MONTH));
        int year = calendar.get(Calendar.YEAR);
        Budget budget = new Budget(spinnerTexts[spinnerItemPos.getValue()],
                Integer.parseInt(budgetAmount.getValue()),
                month,
                year);
        mBudgetRepository.insertBudget(budget);
        completed.setValue(true);
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
