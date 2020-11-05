package com.example.financemanager;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.loader.app.LoaderManager;

import androidx.core.view.GravityCompat;
import androidx.loader.content.AsyncTaskLoader;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.FinanceManagerDatabaseContract.AmountInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.BudgetInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.ExpenditureInfoEntry;
import com.example.financemanager.FinanceManagerDatabaseContract.IncomeInfoEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.DateFormatSymbols;

import java.text.NumberFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        NavigationView.OnNavigationItemSelectedListener {

    public static final int LOADER_AMOUNT = 3;

    private static final String TAG = "MainActivity";
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotifyManager;
    private static final int BUDGET_NOTIFICATION_ID = 0;

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
    private Animation mAnimBlink;
    private AsyncTaskLoader mTaskLoader;
    private boolean mIncomeQueryFinished;
    private boolean mExpenseTotalQueryFinished;
    private double mTotalExpenditure;
    private TextView greetings;
    private Cursor mExpenditureCursor;
    private FirebaseAuth mAuth;
    private int mBalance;
    private TextView mAmountLeft;
    private ImageView ivSetNotification;
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
    private boolean mExpenditureCursorCalled = false;
    private boolean mIncomeCursorCalled = false;
    private TextView mEmptyRecycler;


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
        mAnimBlink = AnimationUtils.loadAnimation(this, R.anim.blink);
        mNetIncomeAmountView = findViewById(R.id.textView_netIncome);
        mNetExpenseAmountView = findViewById(R.id.textView_netExpense);
        mExpInfoView = findViewById(R.id.textView_expInfo);
        ivSetNotification = findViewById(R.id.iv_set_notification);
        ivSetNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: notification icon clicked");
//                sendNotification();
                launchNotificationDialog();
            }
        });

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
        checkIfBudgetHasBeenCreated();
        createNotificationChannel();
    }

    private void checkIfBudgetHasBeenCreated() {
        AsyncTask task = new AsyncTask() {

            private Cursor mBudgetCursor;

            @Override
            protected Object doInBackground(Object[] objects) {
                String[] column = {BudgetInfoEntry.COLUMN_BUDGET_AMOUNT};
                String selection = BudgetInfoEntry.COLUMN_BUDGET_MONTH + " = ? AND " + BudgetInfoEntry.COLUMN_BUDGET_YEAR + " = ?";
                String[] selectionArgs = {mCurrentMonthName, Integer.toString(mYear)};
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                mBudgetCursor = db.query(BudgetInfoEntry.TABLE_NAME, column, selection, selectionArgs, null,
                        null, null);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                if (mBudgetCursor.getCount() <= 0) {
                    // setup alert Dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Creating a Budget is always the best first step. \n" +
                            "You have'nt created any this month.");

                    //add buttons
                    builder.setPositiveButton("Create",
                            // perform an action when user clicks
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    //startActivity(new Intent(MainActivity.this, BudgetActivity.class));
                                }
                            });

                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            startActivity(new Intent(MainActivity.this, BudgetActivity.class));
                        }
                    });

                    // Create and show the AlertDialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                super.onPostExecute(o);
            }
        };
        task.execute();

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
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        mCurrentMonthName = getMonthFromInt(month);
        mYear = calendar.get(Calendar.YEAR);
        mHomeDate.setText(new StringBuilder().append(mCurrentMonthName).append(", ").append(mYear).toString());
        //DataManager.loadFromDatabase(mDbOpenHelper);
        mRecyclerExpenditure = findViewById(R.id.list_expenditure);
        mEmptyRecycler = findViewById(R.id.emptyView);
        mExpenditureLayoutManager = new LinearLayoutManager(this);

        mExpenditureRecyclerAdapter = new ExpenditureRecyclerAdapter(this, null);
        selectNavigationMenuItem(R.id.nav_home);
        displayExpenditures();

        if (day == 1)
            //sendNotification();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int id = (int) viewHolder.itemView.getTag();
                // setup alert Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Alert");
                builder.setMessage("Are you sure you want to delete?!");

                //add buttons
                builder.setPositiveButton("Delete",
                        // perform an action when user clicks
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteAndRefresh(id);
                            }
                        });
                builder.setNegativeButton("Cancel",
                        // perform an action when user clicks
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        getLoaderManager().restartLoader(LOADER_EXPENSE, null, MainActivity.this);
                    }
                });

                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }).attachToRecyclerView(mRecyclerExpenditure);
    }

    private void deleteAndRefresh(final int id) {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                getIdAndAmountOfItem(id);
                String selection = ExpenditureInfoEntry._ID + " = ?";
                String[] selectionArgs = {Integer.toString(id)};
                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
                db.delete(ExpenditureInfoEntry.TABLE_NAME, selection, selectionArgs);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                mTotalIncome = 0;
                mTotalExpenditure = 0;
                getLoaderManager().restartLoader(LOADER_EXPENSE, null, MainActivity.this);
                getLoaderManager().restartLoader(LOADER_INCOME_TOTAL, null, MainActivity.this);
                getLoaderManager().restartLoader(LOADER_EXPENSE_TOTAL, null, MainActivity.this);
                getLoaderManager().restartLoader(LOADER_AMOUNT, null, MainActivity.this);
                getLoaderManager().restartLoader(LOADER_INCOME, null, MainActivity.this);
                super.onPostExecute(o);
            }
        };
        task.execute();
    }

    private void updateAmount(String amount) {
        double originalAmountInDatabase = getOriginalAmount();
        double amountToBeAdded = Double.parseDouble(amount);
        double newAmountForDatabase = originalAmountInDatabase + amountToBeAdded;
        if (newAmountForDatabase < 0)
            newAmountForDatabase = 0;
        ContentValues values = new ContentValues();
        values.put(AmountInfoEntry.COLUMN_AMOUNT, newAmountForDatabase);
        String selection = AmountInfoEntry.COLUMN_AMOUNT + " = ?";
        String[] selectionArgs = {Double.toString(originalAmountInDatabase)};
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.update(AmountInfoEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    private double getOriginalAmount() {
        String[] columns = {AmountInfoEntry.COLUMN_AMOUNT};
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(AmountInfoEntry.TABLE_NAME, columns,null,null,
                null,null,null);
        cursor.moveToFirst();
        int amountPos = cursor.getColumnIndex(AmountInfoEntry.COLUMN_AMOUNT);
        String amount = cursor.getString(amountPos);
        double amountDouble = Double.parseDouble(amount);
        cursor.close();
        return amountDouble;
    }

    private void getIdAndAmountOfItem(int id) {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        String[] columns = {
                ExpenditureInfoEntry.COLUMN_EXPENDITURE_ID,
                ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT
        };
        String selection = ExpenditureInfoEntry._ID + " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        Cursor cursor = db.query(ExpenditureInfoEntry.TABLE_NAME, columns, selection,
                selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.moveToFirst();
        int expIdPos = cursor.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_ID);
        int expAmountPos = cursor.getColumnIndex(ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT);
        String expId = cursor.getString(expIdPos);
        String expAmount = cursor.getString(expAmountPos);

        double originalAmountSpentInBudget = getOriginalAmountSpentInBudget(expId);
        if (originalAmountSpentInBudget >= 0) {
            double newAmount = originalAmountSpentInBudget - Double.parseDouble(expAmount);
            if (newAmount < 0)
                newAmount = 0;
            updateAmountSpentInBudget(newAmount, expId);
            updateAmount(expAmount);
        }
        cursor.close();
    }

    private double getOriginalAmountSpentInBudget(String expId) {
        double budgetAmountSpent = -1;
        String[] columns = {
                BudgetInfoEntry.COLUMN_BUDGET_AMOUNT_SPENT
        };
        String selection = BudgetInfoEntry.COLUMN_BUDGET_CATEGORY + " = ? AND " + BudgetInfoEntry.COLUMN_BUDGET_MONTH + " = ?";
        String[] selectionArgs = {expId, mCurrentMonthName};

        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        Cursor cursor = db.query(BudgetInfoEntry.TABLE_NAME, columns, selection,
                selectionArgs, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            int budgetAmountSpentPos = cursor.getColumnIndex(BudgetInfoEntry.COLUMN_BUDGET_AMOUNT_SPENT);
            String amountSpentString = cursor.getString(budgetAmountSpentPos);
            budgetAmountSpent = Double.parseDouble(amountSpentString);
        }
        cursor.close();
        return budgetAmountSpent;
    }

    private void updateAmountSpentInBudget(double amount, String id) {
        ContentValues values = new ContentValues();
        values.put(BudgetInfoEntry.COLUMN_BUDGET_AMOUNT_SPENT, Double.toString(amount));
        String selection = BudgetInfoEntry.COLUMN_BUDGET_CATEGORY + " = ? AND " + BudgetInfoEntry.COLUMN_BUDGET_MONTH + " = ?";
        String[] selectionArgs = {id, mCurrentMonthName};
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.update(BudgetInfoEntry.TABLE_NAME, values, selection, selectionArgs);
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

    private void displayExpenditures() {
        mRecyclerExpenditure.setLayoutManager(mExpenditureLayoutManager);
        mRecyclerExpenditure.setAdapter(mExpenditureRecyclerAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTotalIncome = 0;
        mTotalExpenditure = 0;
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
            mExpenditureCursorCalled =  true;
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
        if (!mExpenditureCursorCalled)
            barHeight = 0;
        if (barHeight > 0 && barHeight <= 100) {
            Log.i("Income", "Total Income for calculating Expenditure " + (float) mTotalIncome);
            int height = dpToPx(Math.round(barHeight));
            Log.i("Income", "Bar Height In px " + height);
            setExpenditureBarHeight(height);
        } else if (barHeight > 100) {
            updateExpenditure();
        } else if (barHeight == 0) {
            setExpenditureBarHeight(1);
        }
        Log.i("Income", "Total Expenditure " + mTotalExpenditure);
    }

    private void updateExpenditure() {
        mExpenditureBarLp.width = (int) Math.round(getScreenWidth() * 0.35);
        mExpenditureBarLp.height = dpToPx(100);
        mExpenditureBar.setLayoutParams(mExpenditureBarLp);
        mExpenditureBar.setBackgroundColor(Color.rgb(255, 0, 0));
        mExpenditureBar.startAnimation(mAnimSlideUp);
    }

    private void setExpenditureBarHeight(int height) {
        mExpenditureBarLp.width = (int) Math.round(getScreenWidth() * 0.35);
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
            mIncomeCursorCalled = true;
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
        int height;
        if (!mIncomeCursorCalled) {
           height  = 1;
        } else {
            height = 100;
        }
        mIncomeBarLp.width = (int) Math.round(getScreenWidth() * 0.35);
        mIncomeBarLp.height = dpToPx(height);
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

                String selection = ExpenditureInfoEntry.COLUMN_EXPENDITURE_MONTH + " = ? AND " + ExpenditureInfoEntry.COLUMN_EXPENDITURE_YEAR + " = ?";
                String[] selectionArgs = {mCurrentMonthName, Integer.toString(mYear)};
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

                String selection = IncomeInfoEntry.COLUMN_INCOME_MONTH + " = ? AND " + IncomeInfoEntry.COLUMN_INCOME_YEAR + " = ?";
                String[] selectionArgs = {mCurrentMonthName, Integer.toString(mYear)};

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
                }
                mIncomeQueryFinished = true;
                setBarWhenQueriesFinished();
                break;
            case LOADER_EXPENSE:
                mExpenditureCursor = data;
                if (mExpenditureCursor.getCount() != 0) {
                    mExpenditureRecyclerAdapter.changeCursor(mExpenditureCursor);
                    getExpenditureTotal(mExpenditureCursor);
                    mRecyclerExpenditure.setVisibility(View.VISIBLE);
                    mEmptyRecycler.setVisibility(View.GONE);
                } else {
                    mRecyclerExpenditure.setVisibility(View.GONE);
                    mEmptyRecycler.setVisibility(View.VISIBLE);
                }
                break;
            case LOADER_EXPENSE_TOTAL:
                mExpenditureCursorForTotal = data;
                if (mExpenditureCursorForTotal.getCount() != 0) {
                    getExpenditureTotal(mExpenditureCursorForTotal);
                }
                mExpenseTotalQueryFinished = true;
                setBarWhenQueriesFinished();
                break;
            case LOADER_AMOUNT:
                mAmountCursor = data;
                    setBalance(mAmountCursor);

                break;
            case LOADER_INCOME:
                mIncomeCursor = data;
                if (mIncomeCursor.getCount() != 0) {
                    getIncomeTotal(mIncomeCursor);
                }
        }
    }

    private void setBalance(Cursor cursor) {
        cursor.moveToFirst();
        mAmountLeft.startAnimation(mAnimBlink);
        int amountPos = cursor.getColumnIndex(AmountInfoEntry.COLUMN_AMOUNT);
        String amountFromSql = cursor.getString(amountPos);
        double amountD = Double.parseDouble(amountFromSql);
        long amountLong = (long) amountD;
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
        String amount = myFormat.format(amountLong);
        mAmountLeft.setText("N" + amount);
    }

    private void setBarWhenQueriesFinished() {
        if (mExpenseTotalQueryFinished && mIncomeQueryFinished) {
            setExpenditureBar();
            setIncomeBar();
            double percent = (mTotalExpenditure / mTotalIncome) * 100;
            String info = getInfo(percent);
            mExpInfoView.setText(info);
        }
    }

    private String getInfo(double percent) {
        double roundOff = Math.round(percent * 100.0) / 100.0;
        return "You've spent " + roundOff + "% of your income.";
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
        } else if (loader.getId() == LOADER_INCOME) {
            if (mIncomeCursor != null)
                mIncomeCursor.close();
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


    public void launchNotificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create or cancel a Notification")
                .setMessage("You can cancel or set your notification here.\n" +
                        "Click Set to set a Notification or Cancel to cancel an already set Notification")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendNotification();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelNotification();
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Create a NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Budget Notification", NotificationManager
                    .IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification for budget");
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        Intent notificationIntent = new Intent(this, BudgetActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                BUDGET_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String message = "It's that time of the month again where making a budget decision is very important";

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_budget)
                .setContentTitle("Make a Budget")
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker("Budget")
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);
        return notifyBuilder;
    }

    public void sendNotification() {
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        mNotifyManager.notify(BUDGET_NOTIFICATION_ID, notifyBuilder.build());
//            }
//        }, 30000);
    }

    public void cancelNotification() {
        mNotifyManager.cancel(BUDGET_NOTIFICATION_ID);
    }
    public void moveToNetIncomeActivity(View view) {
        startActivity(new Intent(this, NetIncomeActivity.class));
    }
      
}