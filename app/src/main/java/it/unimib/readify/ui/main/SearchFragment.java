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
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.repository.IBookRepository;
import it.unimib.readify.util.ServiceLocator;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerViewSearchResult;
    private BookSearchResultAdapter bookSearchResultAdapter;
    private List<OLWorkApiResponse> searchResultList;
    private BookViewModel bookViewModel;


    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IBookRepository bookRepository = ServiceLocator.getInstance().getBookRepository(requireActivity().getApplication());
        bookViewModel = new ViewModelProvider(
                requireActivity(),
                new BookViewModelFactory(bookRepository)
        ).get(BookViewModel.class);

        searchResultList = new ArrayList<>();


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerViewSearchResult = rootview.findViewById(R.id.recyclerview_search);
        bookSearchResultAdapter = new BookSearchResultAdapter(requireContext(), searchResultList);
        recyclerViewSearchResult.setAdapter(bookSearchResultAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerViewSearchResult.setLayoutManager(layoutManager);

        MaterialButton filterButton = rootview.findViewById(R.id.button_search_filter);

        FilterBottomSheet filterBottomSheet = new FilterBottomSheet();

        filterButton.setOnClickListener( view -> {
            filterBottomSheet.show(getChildFragmentManager(), filterBottomSheet.getTag());
        });

        return rootview;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the TextInputEditText
        TextInputEditText searchEditText = view.findViewById(R.id.edittext_search);

        // Add an OnEditorActionListener to listen for the "Enter" key press
        searchEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (keyEvent != null && (actionId == KeyEvent.ACTION_DOWN || actionId == KeyEvent.KEYCODE_ENTER ||  actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // Perform the search when the "Enter" key is pressed
                String query = Objects.requireNonNull(searchEditText.getText()).toString();
                Snackbar.make(view, "Query: " + query, Snackbar.LENGTH_SHORT).show();

                if(query.trim().isEmpty()){
                    Snackbar.make(view, getString(R.string.empty_search_snackbar), Snackbar.LENGTH_SHORT).show();
                } else {
                    bookViewModel.searchBooks(query, null).observe(getViewLifecycleOwner(), result -> {
                        if(result.isSuccess()) {
                            Log.d("Result.isSuccess() = true in search fragment", result.toString());
                            Result.SearchSuccess searchResult = ((Result.SearchSuccess) result);
                            Log.d("content",searchResult.getData().toString());
                            int intialSize = this.searchResultList.size();
                            this.searchResultList.clear();
                            this.searchResultList.addAll(searchResult.getData().getWorkList());
                            bookSearchResultAdapter.notifyItemRangeInserted(intialSize, this.searchResultList.size());
                        } else {
                            Log.e("Result.isSuccess() = false in search fragment", result.toString());
                            Snackbar.make(view, "Errore nella ricerca", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                return true;
            }
            return false;
        });



    }







    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //bookViewModel.searchBooks(null,null).removeObservers(getViewLifecycleOwner());
    }

}