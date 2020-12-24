package com.example.financemanager.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.financemanager.database.amount.AmountDao;
import com.example.financemanager.database.budget.Budget;
import com.example.financemanager.database.budget.BudgetDao;
import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.database.expense.ExpenditureDao;
import com.example.financemanager.database.income.Income;
import com.example.financemanager.database.income.IncomeDao;

@Database(entities = {Expenditure.class, Budget.class, Income.class}, version = 1, exportSchema = false)
public abstract class FinanceManagerRoomDb extends RoomDatabase {

    public abstract ExpenditureDao expenditureDao();

    public abstract BudgetDao budgetDao();

    public abstract IncomeDao incomeDao();

    public abstract AmountDao amountDao();

    private static FinanceManagerRoomDb INSTANCE;

    public static FinanceManagerRoomDb getDatabase(final Context context) {

        if (INSTANCE == null) {
            synchronized (FinanceManagerRoomDb.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    FinanceManagerRoomDb.class,
                                    "finance_manager_database")
                                    .build();
                }
            }
        }

        return INSTANCE;

    }

}
