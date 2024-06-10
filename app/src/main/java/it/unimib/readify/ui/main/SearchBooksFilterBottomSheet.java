package it.unimib.readify.ui.main;

import static android.view.View.NO_ID;

import static it.unimib.readify.util.Constants.RATING_SORT_SEARCH_MODE;
import static it.unimib.readify.util.Constants.TITLE_SORT_SEARCH_MODE;

import android.content.res.Resources;
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

import it.unimib.readify.R;
import it.unimib.readify.databinding.BottomSheetSearchBooksFilterBinding;
import it.unimib.readify.util.SubjectsUtil;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;

public class SearchBooksFilterBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetSearchBooksFilterBinding binding;
    private BookViewModel bookViewModel;
    private ChipGroup chipGroupGenre;
    private ChipGroup chipGroupSort;
    private Observer<List<String>> genreListObserver;
    private Observer<String> sortModeObserver;

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
        loadSubjectsChips();

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
        SubjectsUtil subjectsUtil = SubjectsUtil.getSubjectsUtil(requireContext());
        List<String> selectedGenres = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
           Chip chip = (Chip) chipGroup.getChildAt(i);
           if(chip != null && chip.isChecked()){
               String chipText = chip.getText().toString().toLowerCase();
               selectedGenres.add(subjectsUtil.getApiValue(chipText));
           }
        }
        if(selectedGenres.isEmpty()){
            selectedGenres = null;
        }
        return selectedGenres;
    }

    private String getSortMode(ChipGroup chipGroup) {
        String selectedSortMode = null;
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if(chip != null && chip.isChecked()){
                if(chip.getId() == R.id.chip_rating_sort){
                    selectedSortMode = RATING_SORT_SEARCH_MODE;
                } else if(chip.getId() == R.id.chip_title_sort){
                    selectedSortMode = TITLE_SORT_SEARCH_MODE;
                }
            }
        }
        return selectedSortMode;
    }

    private void initViewModels(){
        bookViewModel = CustomViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(BookViewModel.class);
    }

    private void initObserver(){
        genreListObserver = genreList -> {
            SubjectsUtil subjectsUtil = SubjectsUtil.getSubjectsUtil(requireContext());
            if(genreList != null && !genreList.isEmpty()){
                List<Integer> chipIds = new ArrayList<>();
                for(String apiValue: genreList){
                    int chipId = subjectsUtil.getChipId(apiValue);
                    if(chipId != NO_ID){
                        chipIds.add(chipId);
                    }
                }
                for (int i = 0; i < chipGroupGenre.getChildCount(); i++) {
                    if (chipGroupGenre.getChildAt(i) instanceof Chip) {
                        Chip chip = (Chip) chipGroupGenre.getChildAt(i);
                        if (chipIds.contains(chip.getId())) {
                            chip.setChecked(true);
                        }
                    }
                }
            }
        };

        sortModeObserver = sortMode -> {
            if(sortMode != null && !sortMode.isEmpty()){
                for (int i = 0; i < chipGroupSort.getChildCount(); i++) {
                    if (chipGroupSort.getChildAt(i) instanceof Chip) {
                        Chip chip = (Chip) chipGroupSort.getChildAt(i);
                        if ( (sortMode.equalsIgnoreCase(TITLE_SORT_SEARCH_MODE) && chip.getId() == R.id.chip_title_sort) ||
                                (sortMode.equalsIgnoreCase(RATING_SORT_SEARCH_MODE) && chip.getId() == R.id.chip_rating_sort)
                        ) {
                            chip.setChecked(true);
                        }
                    }
                }
            }
        };

        bookViewModel.getSortModeLiveData().observe(getViewLifecycleOwner(), sortModeObserver);
        bookViewModel.getSubjectListLiveData().observe(getViewLifecycleOwner(), genreListObserver);
    }

    private void loadSubjectsChips(){
        SubjectsUtil subjectsUtil = SubjectsUtil.getSubjectsUtil(requireContext());
        Resources res = getResources();
        String[] subjects = res.getStringArray(R.array.chip_genres);
        int[] subjectIds = subjectsUtil.getChipIdList();
        for(int i = 0; i < subjects.length; i++) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.single_subject_chip_layout, chipGroupGenre, false);
            chip.setText(subjects[i]);
            chip.setId(subjectIds[i]);
            chipGroupGenre.addView(chip);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bookViewModel.getSortModeLiveData().removeObserver(sortModeObserver);
        bookViewModel.getSubjectListLiveData().removeObserver(genreListObserver);
    }
}
