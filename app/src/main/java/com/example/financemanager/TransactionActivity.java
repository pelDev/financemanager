package com.example.financemanager;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

public class TransactionActivity extends AppCompatActivity {

    private MaterialToolbar mToolbar;
    private TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        mToolbar =  findViewById(R.id.toolbar_transactions);
        setSupportActionBar(mToolbar);
       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTabLayout = findViewById(R.id.tabs_transactions);
        mTabLayout.addTab(mTabLayout.newTab().setText("Deposit"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Withdraw"));
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.viewpager_transactions);
        final TransactionViewPagerAdapter pagerAdapter = new TransactionViewPagerAdapter(
                getSupportFragmentManager(), mTabLayout.getTabCount()
        );
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new
                TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void doNothing(View view) {
    }
}