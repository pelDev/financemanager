package com.example.financemanager.ui.budget;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.financemanager.BR;
import com.example.financemanager.R;
import com.example.financemanager.databinding.AddBudgetActivityBinding;

public class AddBudgetActivity extends AppCompatActivity {

    private AddBudgetViewModel mAddBudgetViewModel;
    public AddBudgetActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.add_budget_activity);

        binding = DataBindingUtil.setContentView(this, R.layout.add_budget_activity);

        binding.setLifecycleOwner(this);

        mAddBudgetViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(AddBudgetViewModel.class);

        binding.setVariable(BR.myAddBudgetViewModel, mAddBudgetViewModel);
    }

}
