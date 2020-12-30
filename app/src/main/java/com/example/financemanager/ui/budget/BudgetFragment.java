package com.example.financemanager.ui.budget;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financemanager.MainActivity;
import com.example.financemanager.R;
import com.example.financemanager.database.budget.Budget;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.List;

public class BudgetFragment extends Fragment {

    private BudgetListAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.budget_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // floating action button
        final SpeedDialView floatingActionButton = ((MainActivity) getActivity()).getSpeedDialView();
        if (floatingActionButton != null) {
            ((MainActivity) getActivity()).showSpeedDialView();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BudgetViewModel budgetViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(BudgetViewModel.class);

        mAdapter = new BudgetListAdapter(R.layout.item_budget);
        RecyclerView recyclerView = getView().findViewById(R.id.list_budgets);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        // set up budget list observer
        budgetViewModel.getAllBudgets().observe(getViewLifecycleOwner(),
                new Observer<List<Budget>>() {
                    @Override
                    public void onChanged(List<Budget> budgets) {
                        mAdapter.setBudgetList(budgets);
                    }
                });
    }
}