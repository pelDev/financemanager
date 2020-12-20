package com.example.financemanager.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.financemanager.database.FinanceManagerRoomDb;
import com.example.financemanager.database.budget.Budget;
import com.example.financemanager.database.budget.BudgetDao;
import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.database.expense.ExpenditureDao;

import java.util.List;

public class BudgetRepository {

    private BudgetDao mBudgetDao;

    LiveData<List<Budget>> mAllBudgets;

    public LiveData<List<Budget>> getAllBudgets() {
        return mAllBudgets;
    }

    public BudgetRepository(Application application) {
        FinanceManagerRoomDb db = FinanceManagerRoomDb.getDatabase(application);
        mBudgetDao = db.budgetDao();
        mAllBudgets = mBudgetDao.getBudgetList();
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
