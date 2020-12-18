package com.example.financemanager.ui.expense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.financemanager.R;
import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.ui.report.ReportViewModel;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class AddExpense extends AppCompatActivity {

    ReportViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        EditText nameEditText, amountEditText, descriptionEditText;
        nameEditText = findViewById(R.id.editTextTextPersonName);
        amountEditText = findViewById(R.id.editTextExpenseAmount);
        descriptionEditText = findViewById(R.id.editTextExpenseDescription);
        Button addButton = findViewById(R.id.button_add_expense);

        mViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(ReportViewModel.class);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                String month = getMonthFromInt(calendar.get(Calendar.MONTH));
                int year = calendar.get(Calendar.YEAR);

                if (!amountEditText.getText().toString().equals("")) {
                    Expenditure expenditure = new Expenditure("food",
                            nameEditText.getText().toString(),
                            descriptionEditText.getText().toString(),
                            day,
                            month,
                            year,
                            Integer.parseInt(amountEditText.getText().toString()));
                    mViewModel.insertExpenditure(expenditure);
                    finish();
                }
            }
        });
    }

    private String getMonthFromInt(int month) {
        String monthString = "";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (month >= 0 && month <= 11) {
            monthString = months[month];
        }
        return monthString;
    }

}