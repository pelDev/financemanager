package com.example.financemanager.database.amount;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface AmountDao {

    @Query("SELECT COALESCE(SUM(incomeAmount), 0) as totalIncome FROM incomes")
    LiveData<Integer> getTotalIncome();

    @Query("SELECT COALESCE(SUM(expenseAmount), 0) as totalExpense FROM expenses")
    LiveData<Integer> getTotalExpense();

    //"(SELECT COALESCE(SUM(expenseAmount), 0) as totalExpenses FROM expenses)"
}
