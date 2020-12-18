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
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.financemanager.ui.budget.BudgetFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, BudgetFragment.OnFragmentInteractionListener {

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotifyManager;
    private static final int BUDGET_NOTIFICATION_ID = 0;

    // home screen
    private DrawerLayout mDrawer;
    private FirebaseAuth mAuth;
    private NavigationView mNavigationView;
    private NavController mNavController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        createNotificationChannel();

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);


    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public void openDrawer(View view) {
        mDrawer.open();
    }

    // convert dp tp px
    public final int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            mNavController.navigate(R.id.action_budgetFragment_to_reportFragment, null);
        } else if (id == R.id.nav_logout) {
            // Sign user out and redirect to start screen.
            mAuth.signOut();
            startActivity(new Intent(this, StartActivity.class));
            // User should not be able to access this activity with a back press
            // so kill this activity
            finish();
        } else if (id == R.id.nav_budget) {
            // Navigate to the Budget activity
            mNavController.navigate(R.id.action_reportFragment_to_budgetFragment, null);
        }
        // close drawer
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
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
    public void onBudgetFragmentInteraction(Uri uri) {

    }
}