package it.unimib.readify.ui.main;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FragmentNotificationsBinding;
import it.unimib.readify.model.Notification;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding fragmentNotificationsBinding;
    private TestDatabaseViewModel testDatabaseViewModel;
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

        loadMenu();
        initViewModels();
        initObservers();
        navigateToNotificationPage();
    }

    private void loadMenu(){
        //Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);
        toolbar.setTitle("Activity notifications");
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
        fetchedNotificationsObserver = result -> {
            notifications = result;
            Log.d("notifications", notifications.toString());
            updateUI();
        };

        testDatabaseViewModel.getNotifications().observe(getViewLifecycleOwner(), fetchedNotificationsObserver);
    }

    private void navigateToNotificationPage() {
        fragmentNotificationsBinding.notificationsNewFollowersContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment("newFollowers");
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
                case "newFollowers":
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