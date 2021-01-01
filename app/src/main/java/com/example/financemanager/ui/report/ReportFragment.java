package com.example.financemanager.ui.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.MainActivity;
import com.example.financemanager.R;
import com.example.financemanager.database.expense.Expenditure;
import com.example.financemanager.databinding.ReportFragmentBinding;
import com.google.android.material.card.MaterialCardView;
import com.leinardi.android.speeddial.SpeedDialView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;
import static com.example.financemanager.BR.myReportViewModel;

public class ReportFragment extends Fragment {

    private ExpenditureListAdapter mAdapter;
    private int totalIncome = 0;
    private int totalExpense = 0;
    private double totalExpenseForMonth = 0;
    private double totalIncomeForMonth = 0;
    private ReportFragmentBinding binding;
    private Animation animSlideUp;
    private Animation animBlink;

    public void doSetAmount() {
        binding.textAmountLeft.startAnimation(animBlink);
        binding.textAmountLeft.setText(Integer.toString(totalIncome - totalExpense));
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.report_fragment, container, false);
        binding.setLifecycleOwner(this);
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
        animSlideUp = AnimationUtils.loadAnimation(getContext(), R.anim.scale);
        animBlink = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ReportViewModel reportViewModel = new ViewModelProvider(this,
                AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(ReportViewModel.class);
        binding.setVariable(myReportViewModel, reportViewModel);

        // set up navigation to add income activity
        MaterialCardView netIncomeCard = binding.cardA;
        netIncomeCard.setOnClickListener(Navigation.createNavigateOnClickListener(
                R.id.action_reportFragment_to_incomeListFragment, null));

        // set up recycler view
        mAdapter = new ExpenditureListAdapter(R.layout.item_expenditure);
        RecyclerView recyclerView = binding.listExpenditure;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        LifecycleOwner owner = getViewLifecycleOwner();

        // set up expenditure list observer
        reportViewModel.getAllExpenditures().observe(owner,
                new Observer<List<Expenditure>>() {
                    @Override
                    public void onChanged(List<Expenditure> expenditures) {
                        if (expenditures.size() >= 1) {
                            mAdapter.setExpenditureList(expenditures);
                            binding.emptyView.setVisibility(View.GONE);
                            binding.listExpenditure.setVisibility(View.VISIBLE);
                        } else {
                            binding.emptyView.setVisibility(View.VISIBLE);
                            binding.listExpenditure.setVisibility(View.GONE);
                        }
                    }
                });

        // set up listener for total income for month
        reportViewModel.getnTotalIncomeForMonth().observe(owner,
                new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        if (integer > 0)
                            setIncomeBar();
                        totalIncomeForMonth = (double) integer;
                        doSetPercent();
                    }
                });

        // set up listener for total expense for month
        reportViewModel.getTotalExpenseForMonth().observe(owner,
                new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        if (integer >= 0)
                            setExpenseBar(integer);
                        totalExpenseForMonth = (double) integer;
                        doSetPercent();
                    }
                });

        // set up total income observer
        reportViewModel.getTotalIncome().observe(owner,
                new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        totalIncome = integer;
                        doSetAmount();
                    }
                });

        // set up total expense observer
        reportViewModel.getTotalExpense().observe(owner,
                new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        totalExpense = integer;
                        doSetAmount();
                    }
                });

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String month = getMonthFromInt(calendar.get(Calendar.MONTH));
        int year = calendar.get(Calendar.YEAR);
        binding.homeScreenDate.setText(day + ", " + month + " " + year);
    }

    private String getMonthFromInt(int month) {
        String monthString = "";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (month >= 0 && month <= 11) {
            monthString = months[month];
        }
        return monthString;
    }

    private void setExpenseBar(int integer) {
        double expensePercentOfIncome = (integer / totalIncomeForMonth) * 100;
        int height = (int) Math.round(expensePercentOfIncome);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.view2.getLayoutParams();
        if (height < 1)
            height = 1;
        if (height > 100)
            height = 100;
        params.height = dpToPx(height);
        binding.view2.setLayoutParams(params);
        binding.view2.startAnimation(animSlideUp);
    }

    private void setIncomeBar() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.view.getLayoutParams();
        params.height = dpToPx(100);
        binding.view.setLayoutParams(params);
        binding.view.startAnimation(animSlideUp);
    }

    public final int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }


    private void doSetPercent() {
        double percent = (totalExpenseForMonth / totalIncomeForMonth) * 100;
        if (percent < 0) {
            percent = 0;
        } else if (percent > 100) {
            percent = 100;
        }
        double roundOff = Math.round(percent * 100.0) / 100.0;
        binding.textViewExpInfo.setText("You've spent " + roundOff + "% of your income");
    }

}
