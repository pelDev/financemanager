package com.example.financemanager.database.budget;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BudgetDao {

    @Query("SELECT *,(SELECT COALESCE(SUM(e.expenseAmount), 0) FROM expenses e WHERE b.category = e.category) " +
            "AS amountSpent FROM budgets b")
    LiveData<List<Budget>> getBudgetList();

    @Insert
    void insertBudget(Budget budget);
}
