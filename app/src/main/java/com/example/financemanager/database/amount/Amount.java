package com.example.financemanager.database.amount;

public class Amount {
    public int totalIncome;
    public int totalExpenses;

    public int getCurrentAmount() {
        return totalIncome - totalExpenses;
    }
}
