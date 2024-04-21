package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.DESTINATION_FRAGMENT_FOLLOWER;
import static it.unimib.readify.util.Constants.DESTINATION_FRAGMENT_FOLLOWING;

import android.content.res.Configuration;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.databinding.FragmentProfileBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class ProfileFragment extends Fragment{
    private FragmentProfileBinding fragmentProfileBinding;
    private TestDatabaseViewModel testDatabaseViewModel;
    private BookViewModel bookViewModel;
    private CollectionAdapter collectionAdapter;
    private User user;
    public ProfileFragment() {}

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater,container,false);
        return fragmentProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d("profile lifecycle", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        initObservers();
        loadMenu();
        initRecyclerView();
        initCreateCollectionSection();
        initFollowersSection();
    }

    private void initViewModels() {
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
            fragmentProfileBinding.progressBarProfile.setVisibility(View.GONE);
        };

        final Observer<Result> loggedUserObserver = result -> {
            if(result.isSuccess()) {
                this.user = ((Result.UserSuccess) result).getData();
                Log.e("USER OBSERVER","TRIGGERED");
                testDatabaseViewModel.fetchCollections(user.getIdToken());
                updateUI();
            }
        };

        final Observer<List<Result>> emptyCollectionsObserver = results -> {
            fragmentProfileBinding.progressBarProfile.setVisibility(View.VISIBLE);
            Log.e("EMPTY COLLECTION OBSERVER","TRIGGERED");
            List<Collection> collectionsResultList = results.stream()
                    .filter(result -> result instanceof Result.CollectionSuccess)
                    .map(result -> ((Result.CollectionSuccess) result).getData())
                    .collect(Collectors.toList());
            bookViewModel.fetchWorksForCollections(collectionsResultList);
        };

        testDatabaseViewModel.getCollectionListLiveData().observe(getViewLifecycleOwner(),emptyCollectionsObserver);
        testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
        bookViewModel.getCompleteCollectionListLiveData().observe(getViewLifecycleOwner(), fetchedCollectionsObserver);
    }

   private void initRecyclerView(){
       collectionAdapter = new CollectionAdapter(collection -> {
                   NavDirections action = ProfileFragmentDirections.actionProfileFragmentToCollectionFragment(collection, collection.getName());
                   Navigation.findNavController(requireView()).navigate(action);
               });
       RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
       fragmentProfileBinding.recyclerviewCollections.setLayoutManager(layoutManager);
       fragmentProfileBinding.recyclerviewCollections.setAdapter(collectionAdapter);
   }

    public void loadMenu(){
        SwitchCompat switchButton = Objects.requireNonNull(fragmentProfileBinding.navView.getMenu().findItem(R.id.nav_switch).getActionView()).findViewById(R.id.switch_compat);
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

// Imposta lo stato dello SwitchCompat in base al tema corrente
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                switchButton.setChecked(true);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                switchButton.setChecked(false);
                break;
        }
        switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Set up the toolbar and remove all icons
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.profile_appbar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_notifications) {
                    // TODO: Show notifications menu
                } else if (menuItem.getItemId() == R.id.action_settings) {
                    DrawerLayout drawerLayout = fragmentProfileBinding.drawLayout;
                    NavigationView navigationView = fragmentProfileBinding.navView;
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);
                    } else {
                        drawerLayout.openDrawer(GravityCompat.END);
                    }

                    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                            int itemId = menuItem.getItemId();

                            if (itemId == R.id.nav_settings) {
                                Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_settingsFragment);
                            }
                            if(itemId == R.id.nav_logout){
                                /*
                                testDatabaseViewModel.logout();
                                //FirebaseAuth.getInstance().signOut();
                                Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_loginFragment);
                                 */
                             }

                            if (itemId == R.id.nav_visibility) {
                                // Esegui le azioni per la voce del menu 'Visibility'
                                // ...
                            }

                            if (itemId == R.id.nav_switch) {

                            }


                            drawerLayout.closeDrawer(GravityCompat.END);
                            return true;
                        }
                    });
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void initCreateCollectionSection() {
        fragmentProfileBinding.createCollection.setOnClickListener(v -> {
            NavDirections action = ProfileFragmentDirections.actionProfileFragmentToCreateCollectionDialog(user.getIdToken());
            Navigation.findNavController(requireView()).navigate(action);
        });
    }
    private void initFollowersSection() {
        View.OnClickListener followClickListener = v -> {
            NavDirections action = null;
            if(v.getId() == fragmentProfileBinding.textviewFollowerCounter.getId() || v.getId() == fragmentProfileBinding.textviewFollowerLabel.getId() ){
                action = ProfileFragmentDirections.actionProfileFragmentToFollowListFragment(user.getIdToken(),user.getUsername(),DESTINATION_FRAGMENT_FOLLOWER);
            } else if(v.getId() == fragmentProfileBinding.textviewFollowingCounter.getId() || v.getId() == fragmentProfileBinding.textviewFollowingLabel.getId()) {
                action = ProfileFragmentDirections.actionProfileFragmentToFollowListFragment(user.getIdToken(),user.getUsername(),DESTINATION_FRAGMENT_FOLLOWING);
            }
            if(action != null){
                Navigation.findNavController(requireView()).navigate(action);
            }
        };
        fragmentProfileBinding.textviewFollowerCounter.setOnClickListener(followClickListener);
        fragmentProfileBinding.textviewFollowerLabel.setOnClickListener(followClickListener);
        fragmentProfileBinding.textviewFollowingCounter.setOnClickListener(followClickListener);
        fragmentProfileBinding.textviewFollowingLabel.setOnClickListener(followClickListener);
    }


    public void updateUI() {
        int avatarId;
        try {
            avatarId = R.drawable.class.getDeclaredField(user.getAvatar().toLowerCase()).getInt(null);
        } catch (Exception e) {
            avatarId = R.drawable.ic_baseline_profile_24;
        }
        Glide.with(requireActivity().getApplicationContext())
                .load(avatarId)
                .dontAnimate()
                .into(fragmentProfileBinding.avatarImageView);

        fragmentProfileBinding.textviewUsername.setText(user.getUsername());
        fragmentProfileBinding.textviewFollowerCounter.setText(String.valueOf(user.getFollowers().getCounter()));
        fragmentProfileBinding.textviewFollowingCounter.setText(String.valueOf(user.getFollowing().getCounter()));

        fragmentProfileBinding.textviewUserBiography.setText(user.getBiography());

        fragmentProfileBinding.textviewFacebook.setVisibility(View.GONE);
        fragmentProfileBinding.textviewTwitter.setVisibility(View.GONE);
        fragmentProfileBinding.textviewInstagram.setVisibility(View.GONE);
        for (int i = 0; i < user.getSocialLinks().size(); i++) {
            switch (user.getSocialLinks().get(i).getSocialPlatform()) {
                case "facebook":
                    fragmentProfileBinding.textviewFacebook.setText(user.getSocialLinks().get(i).getLink());
                    fragmentProfileBinding.textviewFacebook.setVisibility(View.VISIBLE);
                    break;
                case "twitter":
                    fragmentProfileBinding.textviewTwitter.setText(user.getSocialLinks().get(i).getLink());
                    fragmentProfileBinding.textviewTwitter.setVisibility(View.VISIBLE);
                    break;
                case "instagram":
                    fragmentProfileBinding.textviewInstagram.setText(user.getSocialLinks().get(i).getLink());
                    fragmentProfileBinding.textviewInstagram.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
