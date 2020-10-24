package com.example.financemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class BudgetActivity extends AppCompatActivity {

    private RecyclerView mRecyclerBudgets;
    private FinanceManagerOpenHelper mDbHelper;
    private BudgetRecyclerAdapter mBudgetRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.budget);

        Window window = BudgetActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.bckGround));

        mDbHelper = new FinanceManagerOpenHelper(this);
        mRecyclerBudgets = findViewById(R.id.recycler_budgets);

        initializeDisplayContent();


    }

    private void initializeDisplayContent() {
        // Set up the Recycler View
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerBudgets.setLayoutManager(layoutManager);
        mBudgetRecyclerAdapter = new BudgetRecyclerAdapter(this, null);
        mRecyclerBudgets.setAdapter(mBudgetRecyclerAdapter);
    }

    public void gotToAddBudget(View view) {
        // Navigate to Add Budget Screen
        startActivity(new Intent(this, AddBudgetActivity.class));
    }
}