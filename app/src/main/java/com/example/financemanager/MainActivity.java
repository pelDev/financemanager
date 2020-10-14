package com.example.financemanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.financemanager.ExpenditureDatabaseContract.ExpenditureInfoEntry;
import com.example.financemanager.ExpenditureDatabaseContract.IncomeInfoEntry;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // home screen

    private AppBarConfiguration mAppBarConfiguration;
    private ExpenditureOpenHelper mDbOpenHelper;
    private RecyclerView mRecyclerExpenditure;
    private LinearLayoutManager mExpenditureLayoutManager;
    private ExpenditureRecyclerAdapter mExpenditureRecyclerAdapter;
    private ArrayList<BarEntry> mDataValue1;
    private DrawerLayout mDrawer;
    private Cursor mIncomeCursor;
    private int mIncomeAmountPos;
    private int mIncomeMonthPos;
    private int mIncomeYearPos;
    private Cursor mExpenditureCursorForTotal;
    private int mExpenditureAmountPos;
    private int mExpenditureMonthPos;
    private View mIncomeBar;
    private int mTotalIncome = 0;
    private ViewGroup.LayoutParams mIncomeBarLayoutParamstParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        mDbOpenHelper = new ExpenditureOpenHelper(this);
        mDataValue1 = new ArrayList<>();
        mIncomeBar = (View) findViewById(R.id.view);
        mIncomeBarLayoutParamstParams = (ViewGroup.LayoutParams) mIncomeBar.getLayoutParams();

        Window window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, BudgetActivity.class));
            }
        });
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(mDrawer)
                .build();
        initializeDisplayContent();
    }

    private void initializeDisplayContent() {
        DataManager.loadFromDatabase(mDbOpenHelper);
        mRecyclerExpenditure = (RecyclerView) findViewById(R.id.list_expenditure);
        mExpenditureLayoutManager = new LinearLayoutManager(this);

        mExpenditureRecyclerAdapter = new ExpenditureRecyclerAdapter(this, null);
        displayExpenditures();
    }



    private void displayExpenditures() {
        mRecyclerExpenditure.setLayoutManager(mExpenditureLayoutManager);
        mRecyclerExpenditure.setAdapter(mExpenditureRecyclerAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // get all sets of data from database
        loadExpenditureInBackground();
    }

    private void loadExpenditureInBackground() {
        AsyncTaskLoader taskLoader = new AsyncTaskLoader(this) {
            @Nullable
            @Override
            public Object loadInBackground() {
                loadExpenditures();
                loadIncomes();
                return null;
            }
        };
        taskLoader.loadInBackground();
    }

    private void loadExpenditures() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        final String[] expenditureColumns = {
                ExpenditureInfoEntry.COLUMN_EXPENDITURE_NAME,
                ExpenditureInfoEntry.COLUMN_EXPENDITURE_DAY,
                ExpenditureInfoEntry.COLUMN_EXPENDITURE_MONTH,
                ExpenditureInfoEntry.COLUMN_EXPENDITURE_YEAR,
                ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT,
                ExpenditureInfoEntry.COLUMN_EXPENDITURE_ID,
                ExpenditureInfoEntry._ID
        };
        //String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE + ", " + NoteInfoEntry.COLUMN_NOTE_TITLE;

        final Cursor expenditureCursor = db.query(ExpenditureInfoEntry.TABLE_NAME, expenditureColumns, null,
                null, null, null, null);
        mExpenditureRecyclerAdapter.changeCursor(expenditureCursor);

        final String[] expenditureColumnsForTotal = {
                ExpenditureInfoEntry.COLUMN_EXPENDITURE_MONTH,
                ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT,
        };

        String selection = ExpenditureInfoEntry.COLUMN_EXPENDITURE_MONTH + " = ?";
        String[] selectionArgs = {"January"};
        mExpenditureCursorForTotal = db.query(ExpenditureInfoEntry.TABLE_NAME, expenditureColumnsForTotal,
                selection, selectionArgs, null, null, null);

        if (mExpenditureCursorForTotal != null)
            getExpenditureTotal();
    }

    private void getExpenditureTotal() {
        populateExpenditureColumnPosition();

        int totalExpenditure = 0;
        mExpenditureCursorForTotal.moveToFirst();
        while (mExpenditureCursorForTotal.isAfterLast() == false) {
            int amount = mExpenditureCursorForTotal.getInt(mExpenditureAmountPos);
            totalExpenditure = totalExpenditure + amount;
            mExpenditureCursorForTotal.moveToNext();
        }
        Log.i("Income", "Total Expenditure " + totalExpenditure);
    }

    private void populateExpenditureColumnPosition() {
        if (mExpenditureCursorForTotal != null) {
            // get column position for expenditure_amount in the table
            mExpenditureAmountPos = mExpenditureCursorForTotal.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT);

            // get column position for expenditure_month in the table
            mExpenditureMonthPos = mExpenditureCursorForTotal.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_MONTH);

        }
    }

    private void loadIncomes() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        final String[] incomeColumns = {
                IncomeInfoEntry.COLUMN_INCOME_AMOUNT,
                IncomeInfoEntry.COLUMN_INCOME_DAY,
                IncomeInfoEntry.COLUMN_INCOME_MONTH,
                IncomeInfoEntry.COLUMN_INCOME_YEAR
        };

        String selection = IncomeInfoEntry.COLUMN_INCOME_MONTH + " = ?";
        String[] selectionArgs = {"January"};

        mIncomeCursor = db.query(IncomeInfoEntry.TABLE_NAME, incomeColumns, selection, selectionArgs,
                null, null, null);

        if (mIncomeCursor.getCount() != 0)
        getIncomeTotal();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void openDrawer(View view) {
        mDrawer.open();
    }

    public void getIncomeTotal() {
        populateIncomeColumnPosition();

        mIncomeCursor.moveToFirst();
        while (mIncomeCursor.isAfterLast() == false) {
            int amount = mIncomeCursor.getInt(mIncomeAmountPos);
            mTotalIncome = mTotalIncome + amount;
            mIncomeCursor.moveToNext();
        }
        if(mTotalIncome > 0) {
            mIncomeBarLayoutParamstParams.height = dpToPx(10);
            mIncomeBar.setLayoutParams(mIncomeBarLayoutParamstParams);
        }
        Log.i("Income", "Total Income" + mTotalIncome);
    }

    private void populateIncomeColumnPosition() {
        if(mIncomeCursor != null) {
            // get column position for income_amount in the table
            mIncomeAmountPos = mIncomeCursor.getColumnIndex(IncomeInfoEntry.COLUMN_INCOME_AMOUNT);

            // get column position for income_month in the table
            mIncomeMonthPos = mIncomeCursor.getColumnIndex(IncomeInfoEntry.COLUMN_INCOME_MONTH);

            // get column position for income_year in the table
            mIncomeYearPos = mIncomeCursor.getColumnIndex(IncomeInfoEntry.COLUMN_INCOME_YEAR);
        }
    }

    public final int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

}