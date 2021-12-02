package com.example.financemanager.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.financemanager.database.FinanceManagerRoomDb;
import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.database.expense.ExpenditureDao;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenditureRepository {

    private final ExpenditureDao mExpenditureDao;

    private final LiveData<List<Expenditure>> allExpenditures;

    private final LiveData<List<Expenditure>> latestTenExpenditure;

    public LiveData<List<Expenditure>> getLatestTenExpenditure() {
        return latestTenExpenditure;
    }

    public LiveData<List<Expenditure>> getAllExpenditures() { return allExpenditures; }

    private final MutableLiveData<List<Expenditure>> expenseSearchResults = new MutableLiveData<>();

    public MutableLiveData<List<Expenditure>> getSearchResults() { return expenseSearchResults; }

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ExpenditureRepository(Context context) {
        FinanceManagerRoomDb db =
                FinanceManagerRoomDb.getDatabase(context);
        mExpenditureDao = db.expenditureDao();
        allExpenditures = mExpenditureDao.getAllExpenditures();
        latestTenExpenditure = mExpenditureDao.get10Expenditures();
    }

    public void insertExpenditure(Expenditure newExpenditure) {
        executor.execute(() -> mExpenditureDao.insertExpenditure(newExpenditure));
    }

    public void deleteExpenditure(int id) {
        executor.execute(() -> mExpenditureDao.deleteExpenditure(id));
    }

    public void findExpenditure(String expenditureName) {
        try {
            List<Expenditure> searchResults =
                    executor.submit(() -> mExpenditureDao
                            .findExpenditureByName(expenditureName)).get();
            expenseSearchResults.setValue(searchResults);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
