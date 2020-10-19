package com.example.financemanager;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.financemanager.ExpenditureDatabaseContract.ExpenditureInfoEntry;
import com.example.financemanager.ExpenditureDatabaseContract.IncomeInfoEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
//import androidx.loader.app.LoaderManager;
import android.content.Loader;
import android.app.LoaderManager;
import android.content.CursorLoader;
import androidx.loader.content.AsyncTaskLoader;
//import androidx.loader.content.CursorLoader;
//import androidx.loader.content.Loader;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // home screen

    private ExpenditureOpenHelper mDbOpenHelper;
    private RecyclerView mRecyclerExpenditure;
    private static final int LOADER_INCOME = 0;
    private static final int LOADER_EXPENSE = 1;
    private static final int LOADER_EXPENSE_TOTAL = 2;
    private LinearLayoutManager mExpenditureLayoutManager;
    private ExpenditureRecyclerAdapter mExpenditureRecyclerAdapter;
    private DrawerLayout mDrawer;
    private Cursor mIncomeCursor;
    private int mIncomeAmountPos;
    private Cursor mExpenditureCursorForTotal;
    private int mExpenditureAmountPos;
    private View mIncomeBar;
    private int mTotalIncome = 0;
    private View mExpenditureBar;
    private LinearLayout.LayoutParams mIncomeBarLp;
    private LinearLayout.LayoutParams mExpenditureBarLp;
    private Animation mAnimSlideUp;
    private AsyncTaskLoader mTaskLoader;
    private boolean mIncomeQueryFinished;
    private boolean mExpenseTotalQueryFinished;
    private int mTotalExpenditure;
    private Cursor mExpenditureCursor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        mDbOpenHelper = new ExpenditureOpenHelper(this);
        mExpenditureBar = (View) findViewById(R.id.view2);
        mExpenditureBarLp = (LinearLayout.LayoutParams) mExpenditureBar.getLayoutParams();
        mIncomeBar = (View) findViewById(R.id.view);
        mIncomeBarLp = (LinearLayout.LayoutParams) mIncomeBar.getLayoutParams();
        View rightSpacer = (View) findViewById(R.id.view7);
        LinearLayout.LayoutParams RSLp = (LinearLayout.LayoutParams) rightSpacer.getLayoutParams();
        View middleSpacer = (View) findViewById(R.id.view6);
        LinearLayout.LayoutParams MSLp = (LinearLayout.LayoutParams) middleSpacer.getLayoutParams();
        View leftSpacer = (View) findViewById(R.id.view8);
        LinearLayout.LayoutParams LSLp = (LinearLayout.LayoutParams) leftSpacer.getLayoutParams();
        mAnimSlideUp = AnimationUtils.loadAnimation(this, R.anim.scale);

        RSLp.width = (int) Math.round(getScreenWidth() * 0.1);
        rightSpacer.setLayoutParams(RSLp);

        MSLp.width = (int) Math.round(getScreenWidth() * 0.1);
        middleSpacer.setLayoutParams(MSLp);

        LSLp.width = (int) Math.round(getScreenWidth() * 0.1);
        leftSpacer.setLayoutParams(LSLp);

        Window window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddExpenseActivity.class));
            }
        });
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(mDrawer)
                .build();
        initializeDisplayContent();
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private void initializeDisplayContent() {
        //DataManager.loadFromDatabase(mDbOpenHelper);
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
        mTotalIncome = 0;
        // get all sets of data from database
        getLoaderManager().restartLoader(LOADER_INCOME, null, this);
        getLoaderManager().restartLoader(LOADER_EXPENSE, null, this);
        getLoaderManager().restartLoader(LOADER_EXPENSE_TOTAL, null, this);
    }



    private void getExpenditureTotal() {
        populateExpenditureColumnPosition();

        mTotalExpenditure = 0;
        mExpenditureCursorForTotal.moveToFirst();
        while (!mExpenditureCursorForTotal.isAfterLast()) {
            int amount = mExpenditureCursorForTotal.getInt(mExpenditureAmountPos);
            mTotalExpenditure = mTotalExpenditure + amount;
            mExpenditureCursorForTotal.moveToNext();
        }
    }

    private void setExpenditureBar() {
        float barHeight = ((mTotalExpenditure / (float) mTotalIncome) * 100f);
        Log.i("Income", "Bar Height In dp " + Math.round(barHeight) );
        if (barHeight > 0 && barHeight <= 100) {
            Log.i("Income", "Total Income for calculating Expenditure " + (float) mTotalIncome );
            int height = dpToPx(Math.round(barHeight));
            Log.i("Income", "Bar Height In px " + height );
            setExpenditureBarHeight(height);
        } else if (barHeight > 100) {
            updateExpenditure();
        }
        Log.i("Income", "Total Expenditure " + mTotalExpenditure);
    }

    private void updateExpenditure() {
        mExpenditureBarLp.width = (int)  Math.round(getScreenWidth() * 0.35);
        mExpenditureBarLp.height = dpToPx(100);
        mExpenditureBar.setLayoutParams(mExpenditureBarLp);
        mExpenditureBar.setBackgroundColor(Color.rgb(255, 0, 0));
        mExpenditureBar.startAnimation(mAnimSlideUp);
    }

    private void setExpenditureBarHeight(int height) {
        mExpenditureBarLp.width = (int)  Math.round(getScreenWidth() * 0.35);
        mExpenditureBarLp.height = height;
        mExpenditureBar.setLayoutParams(mExpenditureBarLp);
        mExpenditureBar.startAnimation(mAnimSlideUp);
    }

    private void populateExpenditureColumnPosition() {
        if (mExpenditureCursorForTotal != null) {
            // get column position for expenditure_amount in the table
            mExpenditureAmountPos = mExpenditureCursorForTotal.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT);

            // get column position for expenditure_month in the table
            int expenditureMonthPos = mExpenditureCursorForTotal.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_MONTH);

        }
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

    // get total amount of income received in a particular month from the income table.
    public void getIncomeTotal() {
        populateIncomeColumnPosition();

        // move cursor to the first row
        mIncomeCursor.moveToFirst();

        //check if the cursor has passed the last row of the table
        while (mIncomeCursor.isAfterLast() == false) {
            int amount = mIncomeCursor.getInt(mIncomeAmountPos);
            mTotalIncome = mTotalIncome + amount;
            mIncomeCursor.moveToNext();
        }
    }

    private void setIncomeBar() {
        mIncomeBarLp.width = (int) Math.round(getScreenWidth() * 0.35);
        mIncomeBarLp.height = dpToPx(100);
        mIncomeBar.setLayoutParams(mIncomeBarLp);
        mIncomeBar.startAnimation(mAnimSlideUp);
    }

    // get column positions from the income table
    private void populateIncomeColumnPosition() {
        if(mIncomeCursor != null) {
            // get column position for income_amount in the table
            mIncomeAmountPos = mIncomeCursor.getColumnIndex(IncomeInfoEntry.COLUMN_INCOME_AMOUNT);

            // get column position for income_month in the table
            int incomeMonthPos = mIncomeCursor.getColumnIndex(IncomeInfoEntry.COLUMN_INCOME_MONTH);

            // get column position for income_year in the table
            int incomeYearPos = mIncomeCursor.getColumnIndex(IncomeInfoEntry.COLUMN_INCOME_YEAR);
        }
    }

    // convert dp tp px
    public final int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIncomeCursor.close();
        mExpenditureCursorForTotal.close();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_INCOME) {
            loader = createLoaderIncome();
        } else if (id == LOADER_EXPENSE) {
            loader = createLoaderExpense();
        } else if (id == LOADER_EXPENSE_TOTAL) {
            loader = createLoaderExpenseTotal();
        }
        return loader;
    }

    private CursorLoader createLoaderExpenseTotal() {
        mExpenseTotalQueryFinished = false;
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                String[] expenditureColumnsForTotal = {
                        ExpenditureInfoEntry.COLUMN_EXPENDITURE_MONTH,
                        ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT,
                };

                String selection = ExpenditureInfoEntry.COLUMN_EXPENDITURE_MONTH + " = ?";
                String[] selectionArgs = {"January"};
                return db.query(ExpenditureInfoEntry.TABLE_NAME, expenditureColumnsForTotal,
                        selection, selectionArgs, null, null, null);
            }
        };
    }

    private CursorLoader createLoaderExpense() {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                String[] expenditureColumns = {
                        ExpenditureInfoEntry.COLUMN_EXPENDITURE_NAME,
                        ExpenditureInfoEntry.COLUMN_EXPENDITURE_DAY,
                        ExpenditureInfoEntry.COLUMN_EXPENDITURE_MONTH,
                        ExpenditureInfoEntry.COLUMN_EXPENDITURE_YEAR,
                        ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT,
                        ExpenditureInfoEntry.COLUMN_EXPENDITURE_ID,
                        ExpenditureInfoEntry._ID
                };
                //String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE + ", " + NoteInfoEntry.COLUMN_NOTE_TITLE;

                return db.query(ExpenditureInfoEntry.TABLE_NAME, expenditureColumns, null,
                        null, null, null, null);
            }
        };
    }

    private CursorLoader createLoaderIncome() {
        mIncomeQueryFinished = false;
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                String[] incomeColumns = {
                        IncomeInfoEntry.COLUMN_INCOME_AMOUNT,
                        IncomeInfoEntry.COLUMN_INCOME_DAY,
                        IncomeInfoEntry.COLUMN_INCOME_MONTH,
                        IncomeInfoEntry.COLUMN_INCOME_YEAR
                };

                String selection = IncomeInfoEntry.COLUMN_INCOME_MONTH + " = ?";
                String[] selectionArgs = {"January"};

                return db.query(IncomeInfoEntry.TABLE_NAME, incomeColumns, selection, selectionArgs,
                        null, null, null);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_INCOME) {
            mIncomeCursor = data;
            if (mIncomeCursor.getCount() != 0) {
                getIncomeTotal();
                mIncomeQueryFinished = true;
                setBarWhenQueriesFinished();
            }
        } else if (loader.getId() == LOADER_EXPENSE) {
            mExpenditureCursor = data;
            mExpenditureRecyclerAdapter.changeCursor(mExpenditureCursor);
        } else if (loader.getId() == LOADER_EXPENSE_TOTAL) {
            mExpenditureCursorForTotal = data;
            if (mExpenditureCursorForTotal != null) {
                getExpenditureTotal();
                mExpenseTotalQueryFinished = true;
                setBarWhenQueriesFinished();
            }
        }
    }

    private void setBarWhenQueriesFinished() {
        if(mExpenseTotalQueryFinished && mIncomeQueryFinished) {
            setExpenditureBar();
            setIncomeBar();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_EXPENSE) {
            if (mExpenditureCursor != null)
                mExpenditureCursor.close();
        } else if (loader.getId() == LOADER_INCOME) {
            if (mIncomeCursor != null)
                mIncomeCursor.close();
        } else if (loader.getId() == LOADER_EXPENSE_TOTAL) {
            if (mExpenditureCursorForTotal != null)
                mExpenditureCursorForTotal.close();
        }
    }
}