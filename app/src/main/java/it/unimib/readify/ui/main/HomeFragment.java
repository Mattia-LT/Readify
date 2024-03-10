package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.BUNDLE_BOOK;
import static it.unimib.readify.util.Constants.RECENT;
import static it.unimib.readify.util.Constants.SUGGESTED;
import static it.unimib.readify.util.Constants.TRENDING;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookCarouselAdapter;
import it.unimib.readify.data.repository.user.TestIDatabaseRepository;
import it.unimib.readify.databinding.FragmentHomeBinding;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.data.repository.book.IBookRepository;
import it.unimib.readify.util.TestServiceLocator;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;
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

    private TestDatabaseViewModel testDatabaseViewModel;

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
        trendingBookList = new ArrayList<>();
        suggestedBookList = new ArrayList<>();
        recentBookList = new ArrayList<>();
        Log.d("home fragment", "onCreate");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        Log.d("home fragment", "onCreateView");
        return fragmentHomeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMenu();
        Log.d("home fragment", "onViewCreated");

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

        BookCarouselAdapter.OnItemClickListener onItemClickListener = new BookCarouselAdapter.OnItemClickListener(){
            @Override
            public void onBookItemClick(OLWorkApiResponse book) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(BUNDLE_BOOK, book);
                Navigation.findNavController(requireView()).navigate(R.id.action_homeFragment_to_bookDetailsFragment, bundle);
            }

            @Override
            public void onAddToCollectionButtonPressed(int position) {
                //TODO sistemare, magari se tieni premuto appare un dialog per aggiungerlo
                //bookList.get(position).setFavorite(!newsList.get(position).isFavorite());
                //iNewsRepository.updateNews(newsList.get(position));
            }
        };

        RecyclerView.LayoutManager trendingLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager suggestedLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager recentLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recyclerViewTrendingBooks = fragmentHomeBinding.trendingContainer;
        RecyclerView recyclerViewSuggestedBooks = fragmentHomeBinding.suggestedContainer;
        RecyclerView recyclerViewRecentBooks = fragmentHomeBinding.recentContainer;


        trendingBooksAdapter = new BookCarouselAdapter(trendingBookList, requireActivity().getApplication(), onItemClickListener);
        suggestedBooksAdapter = new BookCarouselAdapter(suggestedBookList, requireActivity().getApplication(), onItemClickListener);
        recentBooksAdapter = new BookCarouselAdapter(recentBookList, requireActivity().getApplication(), onItemClickListener);

        recyclerViewTrendingBooks.setAdapter(trendingBooksAdapter);
        recyclerViewSuggestedBooks.setAdapter(suggestedBooksAdapter);
        recyclerViewRecentBooks.setAdapter(recentBooksAdapter);

        recyclerViewTrendingBooks.setLayoutManager(trendingLayoutManager);
        recyclerViewSuggestedBooks.setLayoutManager(suggestedLayoutManager);
        recyclerViewRecentBooks.setLayoutManager(recentLayoutManager);


        bookViewModel.fetchBooks(mockDataTrending, TRENDING).observe(getViewLifecycleOwner(),resultList -> {
            List<OLWorkApiResponse> workResultList = resultList.stream()
                    .filter(result -> result instanceof Result.WorkSuccess)
                    .map(result -> ((Result.WorkSuccess) result).getData())
                    .collect(Collectors.toList());

            trendingBooksAdapter.refreshList(workResultList);
        });
        bookViewModel.fetchBooks(mockDataSuggested, SUGGESTED).observe(getViewLifecycleOwner(), resultList -> {
            List<OLWorkApiResponse> workResultList = resultList.stream()
                    .filter(result -> result instanceof Result.WorkSuccess)
                    .map(result -> ((Result.WorkSuccess) result).getData())
                    .collect(Collectors.toList());

            suggestedBooksAdapter.refreshList(workResultList);
        });
        bookViewModel.fetchBooks(mockDataRecent, RECENT).observe(getViewLifecycleOwner(), resultList -> {
            List<OLWorkApiResponse> workResultList = resultList.stream()
                    .filter(result -> result instanceof Result.WorkSuccess)
                    .map(result -> ((Result.WorkSuccess) result).getData())
                    .collect(Collectors.toList());

            recentBooksAdapter.refreshList(workResultList);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("home fragment", "onStart");
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d("home fragment", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("home fragment", "onDestroy");
    }





    private void loadMenu(){
        // Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                String title = requireContext().getString(R.string.app_name)
                        .concat(" - ")
                        .concat(requireContext().getString(R.string.navigation_bar_item_home));
                toolbar.setTitle(title);
                menuInflater.inflate(R.menu.default_appbar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

    }

    private void initViewModels(){
        IBookRepository bookRepository = TestServiceLocator
                .getInstance(requireActivity().getApplication())
                .getRepository(IBookRepository.class);

        bookViewModel = new ViewModelProvider
                (
                        requireActivity(),
                        new DataViewModelFactory(bookRepository)
                )
                .get(BookViewModel.class);

        TestIDatabaseRepository testDatabaseRepository = TestServiceLocator
                .getInstance(requireActivity().getApplication())
                .getRepository(TestIDatabaseRepository.class);

        testDatabaseViewModel = TestDatabaseViewModelFactory
                .getInstance(testDatabaseRepository)
                .create(TestDatabaseViewModel.class);

    }



}