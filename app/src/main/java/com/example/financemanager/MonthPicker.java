package com.example.financemanager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MonthPicker extends BottomSheetDialogFragment implements View.OnClickListener {

    public static final String TAG = "MonthPickerFragment";
//    private ItemClickListener mListener;

    public static MonthPicker getInstance() {
        return new MonthPicker();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheetlayout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.textView).setOnClickListener(this);
        view.findViewById(R.id.textView_feb).setOnClickListener(this);
        view.findViewById(R.id.textView_mar).setOnClickListener(this);
        view.findViewById(R.id.textView_Apr).setOnClickListener(this);
        view.findViewById(R.id.textView_May).setOnClickListener(this);
        view.findViewById(R.id.textView_June).setOnClickListener(this);
        view.findViewById(R.id.textView_Jul).setOnClickListener(this);
        view.findViewById(R.id.textView_Aug).setOnClickListener(this);
        view.findViewById(R.id.textView_Sep).setOnClickListener(this);
        view.findViewById(R.id.textView_Oct).setOnClickListener(this);
        view.findViewById(R.id.textView_Nov).setOnClickListener(this);
        view.findViewById(R.id.textView_Dec).setOnClickListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        if (context instanceof ItemClickListener) {
//            mListener = (ItemClickListener) context;
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onClick(View v) {
        TextView tvSelected = (TextView) v;
        NavController controller = NavHostFragment.findNavController(this);
        controller.getPreviousBackStackEntry().getSavedStateHandle().set("key", tvSelected.getText().toString());
//        mListener.onItemClick(tvSelected.getText().toString());
        dismiss();
    }

//    public interface ItemClickListener {
//        void onItemClick(String item);
//    }

}
