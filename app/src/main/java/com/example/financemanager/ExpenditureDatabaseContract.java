package com.example.financemanager;

import android.provider.BaseColumns;

public final class ExpenditureDatabaseContract {

    private ExpenditureDatabaseContract() {};

    public static final class ExpenditureInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "expenditure_info";
        public static final String COLUMN_EXPENDITURE_ID = "expenditure_id";
        public static final String COLUMN_EXPENDITURE_NAME = "expenditure_name";
        public static final String COLUMN_EXPENDITURE_TIMESTAMP = "expenditure_timestamp";
        public static final String COLUMN_EXPENDITURE_AMOUNT = "expenditure_amount";

        // create index expenditure_info_index1 ON expenditure_info (expenditure_name)
        public static final String INDEX1 = TABLE_NAME + "_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1 + " ON " + TABLE_NAME + " (" + COLUMN_EXPENDITURE_NAME + ")";

        public static final String getQName(String columnName) {
            return TABLE_NAME + "." + columnName;
        }


        // CREATE TABLE course_info (course_id, course_title)
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_EXPENDITURE_ID + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_EXPENDITURE_NAME + " TEXT NOT NULL, " +
                        COLUMN_EXPENDITURE_TIMESTAMP + " TEXT NOT NULL, " +
                        COLUMN_EXPENDITURE_AMOUNT + " INTEGER)";
    }

}
