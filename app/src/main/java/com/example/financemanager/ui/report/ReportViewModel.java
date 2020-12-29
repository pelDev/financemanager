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

    private LiveData<List<Expenditure>> allExpenditures;
    private LiveData<Integer> mTotalExpenseForMonth;
    private LiveData<Integer> nTotalIncomeForMonth;
    private LiveData<Integer> mTotalIncome;
    private LiveData<Integer> mTotalExpense;


    public ReportViewModel(@NonNull Application application) {
        super(application);
        ExpenditureRepository expenditureRepository = new ExpenditureRepository(application);
        AmountRepository amountRepository = new AmountRepository(application);
        allExpenditures = expenditureRepository.getAllExpenditures();
        mTotalExpenseForMonth = amountRepository.getTotalExpenseMonth();
        mTotalIncome = amountRepository.getTotalIncome();
        mTotalExpense = amountRepository.getTotalExpense();
        nTotalIncomeForMonth = amountRepository.getTotalIncomeMonth();
    }

    public LiveData<List<Expenditure>> getAllExpenditures() {
        return allExpenditures;
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

