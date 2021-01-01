package com.example.financemanager.database.budget;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BudgetDao {

    @Query("SELECT *,(SELECT COALESCE(SUM(e.expenseAmount), 0) FROM expenses e WHERE b.category = e.category AND " +
            "e.expenseMonth = :month AND e.expenseYear = :year) " +
            "AS amountSpent FROM budgets b WHERE b.budgetMonth = :month AND b.budgetYear = :year ORDER BY " +
            "budgetYear ASC , budgetMonthInt ASC, budgetDay DESC")
    LiveData<List<Budget>> getBudgetList(String month, int year);

    @Query("SELECT COUNT(*) FROM budgets WHERE category = :category AND budgetMonth = :month AND budgetYear = :year")
    int getBudgetCountForCategoryAndTime(String category, String month, int year);

    @Insert
    void insertBudget(Budget budget);

    @Query("DELETE FROM budgets")
    void deleteAllBudgets();
}
