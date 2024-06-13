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

    /**
        For future developments, any dedicated implementations for each type of notification
        will need to be implemented here
     */

    private final String TAG = NotificationsPageFragment.class.getSimpleName();

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
        loadMenu();
        initViewModels();
        initObservers();
        initRecyclerView();
        setUpShowAllSection();
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
        loggedUserObserver = loggedUserResult -> {
            if(loggedUserResult.isSuccess()) {
                this.user = ((Result.UserSuccess) loggedUserResult).getData();
                notificationsAdapter.submitFollowings(user.getFollowing().getUsers());
                userViewModel.fetchNotifications(user.getIdToken());
            } else {
                String errorMessage = ((Result.Error) loggedUserResult).getMessage();
                Log.e(TAG, "Error: Logged user fetch wasn't successful -> " + errorMessage);
            }
        };

        fetchedNotificationsObserver = result -> {
            notifications = result;
            if(notifications != null) {
                updateUI();
            }
        };
        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
        userViewModel.getNotifications().observe(getViewLifecycleOwner(), fetchedNotificationsObserver);
    }

    private void updateUI() {
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
                    fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setVisibility(View.VISIBLE);
                    isShowAllButtonVisible = true;
                } else {
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
            Log.e(TAG, "error: notifications instance is null");
            fragmentNotificationsPageBinding.notificationsPageNoNotifications.setVisibility(View.VISIBLE);
            fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setVisibility(View.GONE);
        }
    }

    private void initRecyclerView(){
        notificationsAdapter = new NotificationsAdapter(new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onNotificationItemClick(Notification notification) {
                NavDirections action = NotificationsPageFragmentDirections
                        .actionNotificationsPageFragmentToUserDetailsFragment(notification.getIdToken(), notification.getUser().getUsername());
                Navigation.findNavController(requireView()).navigate(action);
            }
            @Override
            public void onFollowUser(String followedUserIdToken) {
                userViewModel.followUser(user.getIdToken(), followedUserIdToken);
            }
            @Override
            public void onUnfollowUser(String followedUserIdToken) {
                userViewModel.unfollowUser(user.getIdToken(), followedUserIdToken);
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());
        fragmentNotificationsPageBinding.notificationsPageRecyclerView.setLayoutManager(layoutManager);
        fragmentNotificationsPageBinding.notificationsPageRecyclerView.setAdapter(notificationsAdapter);
    }

    private void setUpShowAllSection() {
        fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setOnClickListener(v -> {
            if(notifications.get(receivedContent) != null) {
                notificationsAdapter.submitList(notifications.get(receivedContent));

            }
            isShowAllButtonVisible = false;
            isShowAllButtonPressed = true;
            fragmentNotificationsPageBinding.notificationsPageShowAllTextContainer.setVisibility(View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userViewModel.readNotifications(user.getIdToken(), receivedContent);
        userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
        userViewModel.getNotifications().removeObserver(fetchedNotificationsObserver);
    }
}