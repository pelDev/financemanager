package com.example.financemanager.database.expense;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
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

    @Query("SELECT * FROM expenses")
    LiveData<List<Expenditure>> getAllExpenditures();

    @Query("SELECT * FROM expenses WHERE expenseName = :name")
    List<Expenditure> findExpenditureByName(String name);

}
