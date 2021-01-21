package com.example.financemanager.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.financemanager.database.amount.AmountDao;
import com.example.financemanager.database.budget.Budget;
import com.example.financemanager.database.budget.BudgetDao;
import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.database.expense.ExpenditureDao;
import com.example.financemanager.database.income.Income;
import com.example.financemanager.database.income.IncomeDao;
import com.example.financemanager.database.recurrentIncome.RecurrentIncome;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Expenditure.class, Budget.class, Income.class, RecurrentIncome.class}, version = 1, exportSchema = false)
public abstract class FinanceManagerRoomDb extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;

    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            Income income = new Income(12, "October", 9, 2020,
                    40000, false);

            Income income1 = new Income(1, "December", 11, 2020,
                    2900, false);

            Expenditure expenditure1 = new Expenditure("Food", "Akpu", "",
                    10, "October", 9, 2020,  2000);

            Expenditure expenditure2 = new Expenditure("Housing", "Rent", "",
                    13, "September", 8, 2020, 6900);

            Expenditure expenditure3 = new Expenditure("Education", "School Fees", "",
                    20, "December", 11, 2019, 2000);

            Expenditure expenditure4 = new Expenditure("Food", "Okro", "",
                    20, "December", 11, 2020, 200);

            Budget budget1 = new Budget("Food", 1000, 10, "December", 11, 2020);

            Budget budget2 = new Budget("Food", 10000, 2, "October", 9, 2019);

            databaseWriteExecutor.execute(() -> {

                IncomeDao incomeDao = INSTANCE.incomeDao();
                ExpenditureDao expenditureDao = INSTANCE.expenditureDao();
                BudgetDao budgetDao = INSTANCE.budgetDao();

                incomeDao.deleteAllIncomes();
                incomeDao.insertIncome(income);
                incomeDao.insertIncome(income1);

                budgetDao.deleteAllBudgets();
                budgetDao.insertBudget(budget1);
                budgetDao.insertBudget(budget2);

                expenditureDao.deleteAllExpenses();
                expenditureDao.insertExpenditure(expenditure1);
                expenditureDao.insertExpenditure(expenditure2);
                expenditureDao.insertExpenditure(expenditure3);
                expenditureDao.insertExpenditure(expenditure4);

            });
        }
    };

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
                                    .addCallback(roomCallback)
                                    .build();
                }
            }
        }
        return INSTANCE;
    }

}
