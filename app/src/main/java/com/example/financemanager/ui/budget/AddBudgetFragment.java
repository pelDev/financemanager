package com.example.financemanager.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.financemanager.BR;
import com.example.financemanager.MainActivity;
import com.example.financemanager.R;
import com.example.financemanager.databinding.AddBudgetFragmentBinding;
import com.leinardi.android.speeddial.SpeedDialView;

public class AddBudgetFragment extends Fragment {


    private AddBudgetViewModel mAddBudgetViewModel;
    public AddBudgetFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_budget_fragment, container, false);
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

        mAddBudgetViewModel = new ViewModelProvider(this,
        ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
        .get(AddBudgetViewModel.class);

        binding.setVariable(BR.myAddBudgetViewModel, mAddBudgetViewModel);
    }
}
