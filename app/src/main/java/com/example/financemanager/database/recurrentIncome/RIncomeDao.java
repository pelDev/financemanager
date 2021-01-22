package com.example.financemanager.database.recurrentIncome;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface RIncomeDao {
    @Insert
    void insert(RecurrentIncome recurrentIncome);

    @Query("SELECT * FROM recurrent_income WHERE workerName = :name LIMIT 1")
    RecurrentIncome getRIncomeByName(String name);
}
