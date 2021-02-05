package com.example.financemanager.repository;


import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.financemanager.database.FinanceManagerRoomDb;
import com.example.financemanager.database.income.Income;
import com.example.financemanager.database.income.IncomeDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class IncomeRepository {

    private final IncomeDao mIncomeDao;

    private final LiveData<List<Income>> allIncomes;

    public LiveData<List<Income>> getAllIncomes() {
        return allIncomes;
    }

    public static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public Income getIncomeById(int id) {
        try {
            return executor.submit(() ->
                    mIncomeDao.getIncomeById(id)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateIncome(Income income) {
        executor.execute(() -> mIncomeDao.updateIncome(income));
    }

    public IncomeRepository(Context context) {
        FinanceManagerRoomDb db = FinanceManagerRoomDb.getDatabase(context);
        mIncomeDao = db.incomeDao();
        allIncomes = mIncomeDao.getAllIncomes();
    }

    public void insertIncome(Income income) {
        executor.execute(() -> mIncomeDao.insertIncome(income));
    }

    public int getLastEntry() {
        try {
            return executor.submit(mIncomeDao::getLastEntry).get();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
