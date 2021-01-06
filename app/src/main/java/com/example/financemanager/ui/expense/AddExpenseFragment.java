package com.example.financemanager.ui.expense;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financemanager.BR;
import com.example.financemanager.MainActivity;
import com.example.financemanager.R;
import com.example.financemanager.databinding.AddExpenseFragmentBinding;
import com.google.android.material.snackbar.Snackbar;
import com.leinardi.android.speeddial.SpeedDialView;

public class AddExpenseFragment extends Fragment {

    public AddExpenseFragmentBinding binding;
    private AddExpenseViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_expense_fragment, container, false);
        binding.setLifecycleOwner(this);

        final SpeedDialView floatingActionButton = ((MainActivity) getActivity()).getSpeedDialView();

        if (floatingActionButton != null) {
            ((MainActivity) getActivity()).hideSpeedDialView();
        }
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(AddExpenseViewModel.class);
        binding.setVariable(BR.myViewModel, mViewModel);

        mViewModel.getCompleted().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    Navigation.findNavController(binding.buttonAddExpense).popBackStack();
            }
        });

        mViewModel.getError().observe(getViewLifecycleOwner(), (error) -> {
            if (!error.equals("")) {
                Snackbar snackbar = Snackbar.make(binding.scrollView, error, Snackbar.LENGTH_LONG);
                if (error.substring(0, 6).equals("Budget"))
                    snackbar.setAction("Set", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NavHostFragment.findNavController(AddExpenseFragment.this)
                                    .navigate(R.id.actionAddBudgetFragment);
                        }
                    });
                snackbar.show();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        mViewModel.setError("");
    }
}