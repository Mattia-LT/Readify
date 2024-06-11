package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.NOTIFICATION_NEW_FOLLOWER;
import static it.unimib.readify.util.Constants.NOTIFICATION_RECOMMENDED_BOOKS;
import static it.unimib.readify.util.Constants.NOTIFICATION_SHARED_PROFILES;
import static it.unimib.readify.util.Constants.NOTIFICATION_STATISTICS;
import static it.unimib.readify.util.Constants.NOTIFICATION_SYSTEM;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import it.unimib.readify.databinding.FragmentNotificationsBinding;
import it.unimib.readify.model.Notification;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;

public class NotificationsFragment extends Fragment {

    /*
        For future developments, any dedicated implementations for each type of notification
        will need to be implemented here
     */

    private FragmentNotificationsBinding fragmentNotificationsBinding;
    private UserViewModel userViewModel;
    private HashMap<String, ArrayList<Notification>> notifications;
    private Observer<HashMap<String, ArrayList<Notification>>> fetchedNotificationsObserver;

    public NotificationsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentNotificationsBinding = FragmentNotificationsBinding.inflate(inflater,container,false);
        return fragmentNotificationsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        initObservers();
        setUpNavigationToNotificationPageSection();
    }


    public void initViewModels() {
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }

    public void initObservers() {
        fetchedNotificationsObserver = result -> {
            notifications = result;
            updateUI();
        };

        userViewModel.getNotifications().observe(getViewLifecycleOwner(), fetchedNotificationsObserver);
    }

    private void setUpNavigationToNotificationPageSection() {
        fragmentNotificationsBinding.notificationsNewFollowersContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment(NOTIFICATION_NEW_FOLLOWER);
            Navigation.findNavController(requireView()).navigate(action);
        });
        fragmentNotificationsBinding.notificationsRecommendedBooksContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment(NOTIFICATION_RECOMMENDED_BOOKS);
            Navigation.findNavController(requireView()).navigate(action);
        });
        fragmentNotificationsBinding.notificationsSharedProfilesContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment(NOTIFICATION_SHARED_PROFILES);
            Navigation.findNavController(requireView()).navigate(action);
        });
        fragmentNotificationsBinding.notificationsSystemContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment(NOTIFICATION_SYSTEM);
            Navigation.findNavController(requireView()).navigate(action);
        });
        fragmentNotificationsBinding.notificationsStatisticsContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment(NOTIFICATION_STATISTICS);
            Navigation.findNavController(requireView()).navigate(action);
        });
    }

    public void updateUI() {
        for (String key: notifications.keySet()) {
            int notificationsToRead = 0;
            for (Notification notification: Objects.requireNonNull(notifications.get(key))) {
                if(!notification.isRead()){
                    notificationsToRead++;
                }
            }
            switch (key) {
                case NOTIFICATION_NEW_FOLLOWER:
                    fragmentNotificationsBinding.notificationNewFollowersIconBadge.setText(String.format("%s", notificationsToRead));
                    break;
                case NOTIFICATION_RECOMMENDED_BOOKS:
                    fragmentNotificationsBinding.notificationRecommendedBooksIconBadge.setText(String.format("%s", notificationsToRead));
                    break;
                case NOTIFICATION_SHARED_PROFILES:
                    fragmentNotificationsBinding.notificationSharedProfilesIconBadge.setText(String.format("%s", notificationsToRead));
                    break;
                case NOTIFICATION_SYSTEM:
                    fragmentNotificationsBinding.notificationSystemIconBadge.setText(String.format("%s", notificationsToRead));
                    break;
                case NOTIFICATION_STATISTICS:
                    fragmentNotificationsBinding.notificationStatisticsIconBadge.setText(String.format("%s", notificationsToRead));
                    break;
            }
        }
        setupBadge(fragmentNotificationsBinding.notificationNewFollowersIconBadge);
        setupBadge(fragmentNotificationsBinding.notificationRecommendedBooksIconBadge);
        setupBadge(fragmentNotificationsBinding.notificationSharedProfilesIconBadge);
        setupBadge(fragmentNotificationsBinding.notificationSystemIconBadge);
        setupBadge(fragmentNotificationsBinding.notificationStatisticsIconBadge);
    }

    private void setupBadge(TextView notificationsTextView) {
        int notificationNumber;
        if(notificationsTextView.getText().toString().isEmpty()) {
            notificationNumber = 0;
        } else {
            notificationNumber = Integer.parseInt(notificationsTextView.getText().toString());
        }
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
        userViewModel.getNotifications().removeObserver(fetchedNotificationsObserver);
    }
}