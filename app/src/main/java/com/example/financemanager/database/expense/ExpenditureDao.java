package com.example.financemanager.database.expense;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.financemanager.database.budget.Budget;

import java.util.List;

@Dao
public interface ExpenditureDao {

    @Insert
    void insertExpenditure(Expenditure expenditure);

    @Query("SELECT * FROM expenses WHERE expenseId = :id")
    Expenditure findExpenditureById(Integer id);

    @Query("DELETE FROM expenses WHERE expenseId = :id")
    void deleteExpenditure(Integer id);

    @Query("SELECT * FROM expenses ORDER BY expenseYear DESC, expenseMonthInt DESC, expenseDay DESC")
    LiveData<List<Expenditure>> getAllExpenditures();

    @Query("SELECT * FROM expenses ORDER BY expenseYear DESC, expenseMonthInt DESC, expenseDay DESC LIMIT 10")
    LiveData<List<Expenditure>> get10Expenditures();

    @Query("SELECT * FROM expenses WHERE expenseName = :name")
    List<Expenditure> findExpenditureByName(String name);

    @Query("DELETE FROM expenses")
    void deleteAllExpenses();
}
