package com.example.financemanager;

import androidx.lifecycle.ViewModel;

public class ReportViewModel extends ViewModel {

    private String amount = "";
    private String netIncome = "";
    private String netExpense = "";

    public void setNetIncome(String value) {
        this.netIncome = value;
        int amountInt;
        int valueInt;
        if (amount.equals("")) {
            amountInt = 0;
        } else {
            amountInt = Integer.parseInt(amount);
        }
        if (netIncome.equals("")) {
            valueInt = 0;
        } else {
            valueInt = Integer.parseInt(netIncome);
        }

        amountInt += valueInt;

        this.amount = String.valueOf(amountInt);

    }

    public String getAmount() {return amount;}

}

