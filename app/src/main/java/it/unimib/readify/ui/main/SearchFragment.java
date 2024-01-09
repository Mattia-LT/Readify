package it.unimib.readify.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookSearchResultAdapter;
import it.unimib.readify.databinding.FragmentSearchBinding;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;

public class SearchFragment extends Fragment {

    private BookSearchResultAdapter searchResultsAdapter;
    private List<OLWorkApiResponse> searchResultList;
    private BookViewModel bookViewModel;

    private FragmentSearchBinding fragmentSearchBinding;


    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
        searchResultList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentSearchBinding = FragmentSearchBinding.inflate(inflater,container,false);
        return fragmentSearchBinding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //todo gestire menu


        RecyclerView recyclerViewSearchResults = fragmentSearchBinding.recyclerviewSearch;
        searchResultsAdapter = new BookSearchResultAdapter(requireContext(), searchResultList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerViewSearchResults.setAdapter(searchResultsAdapter);
        recyclerViewSearchResults.setLayoutManager(layoutManager);

        MaterialButton filterButton = fragmentSearchBinding.buttonSearchFilter;

        FilterBottomSheet filterBottomSheet = new FilterBottomSheet();
        filterButton.setOnClickListener( v -> {
            filterBottomSheet.show(getChildFragmentManager(), filterBottomSheet.getTag());
        });

        fragmentSearchBinding.progressindicatorSearch.setVisibility(View.GONE);
        fragmentSearchBinding.errorScreen.setVisibility(View.GONE);
        // Add an OnEditorActionListener to listen for the "Enter" key press
        fragmentSearchBinding.edittextSearch.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (keyEvent != null && (actionId == KeyEvent.ACTION_DOWN || actionId == KeyEvent.KEYCODE_ENTER ||  actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                fragmentSearchBinding.progressindicatorSearch.setVisibility(View.VISIBLE);
                startSearch(view);
                return true;
            }
            return false;
        });



    }




    public void startSearch(View view){
        // Perform the search when the "Enter" key is pressed
        String query = Objects.requireNonNull(fragmentSearchBinding.edittextSearch.getText()).toString();
        Snackbar.make(view, "Query: " + query, Snackbar.LENGTH_SHORT).show();
        if(query.trim().isEmpty()){
            Snackbar.make(view, getString(R.string.empty_search_snackbar), Snackbar.LENGTH_SHORT).show();
        } else {
            bookViewModel.searchBooks(query, null).observe(getViewLifecycleOwner(), resultList -> {
                int counter = 0;
                this.searchResultList.clear();
                Log.i("SF-BVM-SB-O", "ok");
                for(Result result : resultList){
                    if(result.isSuccess()) {
                        OLWorkApiResponse searchedBook = ((Result.WorkSuccess) result).getData();
                        this.searchResultList.add(searchedBook);
                    } else {
                        Log.e("Result.isSuccess() = false in home fragment", result.toString());
                        Snackbar.make(view, "ERRORE", Snackbar.LENGTH_SHORT).show();
                    }
                }
                searchResultsAdapter.notifyDataSetChanged();
                fragmentSearchBinding.progressindicatorSearch.setVisibility(View.GONE);
            });
        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //bookViewModel.searchBooks(null,null).removeObservers(getViewLifecycleOwner());
    }

}