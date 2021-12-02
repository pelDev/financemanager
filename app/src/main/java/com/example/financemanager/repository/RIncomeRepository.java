package com.example.financemanager.repository;

import android.content.Context;

import com.example.financemanager.database.FinanceManagerRoomDb;
import com.example.financemanager.database.recurrentIncome.RIncomeDao;
import com.example.financemanager.database.recurrentIncome.RecurrentIncome;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RIncomeRepository {

    public RIncomeRepository(Context context) {
        FinanceManagerRoomDb db = FinanceManagerRoomDb
                .getDatabase(context);
        mRIncomeDao = db.rIncomeDao();
    }

    private final RIncomeDao mRIncomeDao;

    public static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public RecurrentIncome getRIncome(String name) {
        try {
            return executor.submit(() ->
                    mRIncomeDao.getRIncomeByName(name)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateRIncome(RecurrentIncome rIncome) {
        executor.execute(() -> mRIncomeDao.update(rIncome));
    }

    public void deleteRIncome(RecurrentIncome rIncome) {
        executor.execute(() -> mRIncomeDao.delete(rIncome));
    }

    public void insertRIncome(RecurrentIncome recurrentIncome) {
        executor.execute(() -> mRIncomeDao.insert(recurrentIncome));
    }

}
