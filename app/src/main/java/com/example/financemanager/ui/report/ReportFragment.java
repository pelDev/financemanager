package com.example.financemanager.ui.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.financemanager.R;
import com.google.android.material.card.MaterialCardView;

public class ReportFragment extends Fragment {

    private ReportViewModel mReportViewModel;
    private TextView amount, netIncome, netExpense;
    private int count = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.report_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mReportViewModel = new ViewModelProvider(this).get(ReportViewModel.class);

        // set up navigation to add income activity
        MaterialCardView netIncomeCard = getView().findViewById(R.id.cardA);
        netIncomeCard.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_reportFragment_to_addIncomeActivity, null));

    }

}
