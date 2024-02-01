package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.RECENT;
import static it.unimib.readify.util.Constants.SUGGESTED;
import static it.unimib.readify.util.Constants.TRENDING;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import it.unimib.readify.adapter.BookCarouselAdapter;
import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.databinding.FragmentHomeBinding;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.data.repository.book.IBookRepository;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.UserViewModelFactory;
import it.unimib.readify.util.ServiceLocator;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.DataViewModelFactory;

public class HomeFragment extends Fragment {
    private List<OLWorkApiResponse> trendingBookList;
    private List<OLWorkApiResponse> suggestedBookList;
    private List<OLWorkApiResponse> recentBookList;
    private BookCarouselAdapter trendingBooksAdapter;
    private BookCarouselAdapter suggestedBooksAdapter;
    private BookCarouselAdapter recentBooksAdapter;
    private FragmentHomeBinding fragmentHomeBinding;

    private BookViewModel bookViewModel;
    private UserViewModel userViewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance()
                .getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        IBookRepository bookRepository = ServiceLocator.getInstance()
                .getBookRepository(requireActivity().getApplication());
        bookViewModel = new ViewModelProvider(
                requireActivity(),
                new DataViewModelFactory(bookRepository)
                ).get(BookViewModel.class);

        trendingBookList = new ArrayList<>();
        suggestedBookList = new ArrayList<>();
        recentBookList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        return fragmentHomeBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("user info", userViewModel.getLoggedUser().toString());


        //setting mock data
        List<String> mockDataSuggested = new ArrayList<>();
        mockDataSuggested.add("/works/OL14933414W");
        mockDataSuggested.add("/works/OL27479W");
        mockDataSuggested.add("/works/OL27516W");
        mockDataSuggested.add("/works/OL262758W");
        mockDataSuggested.add("/works/OL27658078W");
        mockDataSuggested.add("/works/OL18146933W");

        List<String> mockDataTrending = new ArrayList<>();
        mockDataTrending.add("/works/OL5735363W");
        mockDataTrending.add("/works/OL15413843W");
        mockDataTrending.add("/works/OL15518787W");
        mockDataTrending.add("/works/OL5735360W");

        List<String> mockDataRecent = new ArrayList<>();
        mockDataRecent.add("/works/OL82563W");
        mockDataRecent.add("/works/OL82586W");
        mockDataRecent.add("/works/OL82548W");
        mockDataRecent.add("/works/OL82537W");
        mockDataRecent.add("/works/OL82536W");
        mockDataRecent.add("/works/OL82565W");
        mockDataRecent.add("/works/OL82560W");
        mockDataRecent.add("/works/OL20874116W");


