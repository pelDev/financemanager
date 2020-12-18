package com.example.financemanager.ui.expense;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.financemanager.R;

public class AddExpense extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        EditText nameEditText, amountEditText, descriptionEditText;
        nameEditText = findViewById(R.id.editTextTextPersonName);
        amountEditText = findViewById(R.id.editTextExpenseAmount);
        descriptionEditText = findViewById(R.id.editTextExpenseDescription);
        Button addButton = findViewById(R.id.button_add_expense);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }
}