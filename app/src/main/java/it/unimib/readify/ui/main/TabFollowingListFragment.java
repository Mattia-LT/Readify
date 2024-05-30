package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.BUNDLE_ID_TOKEN;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import it.unimib.readify.databinding.FragmentTabFollowingListBinding;
import it.unimib.readify.model.FollowUser;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.CustomViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;

public class TabFollowingListFragment extends Fragment {

    private FragmentTabFollowingListBinding fragmentTabFollowingListBinding;
    private UserViewModel userViewModel;
    private FollowListAdapter followListAdapter;
    private List<FollowUser> followingList;
    private String idToken;
    private String loggedUserIdToken;
    public TabFollowingListFragment(){}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        fragmentTabFollowingListBinding = FragmentTabFollowingListBinding.inflate(inflater, container, false);
        return fragmentTabFollowingListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViewModels();
        if(getArguments() != null){
            this.idToken = getArguments().getString(BUNDLE_ID_TOKEN);
        }
        initObserver();

        RecyclerView recyclerView = fragmentTabFollowingListBinding.recyclerviewFollowingUsers;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        followListAdapter = new FollowListAdapter(new FollowListAdapter.OnItemClickListener() {
            @Override
            public void onProfileClick(FollowUser followUser) {
                User selectedUser = followUser.getUser();
                if(!selectedUser.getIdToken().equals(loggedUserIdToken)){
                    NavDirections action = FollowListFragmentDirections.actionFollowListFragmentToUserDetailsFragment(selectedUser.getIdToken(),selectedUser.getUsername());
                    Navigation.findNavController(requireView()).navigate(action);
                }

            }

            @Override
            public void onFollowButtonClick(FollowUser user) {
                //TODO testare bene per trovare eventuali errori
                userViewModel.followUser(idToken, user.getIdToken());
                //userViewModel.fetchFollowing(idToken);
                Log.d("TabFollowerListFragment", "followButtonClick premuto con idtoken: " + idToken);
            }

            @Override
            public void onUnfollowButtonClick(FollowUser user) {
                //TODO testare bene per trovare eventuali errori
                userViewModel.unfollowUser(idToken, user.getIdToken());
                //userViewModel.fetchFollowing(idToken);
                Log.d("TabFollowerListFragment", "UnfollowButtonClick premuto con idtoken: " + idToken);
            }
        });
        recyclerView.setAdapter(followListAdapter);
        recyclerView.setLayoutManager(layoutManager);

        fragmentTabFollowingListBinding.edittextFollowingUsers.addTextChangedListener(new TextWatcher() {
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


    private void initViewModels(){
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }

    private void initObserver(){
        this.followingList = new ArrayList<>();
        final Observer<List<Result>> followingObserver = results -> {
            List<FollowUser> followingList = results.stream()
                    .filter(result -> result instanceof Result.FollowUserSuccess)
                    .map(result -> ((Result.FollowUserSuccess) result).getData())
                    .collect(Collectors.toList());
            followListAdapter.submitList(followingList);
            this.followingList = followingList;
        };

        final Observer<Result> loggedUserObserver = result -> {
            if(result.isSuccess()) {
                User user = ((Result.UserSuccess) result).getData();
                loggedUserIdToken = user.getIdToken();
                Log.e("USER OBSERVER","TRIGGERED");
                followListAdapter.submitFollowings(user.getFollowing().getUsers(), user.getIdToken());
                userViewModel.fetchFollowing(idToken);
            }
        };

        userViewModel.getFollowingListLiveData().observe(getViewLifecycleOwner(), followingObserver);
        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
    }

    private void search(String text) {
        ArrayList<FollowUser> filteredList = new ArrayList<>();
        text = text.trim();
        for (FollowUser user : followingList) {
            if (user.getUser().getUsername().toLowerCase().contains((text.toLowerCase()))) {
                filteredList.add(user);
            }
        }
        if (filteredList.isEmpty()) {
            followListAdapter.submitList(filteredList);
            //Todo cambiare con una texview dietro alla recycler view
            Toast.makeText(requireActivity(), "No Data Found..", Toast.LENGTH_SHORT).show();
        } else {
            followListAdapter.submitList(filteredList);
        }
    }
}