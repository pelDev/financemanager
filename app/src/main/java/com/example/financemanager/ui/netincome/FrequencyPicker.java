package com.example.financemanager.ui.netincome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.financemanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class FrequencyPicker extends BottomSheetDialogFragment implements View.OnClickListener {

    public static FrequencyPicker getInstance() {
        return new FrequencyPicker();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frequency_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.textView_daily).setOnClickListener(this);
        view.findViewById(R.id.textView_weekly).setOnClickListener(this);
        view.findViewById(R.id.textView_monthly).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        TextView tvSelected = (TextView) v;
        NavController controller = NavHostFragment.findNavController(this);
        Objects.requireNonNull(controller.getPreviousBackStackEntry())
                .getSavedStateHandle().set("frequency", tvSelected.getText().toString());
        dismiss();
    }
}
