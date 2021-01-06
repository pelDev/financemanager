package com.example.financemanager.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.BR;
import com.example.financemanager.MainActivity;
import com.example.financemanager.R;
import com.example.financemanager.database.budget.Budget;
import com.example.financemanager.databinding.BudgetFragmentBinding;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.List;

public class BudgetFragment extends Fragment{

    private BudgetListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private BudgetFragmentBinding binding;
    private BudgetViewModel mBudgetViewModel;

    public  BudgetFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.budget_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            NavController controller = Navigation.findNavController(getView().findViewById(R.id.list_budgets));
            controller.navigate(R.id.monthPicker);
        }
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.budget_fragment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // floating action button
        final SpeedDialView floatingActionButton = ((MainActivity) getActivity()).getSpeedDialView();
        if (floatingActionButton != null) {
            ((MainActivity) getActivity()).showSpeedDialView();
        }
        NavController controller = NavHostFragment.findNavController(this);
        final NavBackStackEntry navBackStackEntry = controller.getBackStackEntry(R.id.nav_budget);

        final LifecycleEventObserver observer = new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event.equals(Lifecycle.Event.ON_RESUME) && navBackStackEntry.getSavedStateHandle().contains("key")) {
                    String result = navBackStackEntry.getSavedStateHandle().get("key");
                    assert result != null;
                    mBudgetViewModel.filter(result);
                }
            }
        };

        navBackStackEntry.getLifecycle().addObserver(observer);

        getViewLifecycleOwner().getLifecycle().addObserver(new LifecycleEventObserver() {
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                if (event.equals(Lifecycle.Event.ON_DESTROY)) {
                    navBackStackEntry.getLifecycle().removeObserver(observer);
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBudgetViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(BudgetViewModel.class);
        binding.setVariable(BR.budgetViewModel, mBudgetViewModel);
        mAdapter = new BudgetListAdapter(R.layout.item_budget);
        mRecyclerView = getView().findViewById(R.id.list_budgets);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);

        // set up budget list observer
        mBudgetViewModel.getAllBudgets().observe(getViewLifecycleOwner(),
                new Observer<List<Budget>>() {
                    @Override
                    public void onChanged(List<Budget> budgets) {
                        if (budgets.size() > 0) {
                            mAdapter.setBudgetList(budgets);
                            initializePieChart(budgets);
                            binding.listBudgets.setVisibility(View.VISIBLE);
                            binding.emptyViewIncome.setVisibility(View.GONE);
                        } else {
                            binding.listBudgets.setVisibility(View.GONE);
                            binding.emptyViewIncome.setVisibility(View.VISIBLE);
                            binding.piechart.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initializePieChart(List<Budget> budgets) {
        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i < budgets.size(); i++) {
            double amount = (double) budgets.get(i).getAmount();
            String label = budgets.get(i).getCategory();
            pieEntries.add(new PieEntry((float) amount, label));
        }
        binding.piechart.setVisibility(View.VISIBLE);
        binding.piechart.animateXY(2500, 2500);
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Budget");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData pieData = new PieData(pieDataSet);
        binding.piechart.setData(pieData);
        binding.piechart.invalidate();
    }

}