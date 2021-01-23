package com.example.financemanager;

import android.util.Log;

import androidx.work.Data;

import java.text.DateFormatSymbols;

import static com.example.financemanager.Constants.INCOME_WORKER_NAME;
import static com.example.financemanager.Constants.KEY_WORKER_NAME;

public class Shared {
    public static String getMonthFromInt(int month) {
        String monthString = "";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (month >= 0 && month <= 11) {
            monthString = months[month];
        }
        return monthString;
    }

    public static Data createInputDataForWorkName(int id) {
        Data.Builder builder = new Data.Builder();
        builder.putString(KEY_WORKER_NAME, INCOME_WORKER_NAME + id);
        return builder.build();
    }

    public static void logInfo(String tag, String s) {
        Log.i(tag, s);
    }
}
