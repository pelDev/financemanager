package com.example.financemanager.database.income;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "incomes")
public class Income {

    public Income(int day, String month, int year, int amount) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.amount = amount;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @ColumnInfo(name = "incomeDay")
    private int day;

    @ColumnInfo(name = "incomeMonth")
    private String month;

    @ColumnInfo(name = "incomeYear")
    private int year;

    @ColumnInfo(name = "incomeAmount")
    private int amount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "incomeId")
    private int id;


}
