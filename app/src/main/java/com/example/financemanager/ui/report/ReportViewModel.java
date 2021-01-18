package com.example.financemanager.ui.report;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.repository.AmountRepository;
import com.example.financemanager.repository.ExpenditureRepository;

import java.util.List;

public class ReportViewModel extends AndroidViewModel {

    private final LiveData<List<Expenditure>> allTenExpenditures;
    private final LiveData<Integer> mTotalExpenseForMonth;
    private final LiveData<Integer> nTotalIncomeForMonth;
    private final LiveData<Integer> mTotalIncome;
    private final LiveData<Integer> mTotalExpense;


    public ReportViewModel(@NonNull Application application) {
        super(application);
        ExpenditureRepository expenditureRepository = new ExpenditureRepository(application);
        AmountRepository amountRepository = new AmountRepository(application);
        allTenExpenditures = expenditureRepository.getLatestTenExpenditure();
        mTotalExpenseForMonth = amountRepository.getTotalExpenseMonth();
        mTotalIncome = amountRepository.getTotalIncome();
        mTotalExpense = amountRepository.getTotalExpense();
        nTotalIncomeForMonth = amountRepository.getTotalIncomeMonth();
    }

    public LiveData<List<Expenditure>> getAllExpenditures() {
        return allTenExpenditures;
    }

    public LiveData<Integer> getTotalIncome() {
        return mTotalIncome;
    }

    public LiveData<Integer> getTotalExpenseForMonth() { return mTotalExpenseForMonth; }

    public LiveData<Integer> getTotalExpense() {
        return mTotalExpense;
    }

    public LiveData<Integer> getnTotalIncomeForMonth() { return nTotalIncomeForMonth; }
}

