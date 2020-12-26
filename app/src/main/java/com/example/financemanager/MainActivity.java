package com.example.financemanager;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.financemanager.ui.budget.BudgetFragment;
import com.example.financemanager.ui.report.ReportFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

public class MainActivity extends AppCompatActivity
//        implements
//        NavigationView.OnNavigationItemSelectedListener
{

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotifyManager;
    private static final int BUDGET_NOTIFICATION_ID = 0;

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

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_budget, R.id.nav_todo)
                .setDrawerLayout(drawer)
                .build();

        //mNavigationView.setNavigationItemSelectedListener(this);
        createNotificationChannel();

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, mNavController);

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
//        Intent notificationIntent = new Intent(this, BudgetActivity.class);
//        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
//                BUDGET_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String message = "It's that time of the month again where making a budget decision is very important";

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_budget)
                .setContentTitle("Make a Budget")
                .setColor(getResources().getColor(R.color.colorSecondary))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setTicker("Budget")
//                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);
        return notifyBuilder;
    }

    public void sendNotification() {
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        mNotifyManager.notify(BUDGET_NOTIFICATION_ID, notifyBuilder.build());
    }

    public void cancelNotification() {
        mNotifyManager.cancel(BUDGET_NOTIFICATION_ID);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}