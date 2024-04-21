package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.databinding.BottomSheetSearchBooksFilterBinding;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class SearchBooksFilterBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetSearchBooksFilterBinding binding;
    private BookViewModel bookViewModel;

    private ChipGroup chipGroupGenre;
    private ChipGroup chipGroupSort;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetSearchBooksFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        if (dialog != null) {
            dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        initViewModels();
        initObserver();

        chipGroupGenre = binding.chipgroupGenreFilter;
        chipGroupSort = binding.chipgroupSort;
        MaterialButton applyButton = binding.buttonApply;
        MaterialButton resetButton = binding.buttonReset;

        applyButton.setOnClickListener(v -> {
            List<String> selectedGenres = getSelectedGenres(chipGroupGenre);
            String sortMode = getSortMode(chipGroupSort);
            bookViewModel.setSortMode(sortMode);
            bookViewModel.setSubjectList(selectedGenres);
            requireDialog().dismiss();
        });

        resetButton.setOnClickListener(v -> {
            chipGroupGenre.clearCheck();
            chipGroupSort.clearCheck();
            bookViewModel.setSubjectList(getSelectedGenres(chipGroupGenre));
            bookViewModel.setSortMode(getSortMode(chipGroupSort));
        });

    }

    private List<String> getSelectedGenres(ChipGroup chipGroup) {
        List<String> selectedChips = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
           Chip chip = (Chip) chipGroup.getChildAt(i);
           if(chip != null && chip.isChecked()){
               selectedChips.add(chip.getText().toString());
           }
        }
        if(selectedChips.isEmpty()){
            selectedChips = null;
        }
        return selectedChips;
    }

    private String getSortMode(ChipGroup chipGroup) {
        String selectedSortMode = null;
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if(chip != null && chip.isChecked()){
                selectedSortMode = chip.getText().toString().toLowerCase();
            }
        }
        return selectedSortMode;
    }

    private void initViewModels(){
        bookViewModel = TestDatabaseViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(BookViewModel.class);
    }

    private void initObserver(){
        final Observer<List<String>> genreListObserver = genreList -> {
            if(genreList != null && !genreList.isEmpty()){
                for (int i = 0; i < chipGroupGenre.getChildCount(); i++) {
                    if (chipGroupGenre.getChildAt(i) instanceof Chip) {
                        Chip chip = (Chip) chipGroupGenre.getChildAt(i);
                        String chipText = chip.getText().toString();
                        if (genreList.contains(chipText)) {
                            chip.setChecked(true);
                        }
                    }
                }
            }
        };

        final Observer<String> sortModeObserver = sortMode -> {
            if(sortMode != null && !sortMode.isEmpty()){
                for (int i = 0; i < chipGroupSort.getChildCount(); i++) {
                    if (chipGroupSort.getChildAt(i) instanceof Chip) {
                        Chip chip = (Chip) chipGroupSort.getChildAt(i);
                        String chipText = chip.getText().toString();
                        if (sortMode.equals(chipText.toLowerCase())) {
                            chip.setChecked(true);
                        }
                    }
                }
            }
        };

        bookViewModel.getSortModeLiveData().observe(getViewLifecycleOwner(), sortModeObserver);
        bookViewModel.getSubjectListLiveData().observe(getViewLifecycleOwner(), genreListObserver);
    }

}
