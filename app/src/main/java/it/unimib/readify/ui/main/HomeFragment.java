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
import java.util.concurrent.atomic.AtomicInteger;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookRecyclerViewCarouselAdapter;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.repository.IBookRepository;
import it.unimib.readify.util.ServiceLocator;

public class HomeFragment extends Fragment {

    private static final boolean USE_NAVIGATION_COMPONENT = true;

    private Button buttonDaRimuovere;
    private List<OLWorkApiResponse> trendingBookList;

    private List<OLWorkApiResponse> suggestedBookList;
    private BookRecyclerViewCarouselAdapter trendingBooksAdapter;
    private BookRecyclerViewCarouselAdapter suggestedBooksAdapter;


    private BookViewModel trendingBooksViewModel;
    private BookViewModel suggestedBooksViewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IBookRepository bookRepository = ServiceLocator.getInstance().getBookRepository(requireActivity().getApplication());

        trendingBooksViewModel = new ViewModelProvider(
                requireActivity(),
                new BookViewModelFactory(bookRepository)
                ).get(BookViewModel.class);

        suggestedBooksViewModel = new ViewModelProvider(
                requireActivity(), new BookViewModelFactory(bookRepository)
                ).get(BookViewModel.class);


        trendingBookList = new ArrayList<>();
        suggestedBookList = new ArrayList<>();

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

        // RecyclerView for trending carousel
        RecyclerView recyclerViewTrendingBooks = view.findViewById(R.id.trending_container);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        // Create an adapter
        trendingBooksAdapter = new BookRecyclerViewCarouselAdapter(trendingBookList, requireActivity().getApplication(), new BookRecyclerViewCarouselAdapter.OnItemClickListener(){
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
        trendingBooksViewModel.searchBooks("harry+potter", null).observe(getViewLifecycleOwner(), result -> {
            if(result.isSuccess()) {
                //Log.e("controllo", ((Result.SearchSuccess) result).getData().toString());
                int intialSize = this.trendingBookList.size();
                this.trendingBookList.clear();
                this.trendingBookList.addAll(((Result.SearchSuccess) result).getData().getWorkList());
                trendingBooksAdapter.notifyItemRangeInserted(intialSize, this.trendingBookList.size());
            } else {
                Log.e("Result.isSuccess() = false in home fragment", result.toString());
                Snackbar.make(view, "ERRORE", Snackbar.LENGTH_SHORT).show();
            }
        });





        //recyclerview for suggested books
        List<String> mockData = new ArrayList<>();
        mockData.add("/works/OL14933414W");
        mockData.add("/works/OL27479W");
        mockData.add("/works/OL27516W");
        mockData.add("/works/OL262758W");
        mockData.add("/works/OL27658078W");
        mockData.add("/works/OL18146933W");

        // RecyclerView for suggested carousel
        RecyclerView recyclerViewSuggestedBooks = view.findViewById(R.id.suggested_container);
        RecyclerView.LayoutManager suggestedBooksLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        // Create an adapter
        suggestedBooksAdapter = new BookRecyclerViewCarouselAdapter(suggestedBookList, requireActivity().getApplication(), new BookRecyclerViewCarouselAdapter.OnItemClickListener() {
            @Override
            public void onBookItemClick(OLWorkApiResponse book) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("book", book);
                navigateToBookDetailsFragment(bundle);
            }

            @Override
            public void onSaveButtonPressed(int position) {
                //TODO da implementare
            }
        });

        // Set the adapter on the RecyclerView
        recyclerViewSuggestedBooks.setAdapter(suggestedBooksAdapter);
        recyclerViewSuggestedBooks.setLayoutManager(suggestedBooksLayoutManager);

        // view model

        suggestedBooksViewModel.fetchBooks(mockData).observe(getViewLifecycleOwner(), resultList -> {
            AtomicInteger counter = new AtomicInteger(0);
            for(Result result : resultList){
                if(result.isSuccess()) {
                    OLWorkApiResponse suggestedBook = ((Result.WorkSuccess) result).getData();
                    Log.d("suggested "+ suggestedBook.getTitle() + "(" + counter + ")", suggestedBook.toString());
                    this.suggestedBookList.add(suggestedBook);
                    suggestedBooksAdapter.notifyItemInserted(counter.incrementAndGet());
                } else {
                    Log.e("Result.isSuccess() = false in home fragment", result.toString());
                    Snackbar.make(view, "ERRORE", Snackbar.LENGTH_SHORT).show();
                }
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