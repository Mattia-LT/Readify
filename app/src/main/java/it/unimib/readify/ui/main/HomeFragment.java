package it.unimib.readify.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookRecyclerViewCarouselAdapter;
import it.unimib.readify.model.OLDocs;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.repository.BookRepository;
import it.unimib.readify.repository.IBookRepository;
import it.unimib.readify.util.ResponseCallback;
import it.unimib.readify.util.ServiceLocator;

public class HomeFragment extends Fragment {

    private static final boolean USE_NAVIGATION_COMPONENT = true;

    private Button buttonDaRimuovere;
    private RecyclerView recyclerViewTrendingBooks;
    private LinearLayoutManager layoutManager;
    private List<OLWorkApiResponse> bookList;
    private BookRecyclerViewCarouselAdapter trendingBooksAdapter;


    private BooksViewModel booksViewModel;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookList = new ArrayList<>();

        IBookRepository booksRepository =
                    ServiceLocator.getInstance().getBookRepository();

        booksViewModel = new ViewModelProvider(
                requireActivity(),
                new BooksViewModelFactory(booksRepository)).get(BooksViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //todo controllo

        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO da rimuovere
        buttonDaRimuovere = view.findViewById(R.id.button_da_rimuovere);
        buttonDaRimuovere.setOnClickListener(v -> {
            navigateToBookDetailsFragment();
        });

        // RecyclerView for trending carousel
        recyclerViewTrendingBooks = view.findViewById(R.id.trending_container);
        layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        // Create an adapter
        trendingBooksAdapter = new BookRecyclerViewCarouselAdapter(bookList, requireActivity().getApplication(), new BookRecyclerViewCarouselAdapter.OnItemClickListener(){
            @Override
            public void onBookItemClick(OLWorkApiResponse book) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("book", book);
                navigateToBookDetailsFragment(bundle);
            }

            @Override
            public void onSaveButtonPressed(int position) {
                //TODO sistemare
                //bookList.get(position).setFavorite(!newsList.get(position).isFavorite());
                //iNewsRepository.updateNews(newsList.get(position));
            }
        });

        // Set the adapter on the RecyclerView
        recyclerViewTrendingBooks.setAdapter(trendingBooksAdapter);
        recyclerViewTrendingBooks.setLayoutManager(layoutManager);


        // view model
        booksViewModel.searchBooks("country", null).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        int initialSize = this.bookList.size();
                        this.bookList.clear();
                        List<OLDocs> docsList = ((Result.Success) result).getData().getDocs();
                        for (OLDocs oldoc: docsList) {
                            booksViewModel.fetchBook(oldoc.getKey());
                        }
                        trendingBooksAdapter.notifyItemRangeInserted(initialSize, this.bookList.size());

                    } else {
                        Snackbar.make(view, 0, Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToBookDetailsFragment() {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_bookDetailsFragment);

        } else {
            Intent intent = new Intent(requireContext(), HomeActivity.class);
            startActivity(intent);
            //requireActivity().finish();
        }
    }

    private void navigateToBookDetailsFragment(Bundle bundle) {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_bookDetailsFragment, bundle);
        } else {
            Intent intent = new Intent(requireContext(), HomeActivity.class);
            startActivity(intent);
            //requireActivity().finish();
        }
    }
}