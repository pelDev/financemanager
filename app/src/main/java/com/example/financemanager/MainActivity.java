package com.example.financemanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

import com.example.financemanager.ExpenditureDatabaseContract.ExpenditureInfoEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.loader.content.AsyncTaskLoader;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;

public class MainActivity extends AppCompatActivity {

    // home screen

    private AppBarConfiguration mAppBarConfiguration;
    private ExpenditureOpenHelper mDbOpenHelper;
    private RecyclerView mRecyclerExpenditure;
    private LinearLayoutManager mExpenditureLayoutManager;
    private ExpenditureRecyclerAdapter mExpenditureRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        mDbOpenHelper = new ExpenditureOpenHelper(this);

        Window window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.bckGround));

//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
//                .setDrawerLayout(drawer)
//                .build();
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
                return null;
            }
        };
        taskLoader.loadInBackground();
    }

    private void loadExpenditures() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        final String[] expenditureColumns = {
                ExpenditureInfoEntry.COLUMN_EXPENDITURE_NAME,
                ExpenditureInfoEntry.COLUMN_EXPENDITURE_TIMESTAMP,
                ExpenditureInfoEntry.COLUMN_EXPENDITURE_AMOUNT,
                ExpenditureInfoEntry._ID
        };
        //String noteOrderBy = CourseInfoEntry.COLUMN_COURSE_TITLE + ", " + NoteInfoEntry.COLUMN_NOTE_TITLE;

        final Cursor expenditureCursor = db.query(ExpenditureInfoEntry.TABLE_NAME, expenditureColumns, null,
                null, null, null, null);
        mExpenditureRecyclerAdapter.changeCursor(expenditureCursor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void openDrawer(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Show Menu Bar", null).show();
    }
}