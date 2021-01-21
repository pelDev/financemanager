package com.example.financemanager.ui.netincome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.financemanager.MainActivity;
import com.example.financemanager.R;
import com.example.financemanager.databinding.AddIncomeFragmentBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.leinardi.android.speeddial.SpeedDialView;

import static com.example.financemanager.BR.myAddIncomeViewModel;

public class AddIncomeFragment extends Fragment {

    private NetIncomeViewModel mViewModel;
    private AddIncomeFragmentBinding binding;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(NetIncomeViewModel.class);
        binding.setVariable(myAddIncomeViewModel, mViewModel);



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

        mViewModel.getCompleted().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    Navigation.findNavController(binding.buttonAddIncome).popBackStack();
            }
        });

        mViewModel.getInvalidAmount().observe(getViewLifecycleOwner(), (invalid) -> {
            if (invalid) {
                binding.editTextNumberIncomeAmount.setError("Enter valid amount");
            } else {
                binding.editTextNumberIncomeAmount.setError(null);
            }
        });

    }
}