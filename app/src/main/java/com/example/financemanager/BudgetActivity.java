package com.example.financemanager;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.FinanceManagerProviderContract.Budgets;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BudgetActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerBudgets;
    private FinanceManagerOpenHelper mDbHelper;
    private BudgetRecyclerAdapter mBudgetRecyclerAdapter;
    private static final int LOADER_BUDGETS = 0;
    private static final int LOADER_EXPENDITURE = 1;
    private Cursor mBudgetCursor;
    private int mDay;
    private String mMonthName;
    private int mYear;
    private TextView mEmptyRecycler;
    private PieChart mPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget);

//        Window window = BudgetActivity.this.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.setStatusBarColor(ContextCompat.getColor(this, R.color.bckGround));

        mDbHelper = new FinanceManagerOpenHelper(this);
        // get the current date
        Calendar calendar = Calendar.getInstance();
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        mMonthName = getMonthFromInt(month);
        mYear = calendar.get(Calendar.YEAR);

        ImageView buttonMore = findViewById(R.id.button_more);

        buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });
        TextView title = findViewById(R.id.budget_list_title);
        title.setText(getTitleText());

        mRecyclerBudgets = findViewById(R.id.recycler_budgets);
        mEmptyRecycler = findViewById(R.id.empty_view_income);

        mPieChart = findViewById(R.id.piechart);

        initializeDisplayContent();

    }

    private void openBottomSheet() {
        BottomDialogFragmentBudget bottomDialogFragmentBudget =
                BottomDialogFragmentBudget.newInstance(this);
        bottomDialogFragmentBudget.show(getSupportFragmentManager(), BottomDialogFragmentBudget.TAG);
    }

    private String getTitleText() {
        return "Budget for " + mMonthName + ", " + mYear;
    }

    private String getMonthFromInt(int month) {
        String monthString = "";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (month >= 0 && month <= 11) {
            monthString = months[month];
        }
        return monthString;
    }

    private void initializeDisplayContent() {
        // Set up the Recycler View
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerBudgets.setLayoutManager(layoutManager);
        mBudgetRecyclerAdapter = new BudgetRecyclerAdapter(this, null);
        mRecyclerBudgets.setAdapter(mBudgetRecyclerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(LOADER_BUDGETS, null, this);
    }

    public void gotToAddBudget(View view) {
        // Navigate to Add Budget Screen
        startActivity(new Intent(this, AddBudgetActivity.class));
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_BUDGETS) {
            loader = createLoaderBudgets();
        }
        return loader;
    }


    private CursorLoader createLoaderBudgets() {
                Uri uri = Budgets.CONTENT_URI;
                String[] columns = {
                        Budgets.COLUMN_BUDGET_CATEGORY,
                        Budgets.COLUMN_BUDGET_AMOUNT,
                        Budgets.COLUMN_BUDGET_AMOUNT_SPENT,
                        Budgets._ID,
                };
                String selection = Budgets.COLUMN_BUDGET_MONTH + " = ? AND " + Budgets.COLUMN_BUDGET_YEAR + " = ?";
                String[] selectionArgs = {mMonthName, Integer.toString(mYear)};
                return new CursorLoader(this, uri, columns, selection, selectionArgs, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if (id == LOADER_BUDGETS) {
            if (data.getCount() != 0) {
                mRecyclerBudgets.setVisibility(View.VISIBLE);
                mEmptyRecycler.setVisibility(View.GONE);
                initializePieChart(data);
            } else {
                mRecyclerBudgets.setVisibility(View.GONE);
                mEmptyRecycler.setVisibility(View.VISIBLE);
                mPieChart.setVisibility(View.GONE);
            }
            mBudgetCursor = data;
            mBudgetRecyclerAdapter.changeCursor(mBudgetCursor);
        }
    }

    public void setNewMonth(String month) {
        if (!month.equals(mMonthName)) {
            mMonthName = month;
            getLoaderManager().restartLoader(LOADER_BUDGETS, null, this);
        }
    }

    private void initializePieChart(final Cursor cursor) {
        AsyncTask task = new AsyncTask() {

            private List<PieEntry> mPieEntries;

            @Override
            protected Object doInBackground(Object[] objects) {
                mPieEntries = new ArrayList<>();
                if (cursor != null && cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    int budgetAmountPos = cursor.getColumnIndex(Budgets.COLUMN_BUDGET_AMOUNT);
                    int budgetCategoryPos = cursor.getColumnIndex(Budgets.COLUMN_BUDGET_CATEGORY);
                    while (!cursor.isAfterLast()) {
                        double amount = Double.parseDouble(cursor.getString(budgetAmountPos));
                        String label = cursor.getString(budgetCategoryPos);
                        mPieEntries.add(new PieEntry((float) amount, label));
                        cursor.moveToNext();
                    }


                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                mPieChart.setVisibility(View.VISIBLE);
                mPieChart.animateXY(3000, 3000);

                PieDataSet pieDataSet = new PieDataSet(mPieEntries, "Budget");
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

                PieData pieData = new PieData(pieDataSet);
                mPieChart.setData(pieData);
                Description description = new Description();
                description.setText("Budget");
                mPieChart.invalidate();
                super.onPostExecute(o);
            }
        };
        task.execute();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_BUDGETS) {
            if (mBudgetCursor != null)
                mBudgetCursor.close();
        }
    }

    public void doNothing(View view) {
    }
}