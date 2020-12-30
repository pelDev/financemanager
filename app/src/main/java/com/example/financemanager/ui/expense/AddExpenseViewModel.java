package com.example.financemanager.ui.expense;

import android.app.Activity;
import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.repository.ExpenditureRepository;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class AddExpenseViewModel extends AndroidViewModel {

    ExpenditureRepository mExpenditureRepository;
    public MutableLiveData<String> expenseName = new MutableLiveData<>();
    public MutableLiveData<String> expenseDescription = new MutableLiveData<>();
    public MutableLiveData<String> expenseAmount = new MutableLiveData<>();
    private MutableLiveData<Boolean> completed = new MutableLiveData<>();
    public final String[] spinnerTexts = {"Select", "Food", "Fashion", "Housing", "Investment", "Education", "Entertainment",
    "Transportation", "Other"};
    public MutableLiveData<Integer> spinnerItemPos = new MutableLiveData<>();
    private Calendar mCalendar;

    public AddExpenseViewModel(@NonNull Application application) {
        super(application);
        mExpenditureRepository = new ExpenditureRepository(application);
        completed.setValue(false);
        spinnerItemPos.setValue(0);
        mCalendar = Calendar.getInstance();
    }

    public boolean validateForm() {
        return  expenseName.getValue() != null
                && expenseAmount.getValue() != null
                && !expenseAmount.getValue().equals("")
                && !expenseName.getValue().equals("")
                && spinnerItemPos.getValue() != null
                && spinnerItemPos.getValue() > 0;
    }

    public void insertExpenditure() {
        if (validateForm()) {
            int day = mCalendar.get(Calendar.DAY_OF_MONTH);
            String month = getMonthFromInt(mCalendar.get(Calendar.MONTH));
            int year = mCalendar.get(Calendar.YEAR);
            Expenditure expenditure = new Expenditure(spinnerTexts[spinnerItemPos.getValue()],
                    expenseName.getValue(),
                    expenseDescription.getValue(),
                    day,
                    month,
                    year,
                    Integer.parseInt(expenseAmount.getValue()));
            mExpenditureRepository.insertExpenditure(expenditure);
            completed.setValue(true);
        }
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

    public MutableLiveData<Boolean> getCompleted() {
        return completed;
    }
}
