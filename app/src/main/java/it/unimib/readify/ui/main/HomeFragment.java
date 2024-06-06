package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.DARK_MODE;
import static it.unimib.readify.util.Constants.LIGHT_MODE;
import static it.unimib.readify.util.Constants.PREFERRED_THEME;
import static it.unimib.readify.util.Constants.SHARED_PREFERENCES_FILE_NAME;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookCarouselAdapter;
import it.unimib.readify.databinding.FragmentHomeBinding;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.SharedPreferencesUtil;
import it.unimib.readify.viewmodel.CollectionViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.BookViewModel;

public class HomeFragment extends Fragment {
    private BookCarouselAdapter trendingBooksAdapter;
    private BookCarouselAdapter recommendedBooksAdapter;
    private BookCarouselAdapter recentBooksAdapter;
    private FragmentHomeBinding fragmentHomeBinding;
    private BookViewModel bookViewModel;
    private UserViewModel userViewModel;
    private CollectionViewModel collectionViewModel;
    private User user;

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Log.d("home fragment", "onViewCreated");
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());
        //Load user preferences for theme
        String preferredTheme = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, PREFERRED_THEME);
        if(preferredTheme != null) {
            int currentTheme = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (preferredTheme.equals(DARK_MODE) && currentTheme != Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (preferredTheme.equals(LIGHT_MODE) && currentTheme != Configuration.UI_MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        initViewModels();
        initObservers();

        fragmentHomeBinding.progressbarRecent.setVisibility(View.VISIBLE);
        fragmentHomeBinding.progressbarTrending.setVisibility(View.VISIBLE);
        fragmentHomeBinding.progressbarRecommended.setVisibility(View.VISIBLE);

        BookCarouselAdapter.OnItemClickListener onItemClickListener = book -> {
            NavDirections action = HomeFragmentDirections.actionHomeFragmentToBookDetailsFragment(book);
            Navigation.findNavController(requireView()).navigate(action);
        };

        RecyclerView.LayoutManager trendingLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager recommendedLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager recentLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recyclerViewTrendingBooks = fragmentHomeBinding.trendingContainer;
        RecyclerView recyclerViewRecommendedBooks = fragmentHomeBinding.recommendedContainer;
        RecyclerView recyclerViewRecentBooks = fragmentHomeBinding.recentContainer;

        trendingBooksAdapter = new BookCarouselAdapter(onItemClickListener);
        recommendedBooksAdapter = new BookCarouselAdapter(onItemClickListener);
        recentBooksAdapter = new BookCarouselAdapter(onItemClickListener);

        recyclerViewTrendingBooks.setAdapter(trendingBooksAdapter);
        recyclerViewRecommendedBooks.setAdapter(recommendedBooksAdapter);
        recyclerViewRecentBooks.setAdapter(recentBooksAdapter);

        recyclerViewTrendingBooks.setLayoutManager(trendingLayoutManager);
        recyclerViewRecommendedBooks.setLayoutManager(recommendedLayoutManager);
        recyclerViewRecentBooks.setLayoutManager(recentLayoutManager);
    }

    private void initViewModels(){
        bookViewModel = CustomViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(BookViewModel.class);

        userViewModel = CustomViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);

        collectionViewModel = CustomViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(CollectionViewModel.class);
    }

    private void initObservers(){
        Observer<List<Result>> recommendedBooksObserver = results -> {
            Log.d("recommended observer", "triggered");
            if(results != null) {
                List<OLWorkApiResponse> workResultList = results.stream()
                        .filter(result -> result instanceof Result.WorkSuccess)
                        .map(result -> ((Result.WorkSuccess) result).getData())
                        .collect(Collectors.toList());
                fragmentHomeBinding.progressbarRecommended.setVisibility(View.GONE);
                recommendedBooksAdapter.submitList(workResultList);
            }
        };

        Observer<List<Result>> trendingBooksObserver = results -> {
            Log.d("trending observer", "triggered");
            if(results != null) {
                List<OLWorkApiResponse> workResultList = results.stream()
                        .filter(result -> result instanceof Result.WorkSuccess)
                        .map(result -> ((Result.WorkSuccess) result).getData())
                        .collect(Collectors.toList());
                fragmentHomeBinding.progressbarTrending.setVisibility(View.GONE);
                trendingBooksAdapter.submitList(workResultList);
            }
        };

        Observer<List<Result>> recentBooksObserver = results -> {
            Log.d("recent observer", "triggered");
            if(results != null){
                List<OLWorkApiResponse> workResultList = results.stream()
                        .filter(result -> result instanceof Result.WorkSuccess)
                        .map(result -> ((Result.WorkSuccess) result).getData())
                        .collect(Collectors.toList());
                fragmentHomeBinding.progressbarRecent.setVisibility(View.GONE);
                recentBooksAdapter.submitList(workResultList);
            }
        };


        Observer<Result> loggedUserObserver = result -> {
            Log.d("BookDetails fragment", "user changed");
            if(result.isSuccess()) {
                user = ((Result.UserSuccess) result).getData();
                if(user.getUsername() != null && user.getIdToken() != null && userViewModel.isFirstLoading()){
                    userViewModel.setFirstLoading(false);
                    collectionViewModel.resetLogout();
                    collectionViewModel.fetchLoggedUserCollections(user.getIdToken());
                    bookViewModel.loadRecommendedBooks(user.getRecommended());
                    bookViewModel.loadTrendingBooks();
                    bookViewModel.loadRecentBooks();
                    Log.d("homeFragment","Sono entrato nell'if + " + user.getIdToken());
                }

                if(user.getUsername() != null) {
                    String welcomeMessage = getString(R.string.placeholder_home_welcome)
                            .concat(" ")
                            .concat(user.getUsername())
                            .concat(" ")
                            .concat("\uD83D\uDC4B");
                    fragmentHomeBinding.textviewHomeTitle.setText(welcomeMessage);
                }
            }
        };
        //get user data from database
        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
        bookViewModel.getRecommendedCarouselLiveData().observe(getViewLifecycleOwner(), recommendedBooksObserver);
        bookViewModel.getTrendingCarouselLiveData().observe(getViewLifecycleOwner(), trendingBooksObserver);
        bookViewModel.getRecentCarouselLiveData().observe(getViewLifecycleOwner(), recentBooksObserver);
    }
}