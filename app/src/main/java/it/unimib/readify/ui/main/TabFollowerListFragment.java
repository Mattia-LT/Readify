package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.BUNDLE_ID_TOKEN;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unimib.readify.adapter.FollowListAdapter;
import it.unimib.readify.databinding.FragmentTabFollowerListBinding;
import it.unimib.readify.model.FollowUser;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;

public class TabFollowerListFragment extends Fragment{

    private final String TAG = TabFollowerListFragment.class.getSimpleName();
    private FragmentTabFollowerListBinding fragmentTabFollowerListBinding;
    private UserViewModel userViewModel;
    private FollowListAdapter followListAdapter;
    private List<FollowUser> followerList;
    private String idToken;
    private String loggedUserIdToken;
    private Observer<List<Result>> followersObserver;
    private Observer<Result> loggedUserObserver;

    public TabFollowerListFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentTabFollowerListBinding = FragmentTabFollowerListBinding.inflate(inflater, container, false);
        return fragmentTabFollowerListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if(getArguments()!= null){
            this.idToken = getArguments().getString(BUNDLE_ID_TOKEN);
        }
        initViewModels();
        initObservers();
        initRecyclerView();
        setUpSearchSection();
    }

    private void initViewModels(){
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }

    private void initObservers(){
        this.followerList = new ArrayList<>();

        followersObserver = results -> {
            ConstraintLayout noUsersFoundLayout = fragmentTabFollowerListBinding.noFollowersFoundLayout;
            noUsersFoundLayout.setVisibility(View.GONE);
            List<FollowUser> followersList = results.stream()
                    .filter(result -> result instanceof Result.FollowUserSuccess)
                    .map(result -> ((Result.FollowUserSuccess) result).getData())
                    .collect(Collectors.toList());
            followListAdapter.submitList(followersList);
            this.followerList = followersList;
        };

        loggedUserObserver = loggedUserResult -> {
            if(loggedUserResult.isSuccess()) {
                User loggedUser = ((Result.UserSuccess) loggedUserResult).getData();
                loggedUserIdToken = loggedUser.getIdToken();
                followListAdapter.submitFollowings(loggedUser.getFollowing().getUsers(), loggedUser.getIdToken());
                userViewModel.fetchFollowers(idToken);
            } else {
                String errorMessage = ((Result.Error) loggedUserResult).getMessage();
                Log.e(TAG, "Error: Logged user fetch wasn't successful -> " + errorMessage);
            }
        };

        userViewModel.getFollowersListLiveData().observe(getViewLifecycleOwner(), followersObserver);
        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = fragmentTabFollowerListBinding.recyclerviewFollowerUsers;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        followListAdapter = new FollowListAdapter(new FollowListAdapter.OnItemClickListener() {
            @Override
            public void onProfileClick(FollowUser followUser) {
                User selectedUser = followUser.getUser();
                if(!selectedUser.getIdToken().equals(loggedUserIdToken)){
                    NavDirections action = FollowListFragmentDirections.actionFollowListFragmentToUserDetailsFragment(selectedUser.getIdToken(), selectedUser.getUsername());
                    Navigation.findNavController(requireView()).navigate(action);
                }
            }

            @Override
            public void onFollowButtonClick(FollowUser user) {
                userViewModel.followUser(loggedUserIdToken, user.getIdToken());
            }

            @Override
            public void onUnfollowButtonClick(FollowUser user) {
                userViewModel.unfollowUser(loggedUserIdToken, user.getIdToken());
            }
        });

        recyclerView.setAdapter(followListAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setUpSearchSection() {
        fragmentTabFollowerListBinding.edittextFollowerUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                search(text);
            }
        });
    }

    private void search(String text) {
        ConstraintLayout noUsersFoundLayout = fragmentTabFollowerListBinding.noFollowersFoundLayout;
        ArrayList<FollowUser> filteredList = new ArrayList<>();
        text = text.trim();
        for (FollowUser user : followerList) {
            if (user.getUser().getUsername().toLowerCase().contains((text.toLowerCase()))) {
                filteredList.add(user);
            }
        }
        followListAdapter.submitList(filteredList);
        if (filteredList.isEmpty()) {
            noUsersFoundLayout.setVisibility(View.VISIBLE);
        } else {
            noUsersFoundLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userViewModel.getFollowersListLiveData().removeObserver(followersObserver);
        userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
    }
}