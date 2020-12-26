package com.example.financemanager.ui.netincome;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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

    }

}