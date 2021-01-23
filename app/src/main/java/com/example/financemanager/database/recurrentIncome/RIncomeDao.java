package com.example.financemanager.database.recurrentIncome;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface RIncomeDao {
    @Insert
    void insert(RecurrentIncome recurrentIncome);

    @Query("SELECT * FROM recurrent_income WHERE workerName = :name LIMIT 1")
    RecurrentIncome getRIncomeByName(String name);

    @Update
    void update(RecurrentIncome recurrentIncome);

    @Delete
    void delete(RecurrentIncome recurrentIncome);
}
