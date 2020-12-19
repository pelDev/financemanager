package com.example.financemanager.ui.expense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.financemanager.R;
import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.databinding.ActivityAddExpenseBinding;
import com.example.financemanager.ui.report.ReportViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import static androidx.databinding.library.baseAdapters.BR.myViewModel;

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
        mBinding.setVariable(myViewModel, mViewModel);
    }

}