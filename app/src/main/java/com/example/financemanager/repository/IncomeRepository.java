package com.example.financemanager.repository;


import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.financemanager.database.FinanceManagerRoomDb;
import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.database.expense.ExpenditureDao;
import com.example.financemanager.database.income.Income;
import com.example.financemanager.database.income.IncomeDao;

import java.util.List;

public class IncomeRepository {

    private IncomeDao mIncomeDao;

    private LiveData<List<Income>> allIncomes;

    public LiveData<List<Income>> getAllIncomes() {
        return allIncomes;
    }

    public IncomeRepository(Application application) {
        FinanceManagerRoomDb db = FinanceManagerRoomDb.getDatabase(application);
        mIncomeDao = db.incomeDao();
        allIncomes = mIncomeDao.getAllIncomes();
    }

    public void insertIncome(Income income) {
        InsertAsyncTask task = new InsertAsyncTask(mIncomeDao);
        task.execute(income);
    }

    // async task to input income into the database
    private static class InsertAsyncTask extends AsyncTask<Income, Void, Void> {
        private IncomeDao asyncTaskDao;
        InsertAsyncTask(IncomeDao dao) {
            asyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Income... params) {
            asyncTaskDao.insertIncome(params[0]);
            return null;
        }
    }

}
