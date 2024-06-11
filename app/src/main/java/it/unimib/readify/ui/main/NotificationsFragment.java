package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.NEW_FOLLOWER;

import android.os.Bundle;
import android.util.Log;
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

    private FragmentNotificationsBinding fragmentNotificationsBinding;
    private UserViewModel userViewModel;
    private HashMap<String, ArrayList<Notification>> notifications;
    Observer<HashMap<String, ArrayList<Notification>>> fetchedNotificationsObserver;

    public NotificationsFragment() {}

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

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
        navigateToNotificationPage();
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
        fetchedNotificationsObserver = result -> {
            notifications = result;
            Log.d("notifications", notifications.toString());
            updateUI();
        };

        userViewModel.getNotifications().observe(getViewLifecycleOwner(), fetchedNotificationsObserver);
    }

    private void navigateToNotificationPage() {
        fragmentNotificationsBinding.notificationsNewFollowersContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment(NEW_FOLLOWER);
            Navigation.findNavController(requireView()).navigate(action);
        });
        fragmentNotificationsBinding.notificationsRecommendedBooksContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment("recommendedBooks");
            Navigation.findNavController(requireView()).navigate(action);
        });
        fragmentNotificationsBinding.notificationsSharedProfilesContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment("sharedProfiles");
            Navigation.findNavController(requireView()).navigate(action);
        });
        fragmentNotificationsBinding.notificationsSystemContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment("system");
            Navigation.findNavController(requireView()).navigate(action);
        });
        fragmentNotificationsBinding.notificationsStatisticsContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment("statistics");
            Navigation.findNavController(requireView()).navigate(action);
        });
    }

    public void updateUI() {
        for (String key: notifications.keySet()) {
            int notificationsToRead = 0;
            for (Notification notification: Objects.requireNonNull(notifications.get(key))) {
                if(!notification.isRead())
                    notificationsToRead++;
            }
            switch (key) {
                case NEW_FOLLOWER:
                    fragmentNotificationsBinding.notificationNewFollowersIconBadge.setText(String.format("%s", notificationsToRead));
                    break;
                case "recommendedBooks":
                    fragmentNotificationsBinding.notificationRecommendedBooksIconBadge.setText(String.format("%s", notificationsToRead));
                    break;
                case "sharedProfiles":
                    fragmentNotificationsBinding.notificationSharedProfilesIconBadge.setText(String.format("%s", notificationsToRead));
                    break;
                case "system":
                    fragmentNotificationsBinding.notificationSystemIconBadge.setText(String.format("%s", notificationsToRead));
                    break;
                case "statistics":
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
}