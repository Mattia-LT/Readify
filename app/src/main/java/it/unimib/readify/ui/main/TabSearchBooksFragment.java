package it.unimib.readify.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.stream.Collectors;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookSearchResultAdapter;
import it.unimib.readify.databinding.FragmentTabSearchBooksBinding;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class TabSearchBooksFragment extends Fragment{

    private FragmentTabSearchBooksBinding fragmentTabSearchBooksBinding;

    private BookSearchResultAdapter searchResultsAdapter;
    private BookViewModel bookViewModel;
    private TestDatabaseViewModel testDatabaseViewModel;
    private String sortMode;
    private String subjects;
    private User loggedUser;
    private boolean isLoading;
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
        RecyclerView recyclerViewSearchResults = fragmentTabSearchBooksBinding.recyclerviewSearch;
        searchResultsAdapter = new BookSearchResultAdapter(new BookSearchResultAdapter.OnItemClickListener() {
            @Override
            public void onBookItemClick(OLWorkApiResponse book) {
                NavDirections action = SearchFragmentDirections.actionSearchFragmentToBookDetailsFragment(book);
                Navigation.findNavController(requireView()).navigate(action);
            }

            @Override
            public void onAddToCollectionButtonPressed(OLWorkApiResponse book) {
                NavDirections action = SearchFragmentDirections.actionSearchFragmentToAddToCollectionDialog(book.getKey(), loggedUser.getIdToken());
                Navigation.findNavController(requireView()).navigate(action);
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerViewSearchResults.setAdapter(searchResultsAdapter);
        recyclerViewSearchResults.setLayoutManager(layoutManager);

        recyclerViewSearchResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        fragmentTabSearchBooksBinding.progressindicatorSearchBooks.setVisibility(View.VISIBLE);
                        bookViewModel.loadMoreSearchResults();
                        // Flag to prevent multiple load requests
                        isLoading = true;
                    }
                }
            }
        });



        MaterialButton filterButton = fragmentTabSearchBooksBinding.buttonSearchFilter;

        filterButton.setOnClickListener( v -> {
            NavDirections action = SearchFragmentDirections.actionSearchFragmentToFilterBottomsheet();
            Navigation.findNavController(requireView()).navigate(action);
        });

        // Add an OnEditorActionListener to listen for the "Enter" key press
        fragmentTabSearchBooksBinding.edittextSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (keyEvent != null && (actionId == KeyEvent.ACTION_DOWN || actionId == KeyEvent.KEYCODE_ENTER ||  actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                fragmentTabSearchBooksBinding.progressindicatorSearchBooks.setVisibility(View.VISIBLE);
                fragmentTabSearchBooksBinding.noResultsFoundLayout.setVisibility(View.GONE);
                InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
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
        if(query.isEmpty()){
            Snackbar.make(requireView(), getString(R.string.empty_search_snackbar), Snackbar.LENGTH_SHORT).show();
        } else {
            bookViewModel.searchBooks(query, sortMode, subjects);
        }

    }

    private void initViewModels(){
        bookViewModel = TestDatabaseViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(BookViewModel.class);

        testDatabaseViewModel = TestDatabaseViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);
    }

    private void initObserver(){
        final Observer<List<Result>> searchResultsListObserver = results -> {
            ConstraintLayout noResultsFoundLayout = fragmentTabSearchBooksBinding.noResultsFoundLayout;
            if(results.isEmpty()){
                noResultsFoundLayout.setVisibility(View.VISIBLE);
            } else {
                noResultsFoundLayout.setVisibility(View.GONE);
            }

            List<OLWorkApiResponse> searchResults = results.stream()
                    .filter(result -> result instanceof Result.WorkSuccess)
                    .map(result -> ((Result.WorkSuccess) result).getData())
                    .collect(Collectors.toList());


            searchResultsAdapter.submitList(searchResults);
            fragmentTabSearchBooksBinding.progressindicatorSearchBooks.setVisibility(View.GONE);
            isLoading = false;
        };

        final Observer<List<String>> genreListObserver = genreList -> {
            this.subjects = null;
            if(genreList != null && !genreList.isEmpty()){
                this.subjects = "";
                for(String genre : genreList){
                    this.subjects = subjects.concat(genre).concat(" ");
                }
            }
        };

        final Observer<String> sortModeObserver = sortMode -> this.sortMode = sortMode;

        final Observer<Result> userObserver = loggedUserResult -> {
            if(loggedUserResult.isSuccess()){
                loggedUser = ((Result.UserSuccess) loggedUserResult).getData();
            }
        };

        bookViewModel.getSearchResultsLiveData().observe(getViewLifecycleOwner(), searchResultsListObserver);
        bookViewModel.getSortModeLiveData().observe(getViewLifecycleOwner(), sortModeObserver);
        bookViewModel.getSubjectListLiveData().observe(getViewLifecycleOwner(), genreListObserver);
        testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), userObserver);
    }
}