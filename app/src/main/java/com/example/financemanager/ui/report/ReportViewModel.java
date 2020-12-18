package com.example.financemanager.ui.report;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.repository.ExpenditureRepository;

import java.util.List;

public class ReportViewModel extends AndroidViewModel {

    private ExpenditureRepository mExpenditureRepository;
    private LiveData<List<Expenditure>> allExpenditures;
    private MutableLiveData<List<Expenditure>> searchResults;

    public ReportViewModel(@NonNull Application application) {
        super(application);
        mExpenditureRepository = new ExpenditureRepository(application);
        allExpenditures = mExpenditureRepository.getAllExpenditures();
        searchResults = mExpenditureRepository.getSearchResults();
    }

    public void insertExpenditure(Expenditure expenditure) {
        mExpenditureRepository.insertExpenditure(expenditure);
    }

    public LiveData<List<Expenditure>> getAllExpenditures() {
        return allExpenditures;
    }
}

