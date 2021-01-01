package com.example.financemanager.ui.expense;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.repository.BudgetRepository;
import com.example.financemanager.repository.ExpenditureRepository;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class AddExpenseViewModel extends AndroidViewModel {

    ExpenditureRepository mExpenditureRepository;
    public MutableLiveData<String> expenseName = new MutableLiveData<>();
    public MutableLiveData<String> expenseDescription = new MutableLiveData<>();
    public MutableLiveData<String> expenseAmount = new MutableLiveData<>();
    private MutableLiveData<Boolean> completed = new MutableLiveData<>();
    private  MutableLiveData<String> error = new MutableLiveData<>();
    public final String[] spinnerTexts = {"Select", "Food", "Fashion", "Housing", "Recreation", "Investment", "Education", "Entertainment",
    "Transportation", "Other"};
    public MutableLiveData<Integer> spinnerItemPos = new MutableLiveData<>();
    private Calendar mCalendar;
    private final BudgetRepository mBudgetRepository;

    public AddExpenseViewModel(@NonNull Application application) {
        super(application);
        mExpenditureRepository = new ExpenditureRepository(application);
        mBudgetRepository = new BudgetRepository(application);
        completed.setValue(false);
        spinnerItemPos.setValue(0);
        mCalendar = Calendar.getInstance();
        error.setValue("");
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
            if (checkBudgetForCategory()) {
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                int monthInt = mCalendar.get(Calendar.MONTH);
                String month = getMonthFromInt(monthInt);
                int year = mCalendar.get(Calendar.YEAR);
                Expenditure expenditure = new Expenditure(spinnerTexts[spinnerItemPos.getValue()],
                        expenseName.getValue(),
                        expenseDescription.getValue(),
                        day,
                        month,
                        monthInt,
                        year,
                        Integer.parseInt(expenseAmount.getValue()));
                mExpenditureRepository.insertExpenditure(expenditure);
                completed.setValue(true);
            } else {
                error.setValue("Budget not set for " + spinnerTexts[spinnerItemPos.getValue()]);
            }
        } else {
            error.setValue("Enter correct form details");
        }
    }

    private boolean checkBudgetForCategory() {
        String month = getMonthFromInt(mCalendar.get(Calendar.MONTH));
        int year = mCalendar.get(Calendar.YEAR);
        int result = mBudgetRepository.getNoOfBudgetForCategoryAndTime(
                spinnerTexts[spinnerItemPos.getValue()],
                month,
                year
        );
        return result != 0 && result != -1;
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

    public MutableLiveData<String> getError() {
        return error;
    }

    public void setError(String error) {
        this.error.setValue(error);
    }
}
