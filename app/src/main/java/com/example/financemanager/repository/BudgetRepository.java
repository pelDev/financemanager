package com.example.financemanager.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.financemanager.database.FinanceManagerRoomDb;
import com.example.financemanager.database.budget.Budget;
import com.example.financemanager.database.budget.BudgetDao;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class BudgetRepository {

    private BudgetDao mBudgetDao;

    public LiveData<List<Budget>> getAllBudgets(String month, int year) {
        return mBudgetDao.getBudgetList(month, year);
    }

    public BudgetRepository(Application application) {
        FinanceManagerRoomDb db = FinanceManagerRoomDb.getDatabase(application);
        mBudgetDao = db.budgetDao();
    }

    public void insertBudget(Budget budget) {
        executor.execute(() -> mBudgetDao.insertBudget(budget));
    }

    public int getNoOfBudgetForCategoryAndTime(String category, String month, int year) {
        try {
            return executor.submit(() ->
                    mBudgetDao.getBudgetCountForCategoryAndTime(category, month, year)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    public static final ExecutorService executor = Executors.newSingleThreadExecutor();

}
