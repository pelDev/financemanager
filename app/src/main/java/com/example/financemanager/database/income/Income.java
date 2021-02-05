package com.example.financemanager.database.income;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "incomes")
public class Income {

    public Income(String name, int day, String month, int monthInt,
                  int year, int amount, boolean isRecurrent) {
        this.name = name;
        this.day = day;
        this.month = month;
        this.monthInt = monthInt;
        this.year = year;
        this.amount = amount;
        this.isRecurrent = isRecurrent;
    }

    public int getDay() {
        return day;
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
    private final int day;

    @ColumnInfo(name = "incomeMonth")
    private String month;

    @ColumnInfo(name = "incomeMonthInt")
    private final int monthInt;

    @ColumnInfo(name = "incomeName")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ColumnInfo(name = "incomeYear")
    private int year;

    @ColumnInfo(name = "incomeAmount")
    private int amount;

    private boolean isRecurrent;

    public boolean isRecurrent() {
        return isRecurrent;
    }

    public void setRecurrent(boolean recurrent) {
        isRecurrent = recurrent;
    }

    public int getMonthInt() {
        return monthInt;
    }

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
