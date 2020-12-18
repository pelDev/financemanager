package com.example.financemanager.ui.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.R;
import com.example.financemanager.database.expense.Expenditure;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static androidx.lifecycle.ViewModelProvider.*;

public class ReportFragment extends Fragment {

    private ReportViewModel mReportViewModel;
    private ExpenditureListAdapter mAdapter;
    private boolean isAllFabVisible = false;
    private FloatingActionButton mAddFab;


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

        mReportViewModel = new ViewModelProvider(this,
                AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(ReportViewModel.class);

        mAddFab = getView().findViewById(R.id.add_fab);

        // set up navigation to add income activity
        MaterialCardView netIncomeCard = getView().findViewById(R.id.cardA);
        netIncomeCard.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_reportFragment_to_addIncomeActivity, null));

        // set up fabs
        setUpFabs();

        // set up recycler view
        mAdapter = new ExpenditureListAdapter(R.layout.item_expenditure);
        RecyclerView recyclerView = getView().findViewById(R.id.list_expenditure);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        // set up expenditure list observer
        mReportViewModel.getAllExpenditures().observe(getViewLifecycleOwner(),
                new Observer<List<Expenditure>>() {
                    @Override
                    public void onChanged(List<Expenditure> expenditures) {
                        mAdapter.setExpenditureList(expenditures);
                    }
                });

    }


    private void setUpFabs() {
        mAddFab.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_reportFragment_to_addExpense, null));
    }

}
