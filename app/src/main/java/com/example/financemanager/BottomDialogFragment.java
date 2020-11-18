package com.example.financemanager;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

public class BottomDialogFragment extends BottomSheetDialogFragment {

    public static Context sContext;

    public static final String TAG = "CardOptionsDialogFragment";

    private Button deleteButton;
    private int mId;
    private RecyclerView mRecyclerView;

    public static BottomDialogFragment newInstance(Context context) {
        return new BottomDialogFragment();
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
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_layout, container, false);
        deleteButton = view.findViewById(R.id.button_delete_card);
        assert getArguments() != null;
        mId = getArguments().getInt("key");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                CardActivity cardActivity = new CardActivity();
//                cardActivity.deleteCard(mId);
                Uri cardUri = ContentUris.withAppendedId(FinanceManagerProviderContract.Cards.CONTENT_URI, mId);
                sContext.getContentResolver().delete(cardUri, null, null);
                mRecyclerView =  getActivity().findViewById(R.id.recyclerView_cards);
                Snackbar.make(mRecyclerView, "Card Deleted", Snackbar.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}
