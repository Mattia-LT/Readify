package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import it.unimib.readify.R;
import it.unimib.readify.adapter.NotificationsAdapter;
import it.unimib.readify.databinding.FragmentNotificationsPageBinding;
import it.unimib.readify.model.Notification;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.CustomViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;

public class NotificationsPageFragment extends Fragment {

    /*
        todo assure to initialize correct UI elements for each type of notificationList
         (in case there are going to be multiple types)
     */
    private FragmentNotificationsPageBinding fragmentNotificationsPageBinding;
    private String receivedContent;
    private UserViewModel userViewModel;
    private HashMap<String, ArrayList<Notification>> notifications;
    private User user;
    private Observer<Result> loggedUserObserver;
    private Observer<HashMap<String, ArrayList<Notification>>> fetchedNotificationsObserver;
    private NotificationsAdapter notificationsAdapter;
    private boolean isShowAllButtonVisible;
    private boolean isShowAllButtonPressed;

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

        isShowAllButtonVisible = false;
        isShowAllButtonPressed = false;
        //initRecyclerView() should be executed before initObservers()
        initRecyclerView();
        loadMenu();
        initViewModels();
        initObservers();

        fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setOnClickListener(v -> {
            if(notifications.get(receivedContent) != null) {
                notificationsAdapter.submitList(notifications.get(receivedContent));

            }
            isShowAllButtonVisible = false;
            isShowAllButtonPressed = true;
            fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setVisibility(View.GONE);
            //fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setOnClickListener(null);
        });
    }

    private void loadMenu(){
        //Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);
        toolbar.setTitle(receivedContent);
    }

    public void initViewModels() {
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
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
        loggedUserObserver = result -> {
            if(result.isSuccess()) {
                this.user = ((Result.UserSuccess) result).getData();
                notificationsAdapter.submitFollowings(user.getFollowing().getUsers(), user.getIdToken());
                userViewModel.fetchNotifications(user.getIdToken());
            }
        };

        fetchedNotificationsObserver = result -> {
            notifications = result;
            if(notifications != null) {
                Log.d("notifications check", Objects.requireNonNull(notifications.toString()));
                updateUI();
            }
        };
        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
        userViewModel.getNotifications().observe(getViewLifecycleOwner(), fetchedNotificationsObserver);
    }

    public void updateUI() {
        if(notifications.get(receivedContent) != null) {
            ArrayList<Notification> notificationsToRead = new ArrayList<>();
            for (Notification notification: Objects.requireNonNull(notifications.get(receivedContent))) {
                if(!notification.isRead()) {
                    notificationsToRead.add(notification);
                }
            }

            //no notifications
            if(notificationsToRead.isEmpty()) {
                fragmentNotificationsPageBinding.notificationsPageNoNotifications.setVisibility(View.VISIBLE);
            } else {
                fragmentNotificationsPageBinding.notificationsPageNoNotifications.setVisibility(View.GONE);
            }
            //button
            if(!isShowAllButtonVisible) {
                notificationsAdapter.submitList(notifications.get(receivedContent));
                if(notificationsToRead.size() < Objects.requireNonNull(notifications.get(receivedContent)).size()
                        && !isShowAllButtonPressed) {
                    Log.d("updateUI", "showAll true");
                    fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setVisibility(View.VISIBLE);
                    isShowAllButtonVisible = true;
                } else {
                    Log.d("updateUI", "showAll false");
                    fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setVisibility(View.GONE);
                }
            } else {
                notificationsAdapter.submitList(notificationsToRead);
            }
            //separator
            if((fragmentNotificationsPageBinding.notificationsPageNoNotifications.getVisibility() == View.VISIBLE
                    && fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.getVisibility() == View.VISIBLE)
                || (fragmentNotificationsPageBinding.notificationsPageNoNotifications.getVisibility() == View.VISIBLE
                    && !notificationsAdapter.getCurrentList().isEmpty())) {
                fragmentNotificationsPageBinding.notificationsPageSeparator.setVisibility(View.VISIBLE);
            } else {
                fragmentNotificationsPageBinding.notificationsPageSeparator.setVisibility(View.GONE);
            }
        } else {
            Log.d("notifications null", "null");
            fragmentNotificationsPageBinding.notificationsPageNoNotifications.setVisibility(View.VISIBLE);
            fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setVisibility(View.GONE);
        }
    }

    public void initRecyclerView(){
        notificationsAdapter = new NotificationsAdapter(new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onNotificationItemClick(Notification notification) {
                NavDirections action = NotificationsPageFragmentDirections
                        .actionNotificationsPageFragmentToUserDetailsFragment(notification.getIdToken(), notification.getUser().getUsername());
                Navigation.findNavController(requireView()).navigate(action);
            }
            @Override
            public void onFollowUser(String externalUserIdToken) {
                //todo "show all" should not popping up
                userViewModel.followUser(user.getIdToken(), externalUserIdToken);
            }
            @Override
            public void onUnfollowUser(String externalUserIdToken) {
                userViewModel.unfollowUser(user.getIdToken(), externalUserIdToken);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        fragmentNotificationsPageBinding.notificationsPageRecyclerView.setLayoutManager(layoutManager);
        fragmentNotificationsPageBinding.notificationsPageRecyclerView.setAdapter(notificationsAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userViewModel.readNotifications(user.getIdToken(), receivedContent);
        userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
        userViewModel.getNotifications().removeObserver(fetchedNotificationsObserver);
    }
}