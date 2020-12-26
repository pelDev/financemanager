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

    public AddExpenseViewModel(@NonNull Application application) {
        super(application);
        mExpenditureRepository = new ExpenditureRepository(application);
    }

    public boolean validateForm() {
        return  expenseName.getValue() != null
                && expenseAmount.getValue() != null
                && !expenseAmount.getValue().equals("")
                && !expenseName.getValue().equals("");
    }

    public void insertExpenditure() {
        if (validateForm()) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String month = getMonthFromInt(calendar.get(Calendar.MONTH));
            int year = calendar.get(Calendar.YEAR);
            Expenditure expenditure = new Expenditure("food",
                    expenseName.getValue(),
                    expenseDescription.getValue(),
                    day,
                    month,
                    year,
                    Integer.parseInt(expenseAmount.getValue()));
            mExpenditureRepository.insertExpenditure(expenditure);
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

}