        // RecyclerView for trending carousel
        RecyclerView recyclerViewTrendingBooks = fragmentHomeBinding.trendingContainer;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        // Create an adapter
        trendingBooksAdapter = new BookCarouselAdapter(trendingBookList, requireActivity().getApplication(), new BookCarouselAdapter.OnItemClickListener(){
            @Override
            public void onBookItemClick(OLWorkApiResponse book) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("book", book);
                navigateToBookDetailsFragment(bundle);
            }

            @Override
            public void onAddToCollectionButtonPressed(int position) {
                //TODO sistemare, magari se tieni premuto appare un dialog per aggiungerlo
                //bookList.get(position).setFavorite(!newsList.get(position).isFavorite());
                //iNewsRepository.updateNews(newsList.get(position));
            }
        });

        // Set the adapter on the RecyclerView
        recyclerViewTrendingBooks.setAdapter(trendingBooksAdapter);
        recyclerViewTrendingBooks.setLayoutManager(layoutManager);

        bookViewModel.fetchBooks(mockDataTrending, TRENDING).observe(getViewLifecycleOwner(), resultList -> {
            int counter = 0;
            for(Result result : resultList){
                if(result.isSuccess()) {
                    OLWorkApiResponse trendingBook = ((Result.WorkSuccess) result).getData();
                    //Log.d("trending "+ trendingBook.getTitle(), trendingBook.toString());
                    this.trendingBookList.add(trendingBook);
                    trendingBooksAdapter.notifyItemInserted(counter++);
                } else {
                    //Log.e("Result.isSuccess() = false in home fragment", result.toString());
                    Snackbar.make(view, "ERRORE", Snackbar.LENGTH_SHORT).show();
                }
            }
        });


        /*
        bookViewModel.searchBooks("harry+potter", null).observe(getViewLifecycleOwner(), result -> {
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
        */

        // RecyclerView for suggested carousel
        RecyclerView recyclerViewSuggestedBooks =fragmentHomeBinding.suggestedContainer;
        RecyclerView.LayoutManager suggestedBooksLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        // Create an adapter
        suggestedBooksAdapter = new BookCarouselAdapter(suggestedBookList, requireActivity().getApplication(), new BookCarouselAdapter.OnItemClickListener() {
            @Override
            public void onBookItemClick(OLWorkApiResponse book) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("book", book);
                navigateToBookDetailsFragment(bundle);
            }

            @Override
            public void onAddToCollectionButtonPressed(int position) {
                //TODO da implementare
            }
        });

        // Set the adapter on the RecyclerView
        recyclerViewSuggestedBooks.setAdapter(suggestedBooksAdapter);
        recyclerViewSuggestedBooks.setLayoutManager(suggestedBooksLayoutManager);

        // view model

        bookViewModel.fetchBooks(mockDataSuggested, SUGGESTED).observe(getViewLifecycleOwner(), resultList -> {
            int counter = 0;
            for(Result result : resultList){
                if(result.isSuccess()) {
                    OLWorkApiResponse suggestedBook = ((Result.WorkSuccess) result).getData();
                    //Log.d("suggested "+ suggestedBook.getTitle() + "(" + counter + ")", suggestedBook.toString());
                    this.suggestedBookList.add(suggestedBook);
                    suggestedBooksAdapter.notifyItemInserted(counter++);
                } else {
                    //Log.e("Result.isSuccess() = false in home fragment", result.toString());
                    Snackbar.make(view, "ERRORE", Snackbar.LENGTH_SHORT).show();
                }
            }
        });


        // recycler view for recent books

        RecyclerView recyclerViewRecentBooks = fragmentHomeBinding.recentContainer;
        RecyclerView.LayoutManager recentBooksLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        // Create an adapter
        recentBooksAdapter = new BookCarouselAdapter(recentBookList, requireActivity().getApplication(), new BookCarouselAdapter.OnItemClickListener() {
            @Override
            public void onBookItemClick(OLWorkApiResponse book) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("book", book);
                navigateToBookDetailsFragment(bundle);
            }

            @Override
            public void onAddToCollectionButtonPressed(int position) {
                //TODO da implementare
            }
        });

        // Set the adapter on the RecyclerView
        recyclerViewRecentBooks.setAdapter(recentBooksAdapter);
        recyclerViewRecentBooks.setLayoutManager(recentBooksLayoutManager);

        // view model

        bookViewModel.fetchBooks(mockDataRecent, RECENT).observe(getViewLifecycleOwner(), resultList -> {
            int counter = 0;
            for(Result result : resultList){
                if(result.isSuccess()) {
                    OLWorkApiResponse recentBook = ((Result.WorkSuccess) result).getData();
                    //Log.d("recent "+ recentBook.getTitle() + "(" + counter + ")", recentBook.toString());
                    this.recentBookList.add(recentBook);
                    recentBooksAdapter.notifyItemInserted(counter++);
                } else {
                    //Log.e("Result.isSuccess() = false in home fragment", result.toString());
                    Snackbar.make(view, "ERRORE", Snackbar.LENGTH_SHORT).show();
                }
            }
        });







    }

    private void navigateToBookDetailsFragment() {
        Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_bookDetailsFragment);
    }

    private void navigateToBookDetailsFragment(Bundle bundle) {
        Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_bookDetailsFragment, bundle);
    }
}