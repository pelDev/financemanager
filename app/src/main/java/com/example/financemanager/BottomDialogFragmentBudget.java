package com.example.financemanager;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

public class BottomDialogFragmentBudget extends BottomSheetDialogFragment {

    public static Context sContext;

    public static final String TAG = "BudgetOptionsDialogFragment";

    private TextView mJanuary;
    private TextView mFebruary;
    private TextView mMarch;
    private TextView mApril;
    private TextView mMay;
    private TextView mJune;
    private TextView mJuly;
    private TextView mAugust;
    private TextView mSeptember;
    private TextView mOctober;
    private TextView mNovember;
    private TextView mDecember;
    //private int mId;
    //private RecyclerView mRecyclerView;

    public static BottomDialogFragmentBudget newInstance(Context context) {
        return new BottomDialogFragmentBudget();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.sContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_budget_layout, container, false);
        mJanuary = view.findViewById(R.id.date_january);
        mFebruary = view.findViewById(R.id.date_february);
        mMarch = view.findViewById(R.id.date_march);
        mApril = view.findViewById(R.id.date_april);
        mMay = view.findViewById(R.id.date_may);
        mJune = view.findViewById(R.id.date_june);
        mJuly = view.findViewById(R.id.date_july);
        mAugust = view.findViewById(R.id.date_august);
        mSeptember = view.findViewById(R.id.date_september);
        mOctober = view.findViewById(R.id.date_october);
        mNovember = view.findViewById(R.id.date_november);
        mDecember = view.findViewById(R.id.date_december);
//        assert getArguments() != null;
//        mId = getArguments().getInt("key");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mJanuary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BudgetActivity) getActivity()).setNewMonth("January");
                dismiss();
            }
        });
        mFebruary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BudgetActivity) getActivity()).setNewMonth("February");
                dismiss();
            }
        });
        mMarch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BudgetActivity) getActivity()).setNewMonth("March");
                dismiss();
            }
        });
        mApril.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BudgetActivity) getActivity()).setNewMonth("April");
                dismiss();
            }
        });
        mMay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BudgetActivity) getActivity()).setNewMonth("May");
                dismiss();
            }
        });
        mJune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BudgetActivity) getActivity()).setNewMonth("June");
                dismiss();
            }
        });
        mJuly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BudgetActivity) getActivity()).setNewMonth("July");
                dismiss();
            }
        });
        mAugust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BudgetActivity) getActivity()).setNewMonth("August");
                dismiss();
            }
        });
        mSeptember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BudgetActivity) getActivity()).setNewMonth("September");
                dismiss();
            }
        });
        mOctober.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BudgetActivity) getActivity()).setNewMonth("October");
                dismiss();
            }
        });
        mNovember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BudgetActivity) getActivity()).setNewMonth("November");
                dismiss();
            }
        });
        mDecember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BudgetActivity) getActivity()).setNewMonth("December");
                dismiss();
            }
        });
    }
}
