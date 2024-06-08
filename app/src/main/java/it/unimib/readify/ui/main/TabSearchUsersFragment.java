package it.unimib.readify.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.stream.Collectors;

import it.unimib.readify.R;
import it.unimib.readify.adapter.UserSearchResultAdapter;
import it.unimib.readify.databinding.FragmentTabSearchUsersBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.CustomViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;

public class TabSearchUsersFragment extends Fragment {

    private FragmentTabSearchUsersBinding fragmentTabSearchUsersBinding;
    private UserSearchResultAdapter userSearchResultAdapter;
    private String loggedUserIdToken;
    private UserViewModel userViewModel;
    private boolean isSearchRunning;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentTabSearchUsersBinding = FragmentTabSearchUsersBinding.inflate(inflater, container, false);
        return fragmentTabSearchUsersBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        initObservers();

        RecyclerView recyclerView = fragmentTabSearchUsersBinding.recyclerviewSearchUsers;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        userSearchResultAdapter = new UserSearchResultAdapter(user -> {
            NavDirections action = SearchFragmentDirections.actionSearchFragmentToUserDetailsFragment(user.getIdToken(), user.getUsername());
            Navigation.findNavController(requireView()).navigate(action);
        });
        recyclerView.setAdapter(userSearchResultAdapter);
        recyclerView.setLayoutManager(layoutManager);

        isSearchRunning = false;
        fragmentTabSearchUsersBinding.edittextSearchUsers.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            boolean isEnterKeyPressed = (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN);
            boolean isSearchActionPressed = (actionId == EditorInfo.IME_ACTION_SEARCH);

            if ( (isEnterKeyPressed || isSearchActionPressed) && !isSearchRunning) {
                isSearchRunning = true;
                fragmentTabSearchUsersBinding.noUsersFoundLayout.setVisibility(View.GONE);
                startSearch();
                InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
                return true;
            }
            return false;
        });
    }

    public void startSearch(){
        // Perform the search when the "Enter" key is pressed
        Editable text = fragmentTabSearchUsersBinding.edittextSearchUsers.getText();
        String query = (text != null) ? text.toString() : "";
        query = query.trim();
        if(query.isEmpty()){
            Toast.makeText(requireContext(), getString(R.string.empty_search_snackbar), Toast.LENGTH_SHORT).show();
            isSearchRunning = false;
        } else {
            fragmentTabSearchUsersBinding.progressindicatorSearchUsers.setVisibility(View.VISIBLE);
            userViewModel.searchUsers(query);
        }
    }

    private void initViewModels(){
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }

    private void initObservers(){

        final Observer<Result> loggedUserObserver = result -> {
            if(result.isSuccess()) {
                User loggedUser = ((Result.UserSuccess) result).getData();
                this.loggedUserIdToken = loggedUser.getIdToken();
            }
        };

        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);

        final Observer<List<Result>> searchResultsListObserver = results -> {
            ConstraintLayout noUsersFoundLayout = fragmentTabSearchUsersBinding.noUsersFoundLayout;
            if(results.isEmpty()){
                noUsersFoundLayout.setVisibility(View.VISIBLE);
            } else {
                noUsersFoundLayout.setVisibility(View.GONE);
            }

            List<User> searchResults = results.stream()
                    .filter(result -> result instanceof Result.UserSuccess)
                    .map(result -> ((Result.UserSuccess) result).getData())
                    .filter(user -> !user.getIdToken().equals(loggedUserIdToken))
                    .collect(Collectors.toList());

            userSearchResultAdapter.submitList(searchResults);
            fragmentTabSearchUsersBinding.progressindicatorSearchUsers.setVisibility(View.GONE);
            isSearchRunning = false;
        };

        userViewModel.getUserSearchResultsLiveData().observe(getViewLifecycleOwner(), searchResultsListObserver);
    }
}
