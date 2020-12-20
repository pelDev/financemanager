package com.example.financemanager.ui.budget;

import android.app.Activity;
import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.financemanager.database.budget.Budget;
import com.example.financemanager.repository.BudgetRepository;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class AddBudgetViewModel extends AndroidViewModel {

    private final BudgetRepository mBudgetRepository;
    public MutableLiveData<String> budgetAmount = new MutableLiveData<>();

    public AddBudgetViewModel(@NonNull Application application) {
        super(application);
        mBudgetRepository = new BudgetRepository(application);
    }

    public void insertBudget() {
        Calendar calendar = Calendar.getInstance();
        String month = getMonthFromInt(calendar.get(Calendar.MONTH));
        int year = calendar.get(Calendar.YEAR);
        Budget budget = new Budget("food",
                Integer.parseInt(budgetAmount.getValue()),
                month,
                year);
        mBudgetRepository.insertBudget(budget);
        isSuccessful.set(true);
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

    public final ObservableBoolean isSuccessful = new ObservableBoolean();

    @BindingAdapter("android:onFinish")
    public static void finishAddExpense(View v, boolean isFinished){

        if (isFinished) {
            ((Activity) (v.getContext())).finish();
        }
    }


}
