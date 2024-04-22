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

import com.google.android.material.appbar.MaterialToolbar;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FragmentNotificationsPageBinding;

public class NotificationsPageFragment extends Fragment {
    private FragmentNotificationsPageBinding fragmentNotificationsPageBinding;
    private int receivedContent;

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

        loadMenu();
    }

    private void loadMenu(){
        //Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);
        switch (receivedContent) {
            case 0:
                toolbar.setTitle("New followers");
                break;
            case 1:
                toolbar.setTitle("Recommended books");
                break;
            case 2:
                toolbar.setTitle("Shared profiles");
                break;
            case 3:
                toolbar.setTitle("System notifications");
                break;
            case 4:
                toolbar.setTitle("Statistics");
                break;
        }

        // Enable the back button
        Drawable coloredIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_24);
        int newColor = getResources().getColor(R.color.white, null);
        if (coloredIcon != null) {
            coloredIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);
        }
        toolbar.setNavigationIcon(coloredIcon);
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
    }
}