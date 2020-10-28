package com.example.financemanager;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.example.financemanager.FinanceManagerDatabaseContract.AmountInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.ExpenditureInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.IncomeInfoEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
//import androidx.loader.app.LoaderManager;
import android.content.Loader;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.widget.TextView;

import androidx.core.view.GravityCompat;
import androidx.loader.content.AsyncTaskLoader;
//import androidx.loader.content.CursorLoader;
//import androidx.loader.content.Loader;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
NavigationView.OnNavigationItemSelectedListener{
    public static final int LOADER_AMOUNT = 3;

    // home screen

    private FinanceManagerOpenHelper mDbOpenHelper;
    private RecyclerView mRecyclerExpenditure;
    private static final int LOADER_INCOME_TOTAL = 0;
    private static final int LOADER_EXPENSE = 1;
    private static final int LOADER_EXPENSE_TOTAL = 2;
    private static final int LOADER_INCOME = 4;
    private LinearLayoutManager mExpenditureLayoutManager;
    private ExpenditureRecyclerAdapter mExpenditureRecyclerAdapter;
    private DrawerLayout mDrawer;
    private Cursor mIncomeCursorForTotal;
    private int mIncomeAmountPos;
    private Cursor mExpenditureCursorForTotal;
    private int mExpenditureForTotalAmountPos;
    private View mIncomeBar;
    private double mTotalIncome;
    private View mExpenditureBar;
    private LinearLayout.LayoutParams mIncomeBarLp;
    private LinearLayout.LayoutParams mExpenditureBarLp;
    private Animation mAnimSlideUp;
    private AsyncTaskLoader mTaskLoader;
    private boolean mIncomeQueryFinished;
    private boolean mExpenseTotalQueryFinished;
    private double mTotalExpenditure;
    private TextView greetings;
    private Cursor mExpenditureCursor;
    private FirebaseAuth mAuth;
    private int mBalance;
    private TextView mAmountLeft;
    private TextView mHomeDate;
    private String mCurrentMonthName;
    private Cursor mAmountCursor;
    private Cursor mIncomeCursor;
    private int mNetIncomeAmountPos;
    private TextView mNetIncomeAmountView;
    private int mYear;
    private int mExpenditureAmountPos;
    private TextView mNetExpenseAmountView;
    private TextView mExpInfoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        mDbOpenHelper = new FinanceManagerOpenHelper(this);
        mAuth = FirebaseAuth.getInstance();
        mExpenditureBar = findViewById(R.id.view2);
        mHomeDate = findViewById(R.id.home_screen_date);
        mExpenditureBarLp = (LinearLayout.LayoutParams) mExpenditureBar.getLayoutParams();
        mIncomeBar = findViewById(R.id.view);
        mIncomeBarLp = (LinearLayout.LayoutParams) mIncomeBar.getLayoutParams();
        mAmountLeft = findViewById(R.id.text_amount_left);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View rightSpacer = findViewById(R.id.view7);
        LinearLayout.LayoutParams RSLp = (LinearLayout.LayoutParams) rightSpacer.getLayoutParams();
        View middleSpacer = findViewById(R.id.view6);
        LinearLayout.LayoutParams MSLp = (LinearLayout.LayoutParams) middleSpacer.getLayoutParams();
        View leftSpacer = findViewById(R.id.view8);
        LinearLayout.LayoutParams LSLp = (LinearLayout.LayoutParams) leftSpacer.getLayoutParams();
        mAnimSlideUp = AnimationUtils.loadAnimation(this, R.anim.scale);
        mNetIncomeAmountView = findViewById(R.id.textView_netIncome);
        mNetExpenseAmountView = findViewById(R.id.textView_netExpense);
        mExpInfoView = findViewById(R.id.textView_expInfo);

        RSLp.width = (int) Math.round(getScreenWidth() * 0.1);
        rightSpacer.setLayoutParams(RSLp);

        MSLp.width = (int) Math.round(getScreenWidth() * 0.1);
        middleSpacer.setLayoutParams(MSLp);

        LSLp.width = (int) Math.round(getScreenWidth() * 0.1);
        leftSpacer.setLayoutParams(LSLp);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Window window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        // On Pressed Navigate To Add Expense or Add Income Screen
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddExpenseActivity.class));
            }
        });
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_budget, R.id.nav_slideshow)
                .setDrawerLayout(mDrawer)
                .build();
        initializeDisplayContent();
    }

    private void selectNavigationMenuItem(int p) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(p).setChecked(true);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private void initializeDisplayContent() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        mCurrentMonthName = getMonthFromInt(month);
        mYear = calendar.get(Calendar.YEAR);
        mHomeDate.setText(new StringBuilder().append(mCurrentMonthName).append(", ").append(mYear).toString());
        //DataManager.loadFromDatabase(mDbOpenHelper);
        mRecyclerExpenditure = (RecyclerView) findViewById(R.id.list_expenditure);
        mExpenditureLayoutManager = new LinearLayoutManager(this);

        mExpenditureRecyclerAdapter = new ExpenditureRecyclerAdapter(this, null);
        selectNavigationMenuItem(R.id.nav_home);
        displayExpenditures();
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

    @Override
    protected void onStart() {
        super.onStart();

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
        getLoaderManager().restartLoader(LOADER_INCOME_TOTAL, null, this);
        getLoaderManager().restartLoader(LOADER_EXPENSE, null, this);
        getLoaderManager().restartLoader(LOADER_EXPENSE_TOTAL, null, this);
        getLoaderManager().restartLoader(LOADER_AMOUNT, null, this);
        getLoaderManager().restartLoader(LOADER_INCOME, null, this);
    }



    private void getExpenditureTotal(Cursor cursor) {
        populateExpenditureColumnPosition(cursor);

        if (cursor == mExpenditureCursorForTotal) {
            mTotalExpenditure = 0;
            mExpenditureCursorForTotal.moveToFirst();
            while (!mExpenditureCursorForTotal.isAfterLast()) {
                String amount = mExpenditureCursorForTotal.getString(mExpenditureForTotalAmountPos);
                mTotalExpenditure = mTotalExpenditure + Double.parseDouble(amount);
                mExpenditureCursorForTotal.moveToNext();
            }
        }

        else if (cursor == mExpenditureCursor) {
            double total = 0;
            mExpenditureCursor.moveToFirst();
            while (!mExpenditureCursor.isAfterLast()) {
                String amount = mExpenditureCursor.getString(mExpenditureAmountPos);
                total = total + Double.parseDouble(amount);
                mExpenditureCursor.moveToNext();
            }
            String totalString = formatTotal(total);
            mNetExpenseAmountView.setText(totalString);
        }


    }

    private void setExpenditureBar() {
        float barHeight = (float) ((mTotalExpenditure / mTotalIncome) * 100f);
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
        mExpenditureBar.setBackgroundColor(Color.parseColor("#62b7d5"));
        mExpenditureBar.startAnimation(mAnimSlideUp);
    }

    private void populateExpenditureColumnPosition(Cursor cursor) {
        if (cursor == mExpenditureCursorForTotal) {
            if (mExpenditureCursorForTotal != null) {
                // get column position for expenditure_amount in the table
                mExpenditureForTotalAmountPos = mExpenditureCursorForTotal.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT);

                // get column position for expenditure_month in the table
                int expenditureMonthPos = mExpenditureCursorForTotal.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_MONTH);

            }
        }
        if (cursor == mExpenditureCursor) {
            mExpenditureAmountPos = mExpenditureCursor.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT);
        }

    }

    public void openDrawer(View view) {
        mDrawer.open();
    }

    // get total amount of income received in a particular month from the income table.
    public void getIncomeTotal(Cursor cursor) {
        populateIncomeColumnPosition(cursor);

        if (cursor == mIncomeCursorForTotal) {
            // move cursor to the first row
            mIncomeCursorForTotal.moveToFirst();

            //check if the cursor has passed the last row of the table
            while (!mIncomeCursorForTotal.isAfterLast()) {
                String amount = mIncomeCursorForTotal.getString(mIncomeAmountPos);
                mTotalIncome = mTotalIncome + Double.parseDouble(amount);
                mIncomeCursorForTotal.moveToNext();
            }
        } else if (cursor == mIncomeCursor) {
            mIncomeCursor.moveToFirst();
            double total = 0;
            while (!mIncomeCursor.isAfterLast()) {
                String amount = mIncomeCursor.getString(mNetIncomeAmountPos);
                total = total + Double.parseDouble(amount);
                mIncomeCursor.moveToNext();
            }
            String totalString = formatTotal(total);
            mNetIncomeAmountView.setText(totalString);
        }

    }

    private String formatTotal(double total) {
        Long amnt = (long) total;
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
        String n = myFormat.format(amnt);
        return "N" + n;
    }

    // get column positions from the income table
    private void populateIncomeColumnPosition(Cursor cursor) {
        if (cursor == mIncomeCursorForTotal) {
            if(mIncomeCursorForTotal != null) {
                // get column position for income_amount in the table
                mIncomeAmountPos = mIncomeCursorForTotal.getColumnIndex(IncomeInfoEntry.COLUMN_INCOME_AMOUNT);
            }
        } else if (cursor == mIncomeCursor) {
            mNetIncomeAmountPos = mIncomeCursor.getColumnIndex(IncomeInfoEntry.COLUMN_INCOME_AMOUNT);
        }

    }

    private void setIncomeBar() {
        mIncomeBarLp.width = (int) Math.round(getScreenWidth() * 0.35);
        mIncomeBarLp.height = dpToPx(100);
        mIncomeBar.setLayoutParams(mIncomeBarLp);
        mIncomeBar.startAnimation(mAnimSlideUp);
    }



    // convert dp tp px
    public final int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIncomeCursorForTotal.close();
        mExpenditureCursorForTotal.close();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_INCOME_TOTAL) {
            loader = createLoaderIncomeTotal();
        } else if (id == LOADER_EXPENSE) {
            loader = createLoaderExpense();
        } else if (id == LOADER_EXPENSE_TOTAL) {
            loader = createLoaderExpenseTotal();
        } else if (id == LOADER_AMOUNT) {
            loader = createLoaderAmount();
        } else if (id == LOADER_INCOME) {
            loader = createLoaderIncome();
        }
        return loader;
    }

    private CursorLoader createLoaderIncome() {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                String[] columns = {
                        IncomeInfoEntry.COLUMN_INCOME_AMOUNT,
                };
                String selection = IncomeInfoEntry.COLUMN_INCOME_YEAR + " = ?";
                String[] selectionArgs = {Integer.toString(mYear)};
                return db.query(IncomeInfoEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
            }
        };
    }

    private CursorLoader createLoaderAmount() {
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                String[] columns = {AmountInfoEntry.COLUMN_AMOUNT};
                return db.query(AmountInfoEntry.TABLE_NAME, columns, null, null,
                        null, null, null);
            }
        };
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
                String[] selectionArgs = {mCurrentMonthName};
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
                String selection = ExpenditureInfoEntry.COLUMN_EXPENDITURE_YEAR + " = ?";
                String[] selectionArgs = {Integer.toString(mYear)};
                //String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE + ", " + NoteInfoEntry.COLUMN_NOTE_TITLE;

                return db.query(ExpenditureInfoEntry.TABLE_NAME, expenditureColumns, selection,
                        selectionArgs, null, null, null);
            }
        };
    }

    private CursorLoader createLoaderIncomeTotal() {
        mIncomeQueryFinished = false;
        return new CursorLoader(this) {
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                String[] incomeColumns = {
                        IncomeInfoEntry.COLUMN_INCOME_AMOUNT,
                };

                String selection = IncomeInfoEntry.COLUMN_INCOME_MONTH + " = ?";
                String[] selectionArgs = {mCurrentMonthName};

                return db.query(IncomeInfoEntry.TABLE_NAME, incomeColumns, selection, selectionArgs,
                        null, null, null);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_INCOME_TOTAL:
                mIncomeCursorForTotal = data;
                if (mIncomeCursorForTotal.getCount() != 0) {
                    getIncomeTotal(mIncomeCursorForTotal);
                    mIncomeQueryFinished = true;
                    setBarWhenQueriesFinished();
                }
                break;
            case LOADER_EXPENSE:
                mExpenditureCursor = data;
                mExpenditureRecyclerAdapter.changeCursor(mExpenditureCursor);
                getExpenditureTotal(mExpenditureCursor);
                break;
            case LOADER_EXPENSE_TOTAL:
                mExpenditureCursorForTotal = data;
                if (mExpenditureCursorForTotal != null) {
                    getExpenditureTotal(mExpenditureCursorForTotal);
                    mExpenseTotalQueryFinished = true;
                    setBarWhenQueriesFinished();
                }
                break;
            case LOADER_AMOUNT:
                mAmountCursor = data;
                setBalance(mAmountCursor);
                break;
            case LOADER_INCOME:
                mIncomeCursor = data;
                getIncomeTotal(mIncomeCursor);
        }
    }

    private void setBalance(Cursor cursor) {
        cursor.moveToFirst();
        int amountPos = cursor.getColumnIndex(AmountInfoEntry.COLUMN_AMOUNT);
        String amountFromSql = cursor.getString(amountPos);
        double amountD = Double.parseDouble(amountFromSql);
        long amountLong = (long) amountD;
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
        String amount = myFormat.format(amountLong);
        mAmountLeft.setText("N"  + amount);

    }

    private void setBarWhenQueriesFinished() {
        if(mExpenseTotalQueryFinished && mIncomeQueryFinished) {
            setExpenditureBar();
            setIncomeBar();
            double percent = (mTotalExpenditure / mTotalIncome) * 100;
            String info = getInfo(percent);
            mExpInfoView.setText(info);
        }
    }

    private String getInfo(double percent) {
        return "You've spent " + percent + "% of your income.";
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_EXPENSE) {
            if (mExpenditureCursor != null)
                mExpenditureCursor.close();
        } else if (loader.getId() == LOADER_INCOME_TOTAL) {
            if (mIncomeCursorForTotal != null)
                mIncomeCursorForTotal.close();
        } else if (loader.getId() == LOADER_EXPENSE_TOTAL) {
            if (mExpenditureCursorForTotal != null)
                mExpenditureCursorForTotal.close();
        } else if (loader.getId() == LOADER_AMOUNT) {
            if (mAmountCursor != null)
                mAmountCursor.close();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nave_logout) {
            // Sign user out and redirect to start screen.
            mAuth.signOut();
            startActivity(new Intent(this, StartActivity.class));
            // close the drawer
            mDrawer.closeDrawer(GravityCompat.START);
            // User should not be able to access this activity with a back press
            // so kill this activity
            finish();
        } else if (id == R.id.nav_budget) {
            //selectNavigationMenuItem(R.id.nav_budget);
            // close the drawer
            mDrawer.closeDrawer(GravityCompat.START);
            // Navigate to the Budget activity
            startActivity(new Intent(this, BudgetActivity.class));
        }
        return false;
    }

    public void moveToNetIncomeActivity(View view) {
        startActivity(new Intent(this, NetIncomeActivity.class));
    }
}