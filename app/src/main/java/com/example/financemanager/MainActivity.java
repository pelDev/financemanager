package com.example.financemanager;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;

//import androidx.loader.app.LoaderManager;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotifyManager;
    private static final int BUDGET_NOTIFICATION_ID = 0;

    // home screen
    private DrawerLayout mDrawer;
    private View mIncomeBar;
    private View mExpenditureBar;
    private LinearLayout.LayoutParams mIncomeBarLp;
    private LinearLayout.LayoutParams mExpenditureBarLp;
    private Animation mAnimSlideUp;
    private FirebaseAuth mAuth;
    private ImageView ivSetNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mExpenditureBar = findViewById(R.id.view2);
        mExpenditureBarLp = (LinearLayout.LayoutParams) mExpenditureBar.getLayoutParams();
        mIncomeBar = findViewById(R.id.view);
        mIncomeBarLp = (LinearLayout.LayoutParams) mIncomeBar.getLayoutParams();
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

        // set chart parameters

        // set the width of the space on the right to 10% of the screen Width
        RSLp.width = (int) Math.round(getScreenWidth() * 0.1);
        rightSpacer.setLayoutParams(RSLp);

        // set the width of the space in the middle of the bars to 10% of the device screen width.
        MSLp.width = (int) Math.round(getScreenWidth() * 0.1);
        middleSpacer.setLayoutParams(MSLp);

        LSLp.width = (int) Math.round(getScreenWidth() * 0.1);
        leftSpacer.setLayoutParams(LSLp);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // On Pressed Navigate To Add Expense or Add Income Screen
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        createNotificationChannel();
        setExpenditureBarHeight(dpToPx(50));
        setIncomeBar();
    }

    private void selectNavigationMenuItem(int p) {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(p).setChecked(true);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private void setExpenditureBarHeight(int height) {
        mExpenditureBarLp.width = (int) Math.round(getScreenWidth() * 0.35);
        mExpenditureBarLp.height = height;
        mExpenditureBar.setLayoutParams(mExpenditureBarLp);
        mExpenditureBar.setBackgroundColor(Color.parseColor("#62b7d5"));
        mExpenditureBar.startAnimation(mAnimSlideUp);
    }

    public void openDrawer(View view) {
        mDrawer.open();
    }

    private void setIncomeBar() {
        mIncomeBarLp.width = (int) Math.round(getScreenWidth() * 0.35);
        mIncomeBarLp.height = dpToPx(30);
        mIncomeBar.setLayoutParams(mIncomeBarLp);
        mIncomeBar.startAnimation(mAnimSlideUp);
    }

    // convert dp tp px
    public final int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
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
        } else if (id == R.id.nav_todo) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        return false;
    }


    // Notification
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

}