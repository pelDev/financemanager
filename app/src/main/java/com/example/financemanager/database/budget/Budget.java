package com.example.financemanager.database.budget;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets")
public class Budget {

    @ColumnInfo(name = "category")
    private String category;

    @ColumnInfo(name = "budgetAmount")
    private int amount;

    @ColumnInfo(name = "budgetDay")
    private int day;

    @ColumnInfo(name = "budgetMonth")
    private String month;

    @ColumnInfo(name = "budgetMonthInt")
    private int monthInt;

    @ColumnInfo(name = "budgetYear")
    private int year;

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "budgetId")
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public Budget(String category, int amount, int day, String month, int monthInt, int year) {
        this.category = category;
        this.amount = amount;
        this.day = day;
        this.monthInt = monthInt;
        this.month = month;
        this.year = year;
    }

    private int amountSpent;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonthInt() {
        return monthInt;
    }

    public void setMonthInt(int monthInt) {
        this.monthInt = monthInt;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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

    public int getId() {
        return id;
    }

    public int getAmountSpent() {
        return amountSpent;
    }

    public void setAmountSpent(int amountSpent) {
        this.amountSpent = amountSpent;
    }
}
