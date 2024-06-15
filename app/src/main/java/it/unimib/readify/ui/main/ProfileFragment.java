package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.DARK_MODE;
import static it.unimib.readify.util.Constants.DESTINATION_FRAGMENT_FOLLOWER;
import static it.unimib.readify.util.Constants.DESTINATION_FRAGMENT_FOLLOWING;
import static it.unimib.readify.util.Constants.LIGHT_MODE;
import static it.unimib.readify.util.Constants.PREFERRED_THEME;
import static it.unimib.readify.util.Constants.SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.USER_VISIBILITY_PRIVATE;
import static it.unimib.readify.util.Constants.USER_VISIBILITY_PUBLIC;

import android.content.Intent;
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
import android.widget.Toast;

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
import it.unimib.readify.ui.startup.WelcomeActivity;
import it.unimib.readify.util.SharedPreferencesUtil;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.CollectionViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;

public class ProfileFragment extends Fragment{
    private final String TAG = ProfileFragment.class.getSimpleName();

    private FragmentProfileBinding fragmentProfileBinding;
    private UserViewModel userViewModel;
    private CollectionViewModel collectionViewModel;
    private BookViewModel bookViewModel;
    private CollectionAdapter collectionAdapter;
    private User loggedUser;
    private SharedPreferencesUtil sharedPreferencesUtil;
    private HashMap<String, ArrayList<Notification>> notifications = new HashMap<>();
    private Observer<HashMap<String, ArrayList<Notification>>> fetchedNotificationsObserver;
    private Observer<Result> loggedUserObserver;
    private Observer<Boolean> logoutResultObserver;
    private Observer<Boolean> deleteAllCollectionsResultObserver;
    private Observer<List<Result>> fetchedCollectionsObserver;

