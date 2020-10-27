package com.example.financemanager;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import androidx.loader.content.AsyncTaskLoader;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.ExpenditureDatabaseContract.AmountInfoEntry;
import com.example.financemanager.ExpenditureDatabaseContract.ExpenditureInfoEntry;
import com.example.financemanager.ExpenditureDatabaseContract.IncomeInfoEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        NavigationView.OnNavigationItemSelectedListener {

    public static final int LOADER_AMOUNT = 3;

    private static final String TAG = "MainActivity";
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotifyManager;
    private static final int BUDGET_NOTIFICATION_ID = 0;

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
    private TextView greetings;
    private Cursor mExpenditureCursor;
    private FirebaseAuth mAuth;
    private int mBalance;
    private TextView mAmountLeft;
    private ImageView ivSetNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        mDbOpenHelper = new ExpenditureOpenHelper(this);
        mAuth = FirebaseAuth.getInstance();
        mExpenditureBar = findViewById(R.id.view2);
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
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(mDrawer)
                .build();
        initializeDisplayContent();

        createNotificationChannel();
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
        getLoaderManager().restartLoader(LOADER_INCOME, null, this);
        getLoaderManager().restartLoader(LOADER_EXPENSE, null, this);
        getLoaderManager().restartLoader(LOADER_EXPENSE_TOTAL, null, this);
        getLoaderManager().restartLoader(LOADER_AMOUNT, null, this);
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
        Log.i("Income", "Bar Height In dp " + Math.round(barHeight));
        if (barHeight > 0 && barHeight <= 100) {
            Log.i("Income", "Total Income for calculating Expenditure " + (float) mTotalIncome);
            int height = dpToPx(Math.round(barHeight));
            Log.i("Income", "Bar Height In px " + height);
            setExpenditureBarHeight(height);
        } else if (barHeight > 100) {
            updateExpenditure();
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
        if (mIncomeCursor != null) {
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
        } else if (id == LOADER_AMOUNT) {
            loader = createLoaderAmount();
        }
        return loader;
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
                String[] selectionArgs = {"October"};
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
        if (loader.getId() == LOADER_INCOME) {
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
        } else if (loader.getId() == LOADER_AMOUNT) {
            setBalance(data);
        }
    }

    private void setBalance(Cursor cursor) {
        cursor.moveToFirst();
        int amountPos = cursor.getColumnIndex(AmountInfoEntry.COLUMN_AMOUNT);
        int amountFromSql = cursor.getInt(amountPos);
        Long amountLong = new Long(amountFromSql);
        NumberFormat myFormat = NumberFormat.getInstance();
        myFormat.setGroupingUsed(true);
        String amount = myFormat.format(amountLong);
        mAmountLeft.setText("N" + amount);

    }

    private void setBarWhenQueriesFinished() {
        if (mExpenseTotalQueryFinished && mIncomeQueryFinished) {
            setExpenditureBar();
            setIncomeBar();
//            setAmountBalance();
        }
    }

//    private void setAmountBalance() {
//        mBalance = 0;
//    }

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nave_logout) {
//            mAuth.signOut();
            startActivity(new Intent(this, StartActivity.class));
            finish();
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
}