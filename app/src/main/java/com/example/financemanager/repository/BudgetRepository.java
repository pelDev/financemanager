package com.example.financemanager.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.financemanager.database.FinanceManagerRoomDb;
import com.example.financemanager.database.budget.Budget;
import com.example.financemanager.database.budget.BudgetDao;

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
}
