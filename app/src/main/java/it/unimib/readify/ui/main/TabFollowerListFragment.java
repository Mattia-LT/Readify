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
import it.unimib.readify.databinding.FragmentTabFollowerListBinding;
import it.unimib.readify.model.ExternalUser;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class TabFollowerListFragment extends Fragment{

    private FragmentTabFollowerListBinding fragmentTabFollowerListBinding;
    private TestDatabaseViewModel testDatabaseViewModel;
    private FollowListAdapter followListAdapter;
    private List<ExternalUser> followerList;
    private String idToken;

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
        initViewModels();
        if(getArguments()!= null){
            this.idToken = getArguments().getString(BUNDLE_ID_TOKEN);
        }
        initObserver();
        RecyclerView recyclerView = fragmentTabFollowerListBinding.recyclerviewFollowerUsers;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        followListAdapter = new FollowListAdapter(new FollowListAdapter.OnItemClickListener() {
            @Override
            public void onProfileClick(ExternalUser externalUser) {
                User selectedUser = externalUser.getUser();
                NavDirections action = FollowListFragmentDirections.actionFollowListFragmentToUserDetailsFragment(selectedUser.getIdToken(),selectedUser.getUsername());
                Navigation.findNavController(requireView()).navigate(action);
            }

            @Override
            public void onFollowButtonClick(ExternalUser user) {
                testDatabaseViewModel.followUser(idToken, user.getIdToken());
                //testDatabaseViewModel.fetchFollowing(idToken);
                Log.d("TabFollowerListFragment", "followButtonClick premuto con idtoken: " + idToken);
            }

            @Override
            public void onUnfollowButtonClick(ExternalUser user) {
                //TODO testare bene per trovare eventuali errori
                testDatabaseViewModel.unfollowUser(idToken, user.getIdToken());
                //testDatabaseViewModel.fetchFollowing(idToken);
                Log.d("TabFollowerListFragment", "UnfollowButtonClick premuto con idtoken: " + idToken);
            }
        });
        recyclerView.setAdapter(followListAdapter);
        recyclerView.setLayoutManager(layoutManager);

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

    private void initViewModels(){
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);
    }

    private void initObserver(){
        this.followerList = new ArrayList<>();
        final Observer<List<Result>> followersObserver = results -> {
            List<ExternalUser> followersList = results.stream()
                    .filter(result -> result instanceof Result.ExternalUserSuccess)
                    .map(result -> ((Result.ExternalUserSuccess) result).getData())
                    .collect(Collectors.toList());
            followListAdapter.submitList(followersList);
            this.followerList = followersList;
        };

        final Observer<Result> loggedUserObserver = result -> {
            if(result.isSuccess()) {
                User user = ((Result.UserSuccess) result).getData();
                Log.e("USER OBSERVER","TRIGGERED");
                followListAdapter.submitFollowings(user.getFollowing().getUsers());
                testDatabaseViewModel.fetchFollowers(idToken);
            }
        };


        testDatabaseViewModel.getFollowersListLiveData().observe(getViewLifecycleOwner(), followersObserver);
        testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
    }

    private void search(String text) {
        ArrayList<ExternalUser> filteredList = new ArrayList<>();
        text = text.trim();
        for (ExternalUser user : followerList) {
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