package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.BUNDLE_USER;
import static it.unimib.readify.util.Constants.COLLECTION;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.data.repository.book.IBookRepository;
import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.databinding.FragmentUserDetailsBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.ServiceLocator;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.DataViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.UserViewModelFactory;

public class UserDetailsFragment extends Fragment {

    private FragmentUserDetailsBinding binding;
    private UserViewModel userViewModel;
    private BookViewModel bookViewModel;
    private CollectionAdapter collectionAdapter;
    private List<Collection> publicCollections;

    public UserDetailsFragment() {}

    public static UserDetailsFragment newInstance() {
        return new UserDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserDetailsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMenu();
        initRepositories();
        initRecyclerView(view);
        fetchUserData();
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
                        .concat(requireContext().getString(R.string.user_details));
                toolbar.setTitle(title);
                menuInflater.inflate(R.menu.default_appbar_menu, menu);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        // Enable the back button
        Drawable coloredIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_24);
        int newColor = getResources().getColor(R.color.white, null);
        if (coloredIcon != null) {
            coloredIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);
        }
        toolbar.setNavigationIcon(coloredIcon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }

    private void initRepositories(){
        //initializing repository and viewModel Book
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
    }

    private void initRecyclerView(View view){
        publicCollections = new ArrayList<>();
        collectionAdapter = new CollectionAdapter(
                new CollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onCollectionItemClick(Collection collection) {
                        Navigation.findNavController(view).navigate(R.id.action_userDetailsFragment_to_collectionFragment);
                    }
                }, requireActivity().getApplication());

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recyclerviewUserCollections.setLayoutManager(layoutManager);
        binding.recyclerviewUserCollections.setAdapter(collectionAdapter);
        collectionAdapter.setCollectionsList(publicCollections);
    }
    private void fetchUserData(){
        Bundle arguments = getArguments();
        if(arguments != null){
            User receivedUser = arguments.getParcelable(BUNDLE_USER);
            if(receivedUser != null){
                showUserInfo(receivedUser);
            } else {
                // todo gestire errore user = null
            }
        } else {
            // todo errore arguments = null
        }
    }

    private void showUserInfo(User user){
        binding.textviewFollowerCounter.setText(String.valueOf(user.getFollowers().getCounter()));
        binding.textviewFollowingCounter.setText(String.valueOf(user.getFollowing().getCounter()));
        binding.textviewUserUsername.setText(user.getUsername());
        binding.textviewUserBiography.setText(user.getBiography());
        // todo devo ancora implementare
        loadUserCollections(user);

    }

    private void loadUserCollections(User user) {
        //get books from api
        int counter = 0;
        for (int i = 0; i < user.getCollections().size(); i++) {
            int finalCounter = counter;
            bookViewModel.fetchBooks(user.getCollections().get(i).getBooks(), COLLECTION)
                    .observe(getViewLifecycleOwner(), resultsList -> {
                        for (int j = 0; j < resultsList.size(); j++) {
                            if (resultsList.get(j).isSuccess()) {
                                OLWorkApiResponse book = ((Result.WorkSuccess) resultsList.get(j)).getData();
                                user.getCollections().get(finalCounter).getWorks().add(j, book);
                            }
                        }
                    });
            counter++;
        }

        publicCollections = user.getCollections();
        collectionAdapter.setCollectionsList(publicCollections);
        //todo
    }
}


















