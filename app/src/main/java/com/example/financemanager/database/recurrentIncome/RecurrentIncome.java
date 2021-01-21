package com.example.financemanager.database.recurrentIncome;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recurrent_income")
public class RecurrentIncome {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "incomeId")
    private int id;

    @NonNull
    private String workerName;

    @NonNull
    private int daysPassed;

    public int getDaysPassed() {
        return daysPassed;
    }

    public void setDaysPassed(int daysPassed) {
        this.daysPassed = daysPassed;
    }

    public RecurrentIncome(@NonNull String workerName, int daysPassed) {
        this.workerName = workerName;
        this.daysPassed = daysPassed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(@NonNull String workerName) {
        this.workerName = workerName;
    }
}
