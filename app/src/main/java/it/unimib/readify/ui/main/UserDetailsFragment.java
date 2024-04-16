package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.DESTINATION_FRAGMENT_FOLLOWER;
import static it.unimib.readify.util.Constants.DESTINATION_FRAGMENT_FOLLOWING;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;
import java.util.stream.Collectors;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.databinding.FragmentUserDetailsBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class UserDetailsFragment extends Fragment {

    private FragmentUserDetailsBinding binding;
    private TestDatabaseViewModel testDatabaseViewModel;
    private BookViewModel bookViewModel;
    private CollectionAdapter collectionAdapter;

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
        initViewModels();
        initObservers();
        initRecyclerView();
        fetchUserData();
    }


    private void loadMenu(){
        // Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);
        // Enable the back button
        Drawable coloredIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_24);
        int newColor = getResources().getColor(R.color.white, null);
        if (coloredIcon != null) {
            coloredIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);
        }
        toolbar.setNavigationIcon(coloredIcon);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

    }

    private void initViewModels(){
        //initializing viewModels
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);

        bookViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(BookViewModel.class);
    }
    private void initObservers() {
        final Observer<List<Result>> fetchedCollectionsObserver = results -> {
            List<Collection> collectionResultList = results.stream()
                    .filter(result -> result instanceof Result.CollectionSuccess)
                    .map(result -> ((Result.CollectionSuccess) result).getData())
                    .collect(Collectors.toList());
            Log.e("COLLECTIONS OPENLIBRARY","TRIGGERED");
            collectionAdapter.submitList(collectionResultList);
            binding.collectionsProgressBar.setVisibility(View.GONE);
            binding.recyclerviewUserCollections.setVisibility(View.VISIBLE);
        };

        final Observer<List<Result>> emptyCollectionsObserver = results -> {
            binding.collectionsProgressBar.setVisibility(View.VISIBLE);
            binding.recyclerviewUserCollections.setVisibility(View.GONE);
            Log.e("EMPTY COLLECTION OBSERVER","TRIGGERED");
            List<Collection> collectionsResultList = results.stream()
                    .filter(result -> result instanceof Result.CollectionSuccess)
                    .map(result -> ((Result.CollectionSuccess) result).getData())
                    .filter(Collection::isVisible)
                    .collect(Collectors.toList());
            bookViewModel.fetchWorksForCollections(collectionsResultList);
        };

        testDatabaseViewModel.getCollectionListLiveData().observe(getViewLifecycleOwner(),emptyCollectionsObserver);
        bookViewModel.getCompleteCollectionListLiveData().observe(getViewLifecycleOwner(), fetchedCollectionsObserver);
    }
    private void initRecyclerView(){
        collectionAdapter = new CollectionAdapter(collection -> {
            NavDirections action = UserDetailsFragmentDirections.actionUserDetailsFragmentToCollectionFragment(collection, collection.getName());
            Navigation.findNavController(requireView()).navigate(action);
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recyclerviewUserCollections.setLayoutManager(layoutManager);
        binding.recyclerviewUserCollections.setAdapter(collectionAdapter);
    }
    private void fetchUserData(){
        User receivedUser = UserDetailsFragmentArgs.fromBundle(getArguments()).getUser();
        String username = UserDetailsFragmentArgs.fromBundle(getArguments()).getUsername();
        requireActivity().setTitle(username);
        showUserInfo(receivedUser);
    }

    private void showUserInfo(User user){
        binding.recyclerviewUserCollections.setVisibility(View.GONE);
        testDatabaseViewModel.fetchCollections(user.getIdToken());

        binding.textviewFollowerCounter.setText(String.valueOf(user.getFollowers().getCounter()));
        binding.textviewFollowingCounter.setText(String.valueOf(user.getFollowing().getCounter()));
        binding.textviewUserUsername.setText(user.getUsername());
        binding.textviewUserBiography.setText(user.getBiography());
        int avatarId;
        try {
            avatarId = R.drawable.class.getDeclaredField(user.getAvatar().toLowerCase()).getInt(null);
        } catch (Exception e) {
            avatarId = R.drawable.ic_baseline_profile_24;
        }
        Glide.with(requireActivity().getApplicationContext())
                .load(avatarId)
                .dontAnimate()
                .into(binding.avatarImageView);

        View.OnClickListener followClickListener = v -> {
            NavDirections action = null;
            if(v.getId() == binding.textviewFollowerCounter.getId() || v.getId() == binding.textviewFollowerLabel.getId() ){
                action = UserDetailsFragmentDirections.actionUserDetailsFragmentToFollowListFragment(user.getIdToken(),user.getUsername(), DESTINATION_FRAGMENT_FOLLOWER);
            } else if(v.getId() == binding.textviewFollowingCounter.getId() || v.getId() == binding.textviewFollowingLabel.getId()) {
                action = UserDetailsFragmentDirections.actionUserDetailsFragmentToFollowListFragment(user.getIdToken(),user.getUsername(), DESTINATION_FRAGMENT_FOLLOWING);
            }
            if(action != null){
                Navigation.findNavController(requireView()).navigate(action);
            }
        };
        binding.textviewFollowerCounter.setOnClickListener(followClickListener);
        binding.textviewFollowerLabel.setOnClickListener(followClickListener);
        binding.textviewFollowingCounter.setOnClickListener(followClickListener);
        binding.textviewFollowingLabel.setOnClickListener(followClickListener);
    }
}