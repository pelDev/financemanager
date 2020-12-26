package com.example.financemanager.database.expense;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses")
public class Expenditure {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "expenseId")
    private int id;
    @ColumnInfo(name = "category")
    private String category;
    @ColumnInfo(name = "expenseName")
    private String name;
    @ColumnInfo(name = "expenseDescription")
    private String description;
    @ColumnInfo(name = "expenseDay")
    private int day;
    @ColumnInfo(name = "expenseMonth")
    private String month;
    @ColumnInfo(name = "expenseYear")
    private int year;
    @ColumnInfo(name = "expenseAmount")
    private int amount;

    public Expenditure(String category, String name, String description,
                       int day, String month, int year, int amount) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.day = day;
        this.month = month;
        this.year = year;
        this.amount = amount;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
