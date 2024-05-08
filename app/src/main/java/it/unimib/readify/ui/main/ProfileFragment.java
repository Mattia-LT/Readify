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
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.databinding.FragmentProfileBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.Notification;
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
    private User loggedUser;
    private List<Collection> collectionsList;

    private HashMap<String, ArrayList<Notification>> notifications = new HashMap<>();
    private Observer<HashMap<String, ArrayList<Notification>>> fetchedNotificationsObserver;
    private Observer<Result> loggedUserObserver;
    private Observer<List<Result>> fetchedCollectionsObserver;
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
        initRecyclerView();
        initCreateCollectionSection();
        initFollowersSection();
        loadMenu();
    }

    private void initViewModels() {
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);

        bookViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(BookViewModel.class);
    }

    private void initObservers() {
        //todo all observer (except loggedUser) triggers twice instead of 1 when page is reloaded
        this.collectionsList = new ArrayList<>();

        fetchedCollectionsObserver = results -> {
            this.collectionsList = results.stream()
                    .filter(result -> result instanceof Result.CollectionSuccess)
                    .map(result -> ((Result.CollectionSuccess) result).getData())
                    .collect(Collectors.toList());
            Log.e("COLLECTIONS OPENLIBRARY","TRIGGERED");
            collectionAdapter.submitList(collectionsList);
            fragmentProfileBinding.progressBarProfile.setVisibility(View.GONE);
            fragmentProfileBinding.recyclerviewCollections.setVisibility(View.VISIBLE);
        };

        loggedUserObserver = result -> {
            if(result.isSuccess()) {
                this.loggedUser = ((Result.UserSuccess) result).getData();
                Log.e("USER OBSERVER","TRIGGERED");
                updateUI();
                fragmentProfileBinding.recyclerviewCollections.setVisibility(View.GONE);
                fragmentProfileBinding.progressBarProfile.setVisibility(View.VISIBLE);
                testDatabaseViewModel.fetchLoggedUserCollections(loggedUser.getIdToken());
                testDatabaseViewModel.fetchNotifications(loggedUser.getIdToken());
            }
        };

        final Observer<List<Result>> emptyCollectionsObserver = results -> {
            fragmentProfileBinding.progressBarProfile.setVisibility(View.VISIBLE);
            Log.e("EMPTY COLLECTION OBSERVER","TRIGGERED");
            List<Collection> collectionsResultList = results.stream()
                    .filter(result -> result instanceof Result.CollectionSuccess)
                    .map(result -> ((Result.CollectionSuccess) result).getData())
                    .collect(Collectors.toList());
            this.collectionsList = collectionsResultList;
            bookViewModel.fetchWorksForCollections(collectionsResultList);
        };

        fetchedNotificationsObserver = result -> {
            Log.e("NOTIFICATIONS OBSERVER","TRIGGERED");
            notifications = result;
            loadMenu();
        };

        testDatabaseViewModel.getLoggedUserCollectionListLiveData().observe(getViewLifecycleOwner(),emptyCollectionsObserver);
        testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
        bookViewModel.getCompleteCollectionListLiveData().observe(getViewLifecycleOwner(), fetchedCollectionsObserver);
        testDatabaseViewModel.getNotifications().observe(getViewLifecycleOwner(), fetchedNotificationsObserver);
    }

   private void initRecyclerView(){
       collectionAdapter = new CollectionAdapter(collection -> {
           if(collection != null){
               NavDirections action = ProfileFragmentDirections.actionProfileFragmentToCollectionFragment(collection, collection.getName(), loggedUser.getIdToken());
               Navigation.findNavController(requireView()).navigate(action);
           }
       });
       RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
       fragmentProfileBinding.recyclerviewCollections.setLayoutManager(layoutManager);
       fragmentProfileBinding.recyclerviewCollections.setAdapter(collectionAdapter);
   }

    public void loadMenu(){
        SwitchCompat switchButton = Objects.requireNonNull(fragmentProfileBinding.navView.getMenu()
                .findItem(R.id.nav_switch).getActionView()).findViewById(R.id.switch_compat);
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

                //managing badge notifications itemMenu
                final MenuItem menuItem = menu.findItem(R.id.action_notifications);
                View actionView = menuItem.getActionView();
                assert actionView != null;
                TextView notificationsTextView = actionView.findViewById(R.id.notification_appbar_profile_badge);
                //managing notification number
                int notificationsNumber = 0;
                for (String type: notifications.keySet()) {
                    for (Notification notification: Objects.requireNonNull(notifications.get(type))) {
                        if(!notification.isRead()) {
                            notificationsNumber++;
                        }
                    }
                }
                notificationsTextView.setText(String.format("%s", notificationsNumber));
                setupBadge(notificationsTextView);

                //managing onClick itemMenu
                actionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_notificationsFragment);
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_settings) {
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

                                da fare dopo aver implementato il database locale ig
                                 */
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
            NavDirections action = ProfileFragmentDirections.actionProfileFragmentToCreateCollectionDialog(loggedUser.getIdToken());
            Navigation.findNavController(requireView()).navigate(action);
        });
    }
    private void initFollowersSection() {
        View.OnClickListener followClickListener = v -> {
            NavDirections action = null;
            if(v.getId() == fragmentProfileBinding.textviewFollowerCounter.getId() || v.getId() == fragmentProfileBinding.textviewFollowerLabel.getId() ){
                action = ProfileFragmentDirections.actionProfileFragmentToFollowListFragment(loggedUser.getIdToken(), loggedUser.getUsername(), DESTINATION_FRAGMENT_FOLLOWER);
            } else if(v.getId() == fragmentProfileBinding.textviewFollowingCounter.getId() || v.getId() == fragmentProfileBinding.textviewFollowingLabel.getId()) {
                action = ProfileFragmentDirections.actionProfileFragmentToFollowListFragment(loggedUser.getIdToken(), loggedUser.getUsername(), DESTINATION_FRAGMENT_FOLLOWING);
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
            avatarId = R.drawable.class.getDeclaredField(loggedUser.getAvatar().toLowerCase()).getInt(null);
        } catch (Exception e) {
            avatarId = R.drawable.ic_baseline_profile_24;
        }
        Glide.with(requireActivity().getApplicationContext())
                .load(avatarId)
                .dontAnimate()
                .into(fragmentProfileBinding.avatarImageView);

        fragmentProfileBinding.textviewUsername.setText(loggedUser.getUsername());
        fragmentProfileBinding.textviewFollowerCounter.setText(String.valueOf(loggedUser.getFollowers().getCounter()));
        fragmentProfileBinding.textviewFollowingCounter.setText(String.valueOf(loggedUser.getFollowing().getCounter()));

        fragmentProfileBinding.textviewUserBiography.setText(loggedUser.getBiography());

        fragmentProfileBinding.textviewFacebook.setVisibility(View.GONE);
        fragmentProfileBinding.textviewTwitter.setVisibility(View.GONE);
        fragmentProfileBinding.textviewInstagram.setVisibility(View.GONE);
        for (int i = 0; i < loggedUser.getSocialLinks().size(); i++) {
            switch (loggedUser.getSocialLinks().get(i).getSocialPlatform()) {
                case "facebook":
                    fragmentProfileBinding.textviewFacebook.setText(loggedUser.getSocialLinks().get(i).getLink());
                    fragmentProfileBinding.textviewFacebook.setVisibility(View.VISIBLE);
                    break;
                case "twitter":
                    fragmentProfileBinding.textviewTwitter.setText(loggedUser.getSocialLinks().get(i).getLink());
                    fragmentProfileBinding.textviewTwitter.setVisibility(View.VISIBLE);
                    break;
                case "instagram":
                    fragmentProfileBinding.textviewInstagram.setText(loggedUser.getSocialLinks().get(i).getLink());
                    fragmentProfileBinding.textviewInstagram.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void setupBadge(TextView notificationsTextView) {
        int notificationNumber = Integer.parseInt(notificationsTextView.getText().toString());
        //Log.d("badge", Integer.toString(notificationNumber));
        if (notificationNumber == 0) {
            if (notificationsTextView.getVisibility() != View.GONE) {
                notificationsTextView.setVisibility(View.GONE);
            }
        } else {
            notificationsTextView.setText(String.valueOf(Math.min(notificationNumber, 99)));
            if (notificationsTextView.getVisibility() != View.VISIBLE) {
                notificationsTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        testDatabaseViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
        testDatabaseViewModel.getNotifications().removeObserver(fetchedNotificationsObserver);
        bookViewModel.getCompleteCollectionListLiveData().removeObserver(fetchedCollectionsObserver);
    }
}