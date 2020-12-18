package com.example.financemanager.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.database.expense.ExpenditureDao;

@Database(entities = {Expenditure.class}, version = 1)
public abstract class FinanceManagerRoomDb extends RoomDatabase {

    public abstract ExpenditureDao expenditureDao();

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
