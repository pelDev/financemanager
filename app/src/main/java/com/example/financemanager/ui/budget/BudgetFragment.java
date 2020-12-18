package com.example.financemanager.ui.budget;

import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.financemanager.R;

public class BudgetFragment extends Fragment {

    private BudgetViewModel mViewModel;

    public static BudgetFragment newInstance() {
        return new BudgetFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.budget_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BudgetViewModel.class);


    }

    public interface OnFragmentInteractionListener {
        void onBudgetFragmentInteraction(Uri uri);
    }
}