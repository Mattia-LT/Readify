package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookSearchResultAdapter;
import it.unimib.readify.databinding.FragmentTabSearchBooksBinding;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class TabSearchBooksFragment extends Fragment implements FilterBottomSheet.FilterBottomSheetListener {

    private FragmentTabSearchBooksBinding fragmentTabSearchBooksBinding;

    private BookSearchResultAdapter searchResultsAdapter;
    private List<OLWorkApiResponse> searchResultsList;
    private BookViewModel bookViewModel;

    private String sortMode;
    private String subjects;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTabSearchBooksBinding = FragmentTabSearchBooksBinding.inflate(inflater,container,false);
        return fragmentTabSearchBooksBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        initObserver();
        searchResultsList = new ArrayList<>();
        RecyclerView recyclerViewSearchResults = fragmentTabSearchBooksBinding.recyclerviewSearch;
        searchResultsAdapter = new BookSearchResultAdapter(new BookSearchResultAdapter.OnItemClickListener() {
            @Override
            public void onBookItemClick(OLWorkApiResponse book) {
                NavDirections action = SearchFragmentDirections.actionSearchFragmentToBookDetailsFragment(book);
                Navigation.findNavController(requireView()).navigate(action);
            }

            @Override
            public void onAddToCollectionButtonPressed(int position) {

            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerViewSearchResults.setAdapter(searchResultsAdapter);
        recyclerViewSearchResults.setLayoutManager(layoutManager);

        MaterialButton filterButton = fragmentTabSearchBooksBinding.buttonSearchFilter;

        FilterBottomSheet filterBottomSheet = new FilterBottomSheet();
        filterButton.setOnClickListener( v -> {
            filterBottomSheet.setFilterBottomSheetListener(this);
            filterBottomSheet.show(getChildFragmentManager(), filterBottomSheet.getTag());
        });

        // Add an OnEditorActionListener to listen for the "Enter" key press
        fragmentTabSearchBooksBinding.edittextSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (keyEvent != null && (actionId == KeyEvent.ACTION_DOWN || actionId == KeyEvent.KEYCODE_ENTER ||  actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                fragmentTabSearchBooksBinding.progressindicatorSearchBooks.setVisibility(View.VISIBLE);
                startSearch();
                return true;
            }
            return false;
        });
    }

    public void startSearch(){
        // Perform the search when the "Enter" key is pressed
        Editable text = fragmentTabSearchBooksBinding.edittextSearch.getText();
        String query = (text != null) ? text.toString() : "";
        query = query.trim();
        Snackbar.make(requireView(), "Query: " + query, Snackbar.LENGTH_SHORT).show();
        if(query.isEmpty()){
            Snackbar.make(requireView(), getString(R.string.empty_search_snackbar), Snackbar.LENGTH_SHORT).show();
        } else {
            bookViewModel.searchBooks(query, sortMode, subjects);
        }

    }

    @Override
    public void onDataPassed(String sortMode, List<String> subjectsSelected) {
        this.sortMode = sortMode;
        this.subjects = null;
        if(subjectsSelected != null){
            this.subjects = "";
            for(String s: subjectsSelected){
                this.subjects = subjects.concat(s).concat(" ");
            }

        }
    }

    private void initViewModels(){
        bookViewModel = TestDatabaseViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(BookViewModel.class);
    }

    private void initObserver(){
        this.searchResultsList = new ArrayList<>();
        final Observer<List<Result>> searchResultsListObserver = results -> {
            List<OLWorkApiResponse> searchResults = results.stream()
                    .filter(result -> result instanceof Result.WorkSuccess)
                    .map(result -> ((Result.WorkSuccess) result).getData())
                    .collect(Collectors.toList());
            searchResultsList = searchResults;
            searchResultsAdapter.submitList(searchResults);
            fragmentTabSearchBooksBinding.progressindicatorSearchBooks.setVisibility(View.GONE);
        };
        bookViewModel.getSearchResultsLiveData().observe(getViewLifecycleOwner(), searchResultsListObserver);
    }
}