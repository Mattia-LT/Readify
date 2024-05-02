package it.unimib.readify.ui.main;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import it.unimib.readify.R;
import it.unimib.readify.adapter.NotificationsAdapter;
import it.unimib.readify.databinding.FragmentNotificationsPageBinding;
import it.unimib.readify.model.ExternalUser;
import it.unimib.readify.model.Notification;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class NotificationsPageFragment extends Fragment {

    /*
        todo assure to initialize correct UI elements for each type of notificationList
         (in case there are going to be multiple types)
     */
    private FragmentNotificationsPageBinding fragmentNotificationsPageBinding;
    private String receivedContent;
    private TestDatabaseViewModel testDatabaseViewModel;
    private HashMap<String, ArrayList<Notification>> notifications;
    private User user;
    private Observer<Result> loggedUserObserver;
    private Observer<HashMap<String, ArrayList<Notification>>> fetchedNotificationsObserver;
    private NotificationsAdapter notificationsAdapter;

    public NotificationsPageFragment() {}

    public static NotificationsPageFragment newInstance() {
        return new NotificationsPageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        receivedContent =  NotificationsPageFragmentArgs.fromBundle(getArguments()).getContent();
        fragmentNotificationsPageBinding = FragmentNotificationsPageBinding.inflate(inflater,container,false);
        return fragmentNotificationsPageBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initRecyclerView() should be executed before initObservers()
        initRecyclerView();
        loadMenu();
        initViewModels();
        initObservers();
    }

    private void loadMenu(){
        //Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);
        toolbar.setTitle(receivedContent);

        // Enable the back button
        Drawable coloredIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_24);
        int newColor = getResources().getColor(R.color.white, null);
        if (coloredIcon != null) {
            coloredIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);
        }
        toolbar.setNavigationIcon(coloredIcon);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    public void initViewModels() {
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);
    }

    public void initObservers() {
        /*
            todo in case user is going to open the app by tapping a notification,
             it probably needs to add userObserver (because method fetchNotifications needs user idToken)
         */

        /*
            @run is used to manage completeFetchNotifications method invocation:
             instead of creating another LiveData variable (to memorize complete notifications),
             system updates the same LiveData (@Notifications in VM); @run interrupt an infinite loop
             of method invocation
         */
        final boolean[] run = {true};
        loggedUserObserver = result -> {
            if(result.isSuccess()) {
                this.user = ((Result.UserSuccess) result).getData();
                run[0] = true;
                testDatabaseViewModel.fetchNotifications(user.getIdToken());
            }
        };

        fetchedNotificationsObserver = result -> {
            notifications = result;
            //sort by date
            for (String key: notifications.keySet()) {
                Objects.requireNonNull(notifications.get(key)).sort(Collections.reverseOrder());
            }
            //set followedByUser
            for (Notification notification: Objects.requireNonNull(notifications.get("newFollowers"))) {
                for (ExternalUser following: user.getFollowing().getUsers()) {
                    if(notification.getIdToken().equals(following.getIdToken())) {
                        notification.setFollowedByUser(true);
                    }
                }
            }
            //complete fetch
            if(run[0]) {
                run[0] = false;
                testDatabaseViewModel.completeFetchNotifications(result);
            }
            if(checkFetchingNotifications()) {
                updateUI();
            }
        };
        testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
        testDatabaseViewModel.getNotifications().observe(getViewLifecycleOwner(), fetchedNotificationsObserver);
    }

    public void updateUI() {
        if(notifications.get(receivedContent) != null) {
            if(Objects.requireNonNull(notifications.get(receivedContent)).isEmpty()) {
                fragmentNotificationsPageBinding.notificationsPageNoNotifications.setVisibility(View.VISIBLE);
                fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setVisibility(View.GONE);
            } else {
                ArrayList<Notification> notificationsToRead = new ArrayList<>();
                for (Notification notification: Objects.requireNonNull(notifications.get(receivedContent))) {
                    if(notification.isRead())
                        notificationsToRead.add(notification);
                }
                if(!notificationsToRead.isEmpty()) {
                    notificationsAdapter.submitList(notificationsToRead);
                } else {
                    fragmentNotificationsPageBinding.notificationsPageNoNotifications.setVisibility(View.VISIBLE);
                }
                if(notificationsToRead.size() < Objects.requireNonNull(notifications.get(receivedContent)).size()) {
                    fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setVisibility(View.VISIBLE);
                    setViewAllNotifications();
                }
                if(fragmentNotificationsPageBinding.notificationsPageNoNotifications.getVisibility() == View.VISIBLE
                    && fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.getVisibility() == View.VISIBLE) {
                    fragmentNotificationsPageBinding.notificationsPageSeparator.setVisibility(View.VISIBLE);
                } else {
                    fragmentNotificationsPageBinding.notificationsPageSeparator.setVisibility(View.GONE);
                }
            }
        } else {
            fragmentNotificationsPageBinding.notificationsPageNoNotifications.setVisibility(View.VISIBLE);
            fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setVisibility(View.GONE);
        }
    }

    public void initRecyclerView(){
        notificationsAdapter = new NotificationsAdapter(new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onNotificationItemClick(Notification notification) {
                NavDirections action = NotificationsPageFragmentDirections
                        .actionNotificationsPageFragmentToUserDetailsFragment(notification.getIdToken(), notification.getUsername());
                Navigation.findNavController(requireView()).navigate(action);
            }
            @Override
            public void onFollowUser(String externalUserIdToken) {
                testDatabaseViewModel.followUser(user.getIdToken(), externalUserIdToken);
            }
            @Override
            public void onUnfollowUser(String externalUserIdToken) {
                testDatabaseViewModel.unfollowUser(user.getIdToken(), externalUserIdToken);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        fragmentNotificationsPageBinding.notificationsPageRecyclerView.setLayoutManager(layoutManager);
        fragmentNotificationsPageBinding.notificationsPageRecyclerView.setAdapter(notificationsAdapter);
    }

    public void setViewAllNotifications() {
        fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setOnClickListener(v -> {
            if(notifications.get(receivedContent) != null) {
                notificationsAdapter.submitList(notifications.get(receivedContent));
            }
            fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setVisibility(View.GONE);
            fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setOnClickListener(null);
        });
    }

    public boolean checkFetchingNotifications() {
        boolean fetchIsCompleted = true;
        switch (receivedContent) {
            case "newFollowers":
                for (Notification notification: Objects.requireNonNull(notifications.get("newFollowers"))) {
                    if(notification.getUsername() == null || notification.getAvatar() == null) {
                        fetchIsCompleted = false;
                        break;
                    }
                }
                break;
            case "recommendedBooks":
                break;
            case "sharedProfiles":
                break;
            case "system":
                break;
            case "statistics":
                break;
        }
        return fetchIsCompleted;
    }
}