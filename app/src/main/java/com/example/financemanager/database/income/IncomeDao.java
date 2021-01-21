package com.example.financemanager.database.income;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface IncomeDao {

    @Insert
    void insertIncome(Income income);

    @Query("SELECT * FROM incomes ORDER BY incomeYear DESC, incomeMonthInt DESC, incomeDay DESC")
    LiveData<List<Income>> getAllIncomes();

    @Query("DELETE FROM incomes")
    void deleteAllIncomes();

    @Query("SELECT * FROM incomes WHERE incomeId = :id LIMIT 1")
    Income getIncomeById(int id);

    @Update
    void updateIncome(Income income);

}
