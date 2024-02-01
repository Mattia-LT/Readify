package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookSearchResultAdapter;
import it.unimib.readify.databinding.FragmentSearchBinding;
import it.unimib.readify.databinding.FragmentTabSearchBooksBinding;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.viewmodel.BookViewModel;

public class TabSearchBooksFragment extends Fragment {

    private FragmentTabSearchBooksBinding fragmentTabSearchBooksBinding;

    private BookSearchResultAdapter searchResultsAdapter;
    private List<OLWorkApiResponse> searchResultList;
    private BookViewModel bookViewModel;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTabSearchBooksBinding = FragmentTabSearchBooksBinding.inflate(inflater,container,false);
        return fragmentTabSearchBooksBinding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        resumeSearch();
        //todo potrebbero esserci soluzioni migliori
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
        searchResultList = new ArrayList<>();
        RecyclerView recyclerViewSearchResults = fragmentTabSearchBooksBinding.recyclerviewSearch;
        searchResultsAdapter = new BookSearchResultAdapter(searchResultList, requireActivity().getApplication(), new BookSearchResultAdapter.OnItemClickListener() {
            @Override
            public void onBookItemClick(OLWorkApiResponse book) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("book", book);

                Navigation.findNavController(requireView()).navigate(R.id.action_searchFragment_to_bookDetailsFragment, bundle);

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
            filterBottomSheet.show(getChildFragmentManager(), filterBottomSheet.getTag());
        });

        fragmentTabSearchBooksBinding.progressindicatorSearchBooks.setVisibility(View.GONE);
        // Add an OnEditorActionListener to listen for the "Enter" key press
        fragmentTabSearchBooksBinding.edittextSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (keyEvent != null && (actionId == KeyEvent.ACTION_DOWN || actionId == KeyEvent.KEYCODE_ENTER ||  actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                fragmentTabSearchBooksBinding.progressindicatorSearchBooks.setVisibility(View.VISIBLE);
                startSearch(view);
                return true;
            }
            return false;
        });
    }




    public void startSearch(View view){
        // Perform the search when the "Enter" key is pressed
        String query = Objects.requireNonNull(fragmentTabSearchBooksBinding.edittextSearch.getText()).toString();
        Snackbar.make(view, "Query: " + query, Snackbar.LENGTH_SHORT).show();
        if(query.trim().isEmpty()){
            Snackbar.make(view, getString(R.string.empty_search_snackbar), Snackbar.LENGTH_SHORT).show();
        } else {
            bookViewModel.searchBooks(query, null).observe(getViewLifecycleOwner(), resultList -> {
                int counter = 0;
                this.searchResultList.clear();
                for(Result result : resultList){
                    if(result.isSuccess()) {
                        OLWorkApiResponse searchedBook = ((Result.WorkSuccess) result).getData();
                        Log.d("book", searchedBook.toString());
                        this.searchResultList.add(searchedBook);
                    } else {
                        Log.e("Result.isSuccess() = false in home fragment", result.toString());
                        Snackbar.make(view, "ERRORE", Snackbar.LENGTH_SHORT).show();
                    }
                }
                searchResultsAdapter.notifyItemRangeChanged(0,searchResultList.size());
                fragmentTabSearchBooksBinding.progressindicatorSearchBooks.setVisibility(View.GONE);
            });
        }
    }



    private void resumeSearch(){
        String query = Objects.requireNonNull(fragmentTabSearchBooksBinding.edittextSearch.getText()).toString();
        if(!query.trim().isEmpty()){
            bookViewModel.searchBooks(query, null).observe(getViewLifecycleOwner(), resultList -> {
                this.searchResultList.clear();
                for(Result result : resultList){
                    if(result.isSuccess()) {
                        OLWorkApiResponse searchedBook = ((Result.WorkSuccess) result).getData();
                        Log.d("book", searchedBook.toString());
                        this.searchResultList.add(searchedBook);
                    } else {
                        Log.e("Result.isSuccess() = false in home fragment", result.toString());
                    }
                }
                searchResultsAdapter.notifyItemRangeChanged(0,searchResultList.size());
                fragmentTabSearchBooksBinding.progressindicatorSearchBooks.setVisibility(View.GONE);
            });
        }

    }

}