package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.DESTINATION_FRAGMENT_FOLLOWER;
import static it.unimib.readify.util.Constants.DESTINATION_FRAGMENT_FOLLOWING;
import static it.unimib.readify.util.Constants.USER_VISIBILITY_PUBLIC;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.databinding.FragmentUserDetailsBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.FollowGroup;
import it.unimib.readify.model.FollowUser;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.CollectionViewModel;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;

public class UserDetailsFragment extends Fragment {

    private FragmentUserDetailsBinding binding;
    private UserViewModel userViewModel;
    private CollectionViewModel collectionViewModel;
    private CollectionAdapter collectionAdapter;
    private String userIdToken;
    private String loggedUserIdToken;

    public UserDetailsFragment() {}

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
        initViewModels();
        initObservers();
        initRecyclerView();
        fetchUserData();
    }

    private void initViewModels(){
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);

        collectionViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(CollectionViewModel.class);
    }

    private void initObservers() {
        final Observer<List<Result>> collectionsObserver = results -> {
            ConstraintLayout noCollectionsFoundLayout = binding.noCollectionsFoundLayout;
            if(results.isEmpty()){
                noCollectionsFoundLayout.setVisibility(View.VISIBLE);
            } else {
                noCollectionsFoundLayout.setVisibility(View.GONE);
            }
            List<Collection> collectionResultList = results.stream()
                    .filter(result -> result instanceof Result.CollectionSuccess)
                    .map(result -> ((Result.CollectionSuccess) result).getData())
                    .collect(Collectors.toList());
            collectionAdapter.submitList(collectionResultList);
            binding.collectionsProgressBar.setVisibility(View.GONE);
            binding.recyclerviewUserCollections.setVisibility(View.VISIBLE);
        };

        final Observer<Result> loggedUserObserver = result -> {
            if(result.isSuccess()){
                User loggedUser = ((Result.UserSuccess) result).getData();
                this.loggedUserIdToken = loggedUser.getIdToken();
                if(isFollowed(loggedUser)){
                    binding.followButton.setVisibility(View.GONE);
                    binding.unfollowButton.setVisibility(View.VISIBLE);
                } else {
                    binding.unfollowButton.setVisibility(View.GONE);
                    binding.followButton.setVisibility(View.VISIBLE);
                }
            } else {
                Log.e("UserDetailsFragment", "Logged user error");
            }
        };

        final Observer<Result> otherUserObserver = otherUserResult -> {
            if(otherUserResult.isSuccess()){
                User receivedUser = ((Result.UserSuccess) otherUserResult).getData();
                showPublicUserInfo(receivedUser);
                if(receivedUser.getVisibility().equalsIgnoreCase(USER_VISIBILITY_PUBLIC) || areUsersFriends(loggedUserIdToken, receivedUser)){
                    binding.privateProfileSection.setVisibility(View.GONE);
                    showPrivateUserInfo(receivedUser);
                } else {
                    binding.privateProfileSection.setVisibility(View.VISIBLE);
                    binding.noCollectionsFoundLayout.setVisibility(View.GONE);
                    hidePrivateUserInfo();
                }
            } else {
                Log.e("UserDetailsFragment", "Other user error");
            }
        };

        collectionViewModel.getOtherUserCollectionListLiveData().observe(getViewLifecycleOwner(), collectionsObserver);
        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
        userViewModel.getOtherUserLiveData().observe(getViewLifecycleOwner(),otherUserObserver);
    }

    private boolean areUsersFriends(String loggedUserIdToken, User otherUser) {
        if(loggedUserIdToken == null || otherUser == null) {
            Log.e("UserDetailsFragment", "One of the users is null");
            return false;
        }

        if(otherUser.getFollowing().getUsers() != null && otherUser.getFollowers().getUsers() != null){
            boolean otherUserFollowsLoggedUser = otherUser.getFollowing().getUsers().stream()
                    .anyMatch(user -> user.getIdToken().equals(loggedUserIdToken));

            boolean loggedUserFollowsOtherUser = otherUser.getFollowers().getUsers().stream()
                    .anyMatch(user -> user.getIdToken().equals(loggedUserIdToken));

            return otherUserFollowsLoggedUser && loggedUserFollowsOtherUser;
        }

        return false;
    }

    private void initRecyclerView(){
        collectionAdapter = new CollectionAdapter(collection -> {
            NavDirections action = UserDetailsFragmentDirections.actionUserDetailsFragmentToCollectionFragment(collection, collection.getName(), userIdToken);
            Navigation.findNavController(requireView()).navigate(action);
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recyclerviewUserCollections.setLayoutManager(layoutManager);
        binding.recyclerviewUserCollections.setAdapter(collectionAdapter);
    }
    private void fetchUserData(){
        this.userIdToken = UserDetailsFragmentArgs.fromBundle(getArguments()).getOtherUserIdToken();
        String username = UserDetailsFragmentArgs.fromBundle(getArguments()).getUsername();
        requireActivity().setTitle(username);
        userViewModel.fetchOtherUser(userIdToken);
    }

    private void showPublicUserInfo(User user){
        binding.followButton.setOnClickListener(v -> userViewModel.followUser(loggedUserIdToken, userIdToken));
        binding.unfollowButton.setOnClickListener(v -> userViewModel.unfollowUser(loggedUserIdToken, userIdToken));

        binding.textviewFollowerCounter.setText(String.valueOf(user.getFollowers().getCounter()));
        binding.textviewFollowingCounter.setText(String.valueOf(user.getFollowing().getCounter()));
        binding.textviewUserUsername.setText(user.getUsername());

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
    }

    private void showPrivateUserInfo(User user){
        binding.textviewUserBiography.setVisibility(View.VISIBLE);
        binding.textviewUserBiography.setText(user.getBiography());

        binding.collectionsProgressBar.setVisibility(View.VISIBLE);
        collectionViewModel.fetchOtherUserCollections(userIdToken);

    }

    private void hidePrivateUserInfo(){
        binding.textviewUserBiography.setVisibility(View.INVISIBLE);
        binding.collectionsProgressBar.setVisibility(View.GONE);
        binding.recyclerviewUserCollections.setVisibility(View.GONE);
    }

    private boolean isFollowed(User loggedUser){
        FollowGroup followingInstance = loggedUser.getFollowing();
        if(followingInstance != null){
            List<FollowUser> loggedUserFollowingList = followingInstance.getUsers();
            if(loggedUserFollowingList == null){
                loggedUserFollowingList = new ArrayList<>();
            }
            return loggedUserFollowingList.stream().anyMatch(following -> following.getIdToken().equals(userIdToken));
        }
        return false;
    }
}