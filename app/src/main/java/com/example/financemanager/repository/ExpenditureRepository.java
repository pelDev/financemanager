package com.example.financemanager.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.financemanager.database.FinanceManagerRoomDb;
import com.example.financemanager.database.budget.Budget;
import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.database.expense.ExpenditureDao;

import java.util.List;

public class ExpenditureRepository {

    private ExpenditureDao mExpenditureDao;

    private LiveData<List<Expenditure>> allExpenditures;

    public LiveData<List<Expenditure>> getAllExpenditures() { return allExpenditures; }

    private MutableLiveData<List<Expenditure>> expenseSearchResults = new MutableLiveData<>();

    public MutableLiveData<List<Expenditure>> getSearchResults() { return expenseSearchResults; }

    public ExpenditureRepository(Application application) {
        FinanceManagerRoomDb db =
                FinanceManagerRoomDb.getDatabase(application);
        mExpenditureDao = db.expenditureDao();
        allExpenditures = mExpenditureDao.getAllExpenditures();
    }

    public void insertExpenditure(Expenditure newExpenditure) {
        InsertAsyncTask task = new InsertAsyncTask(mExpenditureDao);
        task.execute(newExpenditure);
    }

    public void deleteExpenditure(int id) {
        DeleteAsyncTask task = new DeleteAsyncTask(mExpenditureDao);
        task.execute(id);
    }

    public void findExpenditure(String expenditureName) {
        QueryAsyncTask task = new QueryAsyncTask(mExpenditureDao);
        task.delegate = this;
        task.execute(expenditureName);
    }

    private void asyncFinished(List<Expenditure> results) {
        expenseSearchResults.setValue(results);
    }

    // async task to search for expenditure by name
    private static class QueryAsyncTask extends
            AsyncTask<String, Void, List<Expenditure>> {
        private ExpenditureDao asyncTaskDao;
        private ExpenditureRepository delegate = null;
        QueryAsyncTask(ExpenditureDao dao) {
            asyncTaskDao = dao;
        }
        @Override
        protected List<Expenditure> doInBackground(final String... params) {
            return asyncTaskDao.findExpenditureByName(params[0]);
        }
        @Override
        protected void onPostExecute(List<Expenditure> result) {
            delegate.asyncFinished(result);
        }
    }

    // async task to input expenditure into the database
    private static class InsertAsyncTask extends AsyncTask<Expenditure, Void, Void> {
        private ExpenditureDao asyncTaskDao;
        InsertAsyncTask(ExpenditureDao dao) {
            asyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Expenditure... params) {
            asyncTaskDao.insertExpenditure(params[0]);
            return null;
        }
    }

    // async task to delete expenditure from the database by id
    private static class DeleteAsyncTask extends AsyncTask<Integer, Void, Void> {
        private ExpenditureDao asyncTaskDao;
        DeleteAsyncTask(ExpenditureDao dao) {
            asyncTaskDao = dao;
        }
        @Override
        protected Void doInBackground(final Integer... params) {
            asyncTaskDao.deleteExpenditure(params[0]);
            return null;
        }
    }

}
