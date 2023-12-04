package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import it.unimib.readify.R;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_search_filter, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the BottomSheetBehavior
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());

        // Set the state to EXPANDED
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }




}
