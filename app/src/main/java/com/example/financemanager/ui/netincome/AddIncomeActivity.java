package com.example.financemanager.ui.netincome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.financemanager.R;

public class AddIncomeActivity extends AppCompatActivity {

    private EditText mAmountEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        mAmountEditText = findViewById(R.id.editTextNumber_income_amount);
        setUpListener();
    }

    private void setUpListener() {
        Button button = findViewById(R.id.button_add_income);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = mAmountEditText.getText().toString();
            }
        });
    }
}