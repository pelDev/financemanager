package com.example.financemanager.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.financemanager.database.FinanceManagerRoomDb;
import com.example.financemanager.database.amount.AmountDao;

public class AmountRepository {

    private final AmountDao mAmountDao;
    private LiveData<Integer> totalIncome;
    private LiveData<Integer> totalExpense;

    public AmountRepository(Application application) {
        FinanceManagerRoomDb db = FinanceManagerRoomDb.getDatabase(application);
        mAmountDao = db.amountDao();
        totalIncome = mAmountDao.getTotalIncome();
        totalExpense = mAmountDao.getTotalExpense();
    }

    public LiveData<Integer> getTotalIncome() {
        return totalIncome;
    }

    public LiveData<Integer> getTotalExpense() {
        return totalExpense;
    }
}
