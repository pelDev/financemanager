package com.example.financemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.financemanager.notifiaction.BudgetNotificationReminder;
import com.example.financemanager.settings.SettingsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.concurrent.TimeUnit;

import static com.example.financemanager.Constants.BUDGET_WORKER_NAME;
import static com.example.financemanager.Constants.DESTINATION_FRAGMENT;
import static com.example.financemanager.Constants.MOVE_TO_ADD_INCOME;
import static com.example.financemanager.Constants.MOVE_TO_BUDGET;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController mNavController;
    private SpeedDialView mSpeedDialView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // home screen
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_logout).setOnMenuItemClickListener(
                item -> {
                    auth.signOut();
                    drawer.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(MainActivity.this, StartActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
        );

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_budget, R.id.nav_todo)
                .setDrawerLayout(drawer)
                .build();

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, mNavController);

        checkIfIntentHasData();
        registerRecurringWork();

        mSpeedDialView = findViewById(R.id.speedDial);
        mSpeedDialView.inflate(R.menu.fab_menu);
        mSpeedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.fab_add_income:
                        mNavController.navigate(R.id.actionAddIncomeFragment);
                        mSpeedDialView.close();
                        return true;
                    case R.id.fab_add_budget:
                        mNavController.navigate(R.id.actionAddBudgetFragment);
                        mSpeedDialView.close();
                        return true;
                    case R.id.fab_add_expense:
                        mNavController.navigate(R.id.actionAddExpenseFragment);
                        mSpeedDialView.close();
                        return true;
                }
                return false;
            }
        });
    }

    // register work that will display notification on the first
    // of every month.
    private void registerRecurringWork() {
        PeriodicWorkRequest.Builder myWorkerBuilder =
                new PeriodicWorkRequest.Builder(BudgetNotificationReminder.class,
                        1, TimeUnit.DAYS);
        WorkManager.getInstance(getApplicationContext())
                .enqueueUniquePeriodicWork(BUDGET_WORKER_NAME,
                        ExistingPeriodicWorkPolicy.KEEP,
                        myWorkerBuilder.build());
    }

    private void checkIfIntentHasData() {
        String message = getIntent().getStringExtra(DESTINATION_FRAGMENT);
        if (message != null && message.equals(MOVE_TO_BUDGET))
            mNavController.navigate(R.id.action_nav_home_to_nav_budget);
        else if (message != null && message.equals(MOVE_TO_ADD_INCOME))
            mNavController.navigate(R.id.actionAddIncomeFragment);
        else
            Toast.makeText(this,
                    "This message isn't understood.", Toast.LENGTH_SHORT)
                    .show();
    }

    public SpeedDialView getSpeedDialView() {
        return mSpeedDialView;
    }

    public void showSpeedDialView() {
        mSpeedDialView.show();
    }

    public void hideSpeedDialView() {
        mSpeedDialView.hide();
    }

    // Notification
    public void launchNotificationDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create or cancel a Notification")
                .setMessage("You can cancel or set your notification here.\n" +
                        "Click Set to set a Notification or Cancel to cancel an already set Notification")
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //sendNotification();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //cancelNotification();
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void radioButtonClicked(View view) {
        boolean isChecked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.radioButton_yes:
                if (isChecked) {
                    // if work queue does not already exist create it
                }
                break;
            case R.id.radioButton_no:
                if (isChecked) {
                    // delete queue if it exists before
                }
                break;
        }

    }

    //    @Override
//    public void onItemClick(String item) {
//        //Snackbar.make(mRecyclerView, item, Snackbar.LENGTH_SHORT).show();
//
//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_container);
//        if (fragment instanceof BudgetFragment) {
//            int q = 1;
//        }
////        ((BudgetFilterAction) getSupportFragmentManager().findFragmentById(R.id.))
////                .budgetFilterAction(item);
//    }

}