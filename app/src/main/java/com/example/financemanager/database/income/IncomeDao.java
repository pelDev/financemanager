package com.example.financemanager.database.income;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface IncomeDao {

    @Insert
    void insertIncome(Income income);

    @Query("SELECT * FROM incomes")
    LiveData<List<Income>> getAllIncomes();

}
