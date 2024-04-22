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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.material.appbar.MaterialToolbar;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding fragmentNotificationsBinding;

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

    private void navigateToNotificationPage() {
        fragmentNotificationsBinding.notificationsNewFollowerContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment(0);
            Navigation.findNavController(requireView()).navigate(action);
        });
        fragmentNotificationsBinding.notificationsRecommendedContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment(1);
            Navigation.findNavController(requireView()).navigate(action);
        });
        fragmentNotificationsBinding.notificationsSharedProfilesContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment(2);
            Navigation.findNavController(requireView()).navigate(action);
        });
        fragmentNotificationsBinding.notificationsSystemContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment(3);
            Navigation.findNavController(requireView()).navigate(action);
        });
        fragmentNotificationsBinding.notificationsStatisticsContainer.setOnClickListener(v -> {
            NavDirections action = NotificationsFragmentDirections.actionNotificationsFragmentToNotificationPageFragment(4);
            Navigation.findNavController(requireView()).navigate(action);
        });
    }
}