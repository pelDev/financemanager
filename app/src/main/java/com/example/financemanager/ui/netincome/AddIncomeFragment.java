package com.example.financemanager.ui.netincome;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.financemanager.MainActivity;
import com.example.financemanager.R;
import com.example.financemanager.database.income.Income;
import com.example.financemanager.databinding.AddIncomeFragmentBinding;
import com.google.android.material.snackbar.Snackbar;
import com.leinardi.android.speeddial.SpeedDialView;

import static com.example.financemanager.BR.myAddIncomeViewModel;
import static com.example.financemanager.Constants.INCOME_ID;

public class AddIncomeFragment extends Fragment {

    private AddIncomeFragmentBinding binding;

    private int mNotePositionArg;
    private NetIncomeViewModel mViewModel;

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
        mNotePositionArg = getArguments() != null ? getArguments().getInt(INCOME_ID) : -1;
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(NetIncomeViewModel.class);
        binding.setVariable(myAddIncomeViewModel, mViewModel);

        NavController controller = NavHostFragment.findNavController(this);
        binding.radioButtonYes.setOnClickListener((v) -> {
                controller.navigate(R.id.actionFrequencyPicker);
                mViewModel.isRecurrent.setValue(true);
        });
        binding.radioButtonNo.setOnClickListener((v) -> mViewModel.isRecurrent.setValue(false));

        setUpReceiverForFrequencyPicker(controller);

        mViewModel.getCompleted().observe(getViewLifecycleOwner(), isCompleted -> {
            if (isCompleted)
                Navigation.findNavController(binding.buttonAddIncome).popBackStack();
        });

        if (mNotePositionArg >= 0) {
           mViewModel.incomePosition = mNotePositionArg;
           Income income = mViewModel.getIncomeById(mNotePositionArg);
           mViewModel.income = income;
           mViewModel.incomeAmount.setValue(Integer.toString(income.getAmount()));
           mViewModel.incomeName.setValue(income.getName());
           mViewModel.setIsRecurrent(income.isRecurrent());
        } else {
            mViewModel.setIsRecurrent(false);
        }

        mViewModel.getInvalidAmount().observe(getViewLifecycleOwner(), (invalid) -> {
            if (invalid) {
                Snackbar.make(binding.buttonAddIncome,
                        "Fill all the information correctly.",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        mViewModel.isRecurrent.observe(getViewLifecycleOwner(), this::setRadioButton);

    }

    private void setUpReceiverForFrequencyPicker(NavController controller) {
        final NavBackStackEntry navBackStackEntry = controller.getBackStackEntry(R.id.addIncomeFragment);

        final LifecycleEventObserver observer = (source, event) -> {
            if (event.equals(Lifecycle.Event.ON_RESUME) &&
                    navBackStackEntry.getSavedStateHandle().contains("frequency")) {
                String result = navBackStackEntry.getSavedStateHandle().get("frequency");
                if (result != null) {
                    Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
                    mViewModel.intervalString = result;
                }
            }
        };

        navBackStackEntry.getLifecycle().addObserver(observer);
        getViewLifecycleOwner().getLifecycle().addObserver((LifecycleEventObserver) (source, event) -> {
            if (event.equals(Lifecycle.Event.ON_DESTROY)) {
                navBackStackEntry.getLifecycle().removeObserver(observer);
            }
        });
    }

    public void setRadioButton(boolean isRecurrent) {
        if (isRecurrent) {
            binding.radioButtonYes.setChecked(true);
        } else
            binding.radioButtonNo.setChecked(true);
    }

}