    public ProfileFragment() {}

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
        super.onViewCreated(view, savedInstanceState);
        sharedPreferencesUtil = new SharedPreferencesUtil(requireActivity().getApplication());
        initViewModels();
        initObservers();
        initRecyclerView();
        initCreateCollectionSection();
        initFollowersSection();

    }

    private void initViewModels() {
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);

        collectionViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(CollectionViewModel.class);

        bookViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(BookViewModel.class);
    }

    private void initObservers() {
        fetchedCollectionsObserver = results -> {
            List<Collection> collectionsList = results.stream()
                    .filter(result -> result instanceof Result.CollectionSuccess)
                    .map(result -> ((Result.CollectionSuccess) result).getData())
                    .collect(Collectors.toList());

            collectionAdapter.submitList(collectionsList);
            fragmentProfileBinding.progressBarProfile.setVisibility(View.GONE);
            fragmentProfileBinding.recyclerviewCollections.setVisibility(View.VISIBLE);
        };

        loggedUserObserver = loggedUserResult -> {
            if(loggedUserResult.isSuccess()) {
                this.loggedUser = ((Result.UserSuccess) loggedUserResult).getData();
                updateUI();
                fragmentProfileBinding.recyclerviewCollections.setVisibility(View.GONE);
                fragmentProfileBinding.progressBarProfile.setVisibility(View.VISIBLE);
                collectionViewModel.loadLoggedUserCollections();
                if(loggedUser.getIdToken() != null){
                    userViewModel.fetchNotifications(loggedUser.getIdToken());
                }
                loadMenu();
            } else {
                String errorMessage = ((Result.Error) loggedUserResult).getMessage();
                Log.e(TAG, "Error: Logged user fetch wasn't successful -> " + errorMessage);
            }
        };

        fetchedNotificationsObserver = result -> {
            notifications = result;
            if(notifications != null) {
                loadMenu();
            }
        };

        logoutResultObserver = result -> {
          if(result != null){
              if(result){
                  collectionViewModel.emptyLocalDb();
              } else {
                  Toast.makeText(requireContext(), R.string.error_logout, Toast.LENGTH_SHORT).show();
              }
          }
        };

        deleteAllCollectionsResultObserver = result -> {
            if(result != null){
                if(result){
                    userViewModel.setFirstLoading(true);
                    userViewModel.setContinueRegistrationFirstLoading(true);
                    userViewModel.deleteUserInfo();
                    bookViewModel.resetCarousels();
                    Intent intent = new Intent(requireActivity(), WelcomeActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
                    Toast.makeText(requireContext(), R.string.error_local_database, Toast.LENGTH_SHORT).show();
                }
            }
        };

        collectionViewModel.getLoggedUserCollectionListLiveData().observe(getViewLifecycleOwner(), fetchedCollectionsObserver);
        collectionViewModel.getDeleteAllCollectionResult().observe(getViewLifecycleOwner(),deleteAllCollectionsResultObserver);
        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
        userViewModel.getNotifications().observe(getViewLifecycleOwner(), fetchedNotificationsObserver);
        userViewModel.getLogoutResult().observe(getViewLifecycleOwner(), logoutResultObserver);
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
        SwitchCompat switchButtonTheme = Objects.requireNonNull(fragmentProfileBinding.navView.getMenu()
                .findItem(R.id.nav_switch_theme).getActionView()).findViewById(R.id.switch_compat);

        int currentNightMode = getSavedNightMode();

        //Set the switch on the current mode
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_YES:
                switchButtonTheme.setChecked(true);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                switchButtonTheme.setChecked(false);
                break;
        }

        switchButtonTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                        PREFERRED_THEME, DARK_MODE);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                sharedPreferencesUtil.writeStringData(SHARED_PREFERENCES_FILE_NAME,
                        PREFERRED_THEME, LIGHT_MODE);
            }
        });

        SwitchCompat switchButtonVisibility = Objects.requireNonNull(fragmentProfileBinding.navView.getMenu()
                .findItem(R.id.nav_switch_visibility).getActionView()).findViewById(R.id.switch_compat);

        if(loggedUser!= null){
            String currentVisibility = loggedUser.getVisibility();

            switch (currentVisibility) {
                case USER_VISIBILITY_PUBLIC:
                    switchButtonVisibility.setChecked(false);
                    break;
                case USER_VISIBILITY_PRIVATE:
                    switchButtonVisibility.setChecked(true);
                    break;
            }

            switchButtonVisibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String selectedVisibility;
                if (isChecked) {
                    selectedVisibility = USER_VISIBILITY_PRIVATE;
                } else {
                    selectedVisibility = USER_VISIBILITY_PUBLIC;
                }
                loggedUser.setVisibility(selectedVisibility);
                userViewModel.setUserVisibility(loggedUser);
            });

        } else {
            Log.e(TAG, "logged user is null, error during load visibility section");
        }

        //Set up the toolbar and remove all icons
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.profile_appbar_menu, menu);

                //managing badge notifications itemMenu
                final MenuItem menuItem = menu.findItem(R.id.action_notifications);
                View actionView = menuItem.getActionView();
                if(actionView != null){
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
                    actionView.setOnClickListener(v -> {
                        NavDirections action = ProfileFragmentDirections.actionProfileFragmentToNotificationsFragment();
                        Navigation.findNavController(requireView()).navigate(action);
                    });
                }
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

                    navigationView.setNavigationItemSelectedListener(menuItem1 -> {
                        int itemId = menuItem1.getItemId();

                        if (itemId == R.id.nav_edit_profile) {
                            Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_editProfileFragment);
                        } else if(itemId == R.id.nav_logout){
                            userViewModel.logout();
                        }

                        drawerLayout.closeDrawer(GravityCompat.END);
                        return true;
                    });
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private int getSavedNightMode() {
        int currentNightMode;
        String preferred_theme = sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME, PREFERRED_THEME);
        if(preferred_theme == null){
            currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        } else {
            if(preferred_theme.equals(DARK_MODE)){
                currentNightMode = Configuration.UI_MODE_NIGHT_YES;
            } else if(preferred_theme.equals(LIGHT_MODE)){
                currentNightMode = Configuration.UI_MODE_NIGHT_NO;
            } else {
                currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            }
        }
        return currentNightMode;
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

        if(loggedUser.getBiography() != null){
            fragmentProfileBinding.textviewUserBiography.setText(loggedUser.getBiography());
        } else {
            fragmentProfileBinding.textviewUserBiography.setText(getString(R.string.empty_biography_message));
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
        userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
        userViewModel.getNotifications().removeObserver(fetchedNotificationsObserver);
        userViewModel.getLogoutResult().removeObserver(logoutResultObserver);
        collectionViewModel.getLoggedUserCollectionListLiveData().removeObserver(fetchedCollectionsObserver);
        collectionViewModel.getDeleteAllCollectionResult().removeObserver(deleteAllCollectionsResultObserver);
    }
}