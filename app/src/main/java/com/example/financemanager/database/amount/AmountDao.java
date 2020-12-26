package com.example.financemanager.database.amount;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface AmountDao {

    @Query("SELECT COALESCE(SUM(incomeAmount), 0) as totalIncome FROM incomes")
    LiveData<Integer> getTotalIncome();

    @Query("SELECT COALESCE(SUM(expenseAmount), 0) as totalExpense FROM expenses")
    LiveData<Integer> getTotalExpense();

    @Query("SELECT COALESCE(SUM(expenseAmount), 0) as totalExpense FROM expenses WHERE expenseMonth = :month AND " +
            "expenseYear = :year")
    LiveData<Integer> getTotalExpenseForMonth(String month, int year);

    @Query("SELECT COALESCE(SUM(incomeAmount), 0) as totalIncome FROM incomes WHERE incomeMonth = :month AND " +
            "incomeYear = :year")
    LiveData<Integer> getTotalIncomeForYear(String month, int year);

    //"(SELECT COALESCE(SUM(expenseAmount), 0) as totalExpenses FROM expenses)"
}
