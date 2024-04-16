package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private List<ExternalUser> searchResultsList;
    private TestDatabaseViewModel testDatabaseViewModel;
    private FollowListAdapter followListAdapter;
    private String idToken;

    public TabFollowerListFragment(){}

    public TabFollowerListFragment(String idToken){
        this.idToken = idToken;
    }

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
        initObserver();

        RecyclerView recyclerView = fragmentTabFollowerListBinding.recyclerviewFollowerUsers;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        followListAdapter = new FollowListAdapter(new FollowListAdapter.OnItemClickListener() {
            @Override
            public void onProfileClick(ExternalUser externalUser) {
                User selectedUser = externalUser.getUser();
                NavDirections action = FollowListFragmentDirections.actionFollowListFragmentToUserDetailsFragment(selectedUser,selectedUser.getUsername());
                Navigation.findNavController(requireView()).navigate(action);
            }

            @Override
            public void onFollowButtonClick(ExternalUser user) {
                //todo
            }

            @Override
            public void onUnfollowButtonClick(ExternalUser user) {
                //todo
            }
        });
        recyclerView.setAdapter(followListAdapter);
        recyclerView.setLayoutManager(layoutManager);

        fragmentTabFollowerListBinding.edittextFollowerUsers.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (keyEvent != null && (actionId == KeyEvent.ACTION_DOWN || actionId == KeyEvent.KEYCODE_ENTER ||  actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                fragmentTabFollowerListBinding.progressindicatorFollowerUsers.setVisibility(View.VISIBLE);
                startSearch();
                return true;
            }
            return false;
        });
    }


    private void startSearch(){
        //todo
    }


    private void initViewModels(){
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);
    }

    private void initObserver(){
        final Observer<List<Result>> followersObserver = results -> {
            List<ExternalUser> followersList = results.stream()
                    .filter(result -> result instanceof Result.ExternalUserSuccess)
                    .map(result -> ((Result.ExternalUserSuccess) result).getData())
                    .collect(Collectors.toList());
            followListAdapter.submitList(followersList);
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


}