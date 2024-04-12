package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.text.Editable;
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

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unimib.readify.R;
import it.unimib.readify.adapter.UserSearchResultAdapter;
import it.unimib.readify.databinding.FragmentTabSearchUsersBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class TabSearchUsersFragment extends Fragment {

    private FragmentTabSearchUsersBinding fragmentTabSearchUsersBinding;

    private UserSearchResultAdapter userSearchResultAdapter;
    private List<User> searchResultsList;
    private TestDatabaseViewModel testDatabaseViewModel;

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
        initObserver();

        RecyclerView recyclerView = fragmentTabSearchUsersBinding.recyclerviewSearchUsers;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        userSearchResultAdapter = new UserSearchResultAdapter(searchResultsList, requireActivity().getApplication(), new UserSearchResultAdapter.OnItemClickListener() {
            @Override
            public void onUserItemClick(User user) {
                NavDirections action = SearchFragmentDirections.actionSearchFragmentToUserDetailsFragment(user, user.getUsername());
                Navigation.findNavController(requireView()).navigate(action);
            }

            @Override
            public void onAddToCollectionButtonPressed(int position) {
                //todo implementa pulsante segui?
            }
        });
        recyclerView.setAdapter(userSearchResultAdapter);
        recyclerView.setLayoutManager(layoutManager);


        fragmentTabSearchUsersBinding.edittextSearchUsers.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (keyEvent != null && (actionId == KeyEvent.ACTION_DOWN || actionId == KeyEvent.KEYCODE_ENTER ||  actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                fragmentTabSearchUsersBinding.progressindicatorSearchUsers.setVisibility(View.VISIBLE);
                startSearch();
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
        Snackbar.make(requireView(), "Query: " + query, Snackbar.LENGTH_SHORT).show();
        if(query.isEmpty()){
            Snackbar.make(requireView(), getString(R.string.empty_search_snackbar), Snackbar.LENGTH_SHORT).show();
        } else {
            Log.d("UserSearchFragment", "Query: " + query);
            testDatabaseViewModel.searchUsers(query);
        }
    }

    private void initViewModels(){
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);
    }

    private void initObserver(){
        this.searchResultsList = new ArrayList<>();
        final Observer<List<Result>> searchResultsListObserver = results -> {
            List<User> searchResults = results.stream()
                    .filter(result -> result instanceof Result.UserSuccess)
                    .map(result -> ((Result.UserSuccess) result).getData())
                    .collect(Collectors.toList());
            searchResultsList = searchResults;
            userSearchResultAdapter.refreshList(searchResults);
            fragmentTabSearchUsersBinding.progressindicatorSearchUsers.setVisibility(View.GONE);
        };
        testDatabaseViewModel.getUserSearchResultsLiveData().observe(getViewLifecycleOwner(), searchResultsListObserver);
    }
}
