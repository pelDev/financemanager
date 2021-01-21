package com.example.financemanager.ui.netincome;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.financemanager.MainActivity;
import com.example.financemanager.R;
import com.example.financemanager.database.income.Income;
import com.example.financemanager.databinding.AddIncomeFragmentBinding;
import com.leinardi.android.speeddial.SpeedDialView;

import static com.example.financemanager.BR.myAddIncomeViewModel;
import static com.example.financemanager.Constants.INCOME_ID;

public class AddIncomeFragment extends Fragment {

    private AddIncomeFragmentBinding binding;

    private Boolean isRecurrent = false;
    private int mNotePositionArg = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.add_income_fragment, container, false);
        binding.setLifecycleOwner(this);
        final SpeedDialView floatingActionButton = ((MainActivity) getActivity()).getSpeedDialView();

        if (floatingActionButton != null) {
            ((MainActivity) getActivity()).hideSpeedDialView();
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mNotePositionArg = getArguments() != null ? getArguments().getInt(INCOME_ID) : 0;
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        NetIncomeViewModel viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(NetIncomeViewModel.class);
        binding.setVariable(myAddIncomeViewModel, viewModel);

        NavController controller = NavHostFragment.findNavController(this);
        binding.radioButtonYes.setOnClickListener((v) -> controller.navigate(R.id.actionFrequencyPicker));
        final NavBackStackEntry navBackStackEntry = controller.getBackStackEntry(R.id.addIncomeFragment);

        final LifecycleEventObserver observer = (LifecycleEventObserver) (source, event) -> {
            if (event.equals(Lifecycle.Event.ON_RESUME) &&
                    navBackStackEntry.getSavedStateHandle().contains("frequency")) {
                String result = navBackStackEntry.getSavedStateHandle().get("frequency");
                if (result != null)
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
            }
        };

        navBackStackEntry.getLifecycle().addObserver(observer);
        getViewLifecycleOwner().getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event.equals(Lifecycle.Event.ON_DESTROY)) {
                navBackStackEntry.getLifecycle().removeObserver(observer);
            }
        });

        viewModel.getCompleted().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    Navigation.findNavController(binding.buttonAddIncome).popBackStack();
            }
        });

        if (mNotePositionArg >= 0) {
            viewModel.incomePosition = mNotePositionArg;
           Income income = viewModel.getIncomeById(mNotePositionArg);
           viewModel.income = income;
           viewModel.incomeAmount.setValue(Integer.toString(income.getAmount()));
//           if (income.isRecurrent()) {
//               binding.radioButtonYes.setChecked(true);
//           } else {
//               binding.radioButtonNo.setChecked(true);
//           }
            viewModel.setIsRecurrent(income.isRecurrent());
        } else {
            viewModel.setIsRecurrent(isRecurrent);
        }

        viewModel.getInvalidAmount().observe(getViewLifecycleOwner(), (invalid) -> {
            if (invalid) {
                binding.editTextNumberIncomeAmount.setError("Enter valid amount");
            } else {
                binding.editTextNumberIncomeAmount.setError(null);
            }
        });

        viewModel.isRecurrent.observe(getViewLifecycleOwner(), this::setRadioButton);

    }

    public void setRadioButton(boolean isRecurrent) {
        if (isRecurrent) {
            binding.radioButtonYes.setChecked(true);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.actionFrequencyPicker);
        } else
            binding.radioButtonNo.setChecked(true);
    }

}