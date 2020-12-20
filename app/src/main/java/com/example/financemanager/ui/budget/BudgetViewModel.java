package com.example.financemanager.ui.budget;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.financemanager.database.budget.Budget;
import com.example.financemanager.repository.BudgetRepository;

import java.util.List;

public class BudgetViewModel extends AndroidViewModel {

    private LiveData<List<Budget>> allBudgets;

    public BudgetViewModel(@NonNull Application application) {
        super(application);
        BudgetRepository repository = new BudgetRepository(application);
        allBudgets = repository.getAllBudgets();
    }

    public LiveData<List<Budget>> getAllBudgets() {
        return allBudgets;
    }
}