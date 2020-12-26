package com.example.financemanager.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.financemanager.database.FinanceManagerRoomDb;
import com.example.financemanager.database.amount.AmountDao;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class AmountRepository {

    private LiveData<Integer> totalIncome;
    private LiveData<Integer> totalExpense;
    private LiveData<Integer> totalExpenseMonth;
    private LiveData<Integer> totalIncomeMonth;

    public AmountRepository(Application application) {
        FinanceManagerRoomDb db = FinanceManagerRoomDb.getDatabase(application);
        AmountDao amountDao = db.amountDao();
        totalIncome = amountDao.getTotalIncome();
        totalExpense = amountDao.getTotalExpense();
        Calendar calendar = Calendar.getInstance();
        String month = getMonthFromInt(calendar.get(Calendar.MONTH));
        int year = calendar.get(Calendar.YEAR);
        totalExpenseMonth = amountDao.getTotalExpenseForMonth(month, year);
        totalIncomeMonth = amountDao.getTotalIncomeForYear(month, year);
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

    public LiveData<Integer> getTotalExpenseMonth() {
        return totalExpenseMonth;
    }

    public LiveData<Integer> getTotalIncomeMonth() { return totalIncomeMonth; }

    public LiveData<Integer> getTotalIncome() {
        return totalIncome;
    }

    public LiveData<Integer> getTotalExpense() {
        return totalExpense;
    }

}
