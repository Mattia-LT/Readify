package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.databinding.BottomSheetSearchFilterBinding;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    private FilterBottomSheetListener mListener;
    private BottomSheetSearchFilterBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetSearchFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the BottomSheetBehavior
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        // Set the state to EXPANDED
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);


        ChipGroup chipGroupGenre = binding.chipgroupGenreFilter;
        MaterialButton applyButton = binding.buttonApply;
        ChipGroup chipGroupSort = binding.chipgroupSort;

      
        applyButton.setOnClickListener(v -> {
            List<String> selectedGenres = getSelectedChips(chipGroupGenre);
            String sortMode = getSortMode(chipGroupSort);
            if(mListener != null){
                mListener.onDataPassed(sortMode,selectedGenres);
            }
            requireDialog().dismiss();
        });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public interface FilterBottomSheetListener {
        void onDataPassed(String sortMode, List<String> genres);

    }

    public void setFilterBottomSheetListener(FilterBottomSheetListener listener){
        this.mListener = listener;
    }

    private List<String> getSelectedChips(ChipGroup chipGroup) {
        List<String> selectedChips = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
           Chip chip = (Chip) chipGroup.getChildAt(i);
           if(chip != null && chip.isChecked()){
               selectedChips.add(chip.getText().toString());
           }
        }
        return selectedChips;
    }

    private String getSortMode(ChipGroup chipGroup) {
        String selectedSortMode = null;
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if(chip != null && chip.isChecked()){
                selectedSortMode = chip.getText().toString();
            }
        }
        return selectedSortMode;
    }

}
