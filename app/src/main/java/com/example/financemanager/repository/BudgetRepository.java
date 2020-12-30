package com.example.financemanager.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.financemanager.database.FinanceManagerRoomDb;
import com.example.financemanager.database.budget.Budget;
import com.example.financemanager.database.budget.BudgetDao;
import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.database.expense.ExpenditureDao;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;

public class BudgetRepository {

    private String getMonthFromInt(int month) {
        String monthString = "";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (month >= 0 && month <= 11) {
            monthString = months[month];
        }
        return monthString;
    }

    private BudgetDao mBudgetDao;

    LiveData<List<Budget>> mAllBudgets;

    public LiveData<List<Budget>> getAllBudgets() {
        return mAllBudgets;
    }

    public BudgetRepository(Application application) {
        FinanceManagerRoomDb db = FinanceManagerRoomDb.getDatabase(application);
        Calendar calendar = Calendar.getInstance();
        String month = getMonthFromInt(calendar.get(Calendar.MONTH));
        int year = calendar.get(Calendar.YEAR);
        mBudgetDao = db.budgetDao();
        mAllBudgets = mBudgetDao.getBudgetList(month, year);
    }

    public void insertBudget(Budget budget) {
        InsertAsyncTask task = new InsertAsyncTask(mBudgetDao);
        task.execute(budget);
    }

    // async task to input expenditure into the database
    private static class InsertAsyncTask extends AsyncTask<Budget, Void, Void> {
        private BudgetDao asyncTaskDao;
        InsertAsyncTask(BudgetDao dao) {
            asyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Budget... params) {
            asyncTaskDao.insertBudget(params[0]);
            return null;
        }
    }
}
