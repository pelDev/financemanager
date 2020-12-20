package com.example.financemanager.ui.expense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;

import com.example.financemanager.BR;
import com.example.financemanager.R;
import com.example.financemanager.databinding.ActivityAddExpenseBinding;


public class AddExpense extends AppCompatActivity {

    private AddExpenseViewModel mViewModel;
    public ActivityAddExpenseBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_expense);

        mBinding .setLifecycleOwner(this);

        mViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(AddExpenseViewModel.class);
        mBinding.setVariable(BR.myViewModel, mViewModel);
    }

}