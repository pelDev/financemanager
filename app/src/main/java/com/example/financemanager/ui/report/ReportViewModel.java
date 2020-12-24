package com.example.financemanager.ui.report;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.repository.AmountRepository;
import com.example.financemanager.repository.ExpenditureRepository;

import java.util.List;

public class ReportViewModel extends AndroidViewModel {

    private ExpenditureRepository mExpenditureRepository;
    private LiveData<List<Expenditure>> allExpenditures;
    private MutableLiveData<List<Expenditure>> searchResults;
    private LiveData<Integer> mTotalIncome;
    private LiveData<Integer> mTotalExpense;
    private LiveData<Integer> mCurrentAmount;

    public ReportViewModel(@NonNull Application application) {
        super(application);
        mExpenditureRepository = new ExpenditureRepository(application);
        AmountRepository amountRepository = new AmountRepository(application);
        allExpenditures = mExpenditureRepository.getAllExpenditures();
        searchResults = mExpenditureRepository.getSearchResults();
        mTotalIncome = amountRepository.getTotalIncome();
        mTotalExpense = amountRepository.getTotalExpense();
    }

    public LiveData<List<Expenditure>> getAllExpenditures() {
        return allExpenditures;
    }

    public LiveData<Integer> getTotalIncome() {
        return mTotalIncome;
    }

    public LiveData<Integer> getTotalExpense() {
        return mTotalExpense;
    }
}

