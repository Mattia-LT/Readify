package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.BUNDLE_USER;

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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.unimib.readify.R;
import it.unimib.readify.adapter.UserSearchResultAdapter;
import it.unimib.readify.data.repository.user.TestIDatabaseRepository;
import it.unimib.readify.databinding.FragmentTabSearchUsersBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.TestServiceLocator;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class TabSearchUsersFragment extends Fragment {

    private FragmentTabSearchUsersBinding fragmentTabSearchUsersBinding;

    private UserSearchResultAdapter userSearchResultAdapter;
    private List<User> searchResultList;
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
        initRepositories();

        searchResultList = new ArrayList<>();
        RecyclerView recyclerView = fragmentTabSearchUsersBinding.recyclerviewSearchUsers;
        userSearchResultAdapter = new UserSearchResultAdapter(searchResultList, requireActivity().getApplication(), new UserSearchResultAdapter.OnItemClickListener() {
            @Override
            public void onUserItemClick(User user) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(BUNDLE_USER, user);
                Navigation.findNavController(requireView()).navigate(R.id.action_searchFragment_to_userDetailsFragment, bundle);
            }

            @Override
            public void onAddToCollectionButtonPressed(int position) {
                //todo implementa pulsante segui?b
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setAdapter(userSearchResultAdapter);
        recyclerView.setLayoutManager(layoutManager);

        fragmentTabSearchUsersBinding.progressindicatorSearchUsers.setVisibility(View.GONE);
        // Add an OnEditorActionListener to listen for the "Enter" key press
        fragmentTabSearchUsersBinding.edittextSearchUsers.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (keyEvent != null && (actionId == KeyEvent.ACTION_DOWN || actionId == KeyEvent.KEYCODE_ENTER ||  actionId == EditorInfo.IME_ACTION_SEARCH || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                fragmentTabSearchUsersBinding.progressindicatorSearchUsers.setVisibility(View.VISIBLE);
                startSearch(view);
                return true;
            }
            return false;
        });
    }

    public void startSearch(View view){
        // Perform the search when the "Enter" key is pressed
        String query = Objects.requireNonNull(fragmentTabSearchUsersBinding.edittextSearchUsers.getText()).toString();
        Snackbar.make(view, "Query: " + query, Snackbar.LENGTH_SHORT).show();
        if(query.trim().isEmpty()){
            Snackbar.make(view, getString(R.string.empty_search_snackbar), Snackbar.LENGTH_SHORT).show();
        } else {
            //todo implementa qui ricerca utenti
            Log.d("UserSearchFragment", "Query: " + query);
            fragmentTabSearchUsersBinding.progressindicatorSearchUsers.setVisibility(View.VISIBLE);
            testDatabaseViewModel.searchUsers(query).observe(getViewLifecycleOwner(), resultList -> {
                fragmentTabSearchUsersBinding.progressindicatorSearchUsers.setVisibility(View.VISIBLE);
                searchResultList.clear();
                userSearchResultAdapter.notifyItemRangeChanged(0,0);
                for(Result result : resultList){
                    if(result.isSuccess()) {
                        User user = ((Result.UserSuccess) result).getData();
                        Log.d("UserSearchFragment", "user: " + user);
                        searchResultList.add(user);
                    } else {
                        Log.e("Result.isSuccess() = false in SearchUsers fragment", result.toString());
                        Snackbar.make(view, "ERRORE", Snackbar.LENGTH_SHORT).show();
                    }
                }
                userSearchResultAdapter.notifyItemRangeChanged(0, searchResultList.size());
                fragmentTabSearchUsersBinding.progressindicatorSearchUsers.setVisibility(View.GONE);
            });
        }
    }

    private void resumeSearch(){
        String query = Objects.requireNonNull(fragmentTabSearchUsersBinding.edittextSearchUsers.getText()).toString();
        if(!query.trim().isEmpty()){
            testDatabaseViewModel.searchUsers(query).observe(getViewLifecycleOwner(), resultList -> {
                this.searchResultList.clear();
                for(Result result : resultList){
                    if(result.isSuccess()) {
                        User user = ((Result.UserSuccess) result).getData();
                        //Log.d("user", user.toString());
                        this.searchResultList.add(user);
                    } else {
                        Log.e("Result.isSuccess() = false in SearchUsers fragment", result.toString());
                    }
                }
                userSearchResultAdapter.notifyItemRangeChanged(0,searchResultList.size());
                fragmentTabSearchUsersBinding.progressindicatorSearchUsers.setVisibility(View.GONE);
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeSearch();
        //todo potrebbero esserci soluzioni migliori
    }

    private void initRepositories(){
        TestIDatabaseRepository testIDatabaseRepository = TestServiceLocator
                .getInstance(requireActivity().getApplication())
                .getRepository(TestIDatabaseRepository.class);
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(testIDatabaseRepository)
                .create(TestDatabaseViewModel.class);
    }


}
