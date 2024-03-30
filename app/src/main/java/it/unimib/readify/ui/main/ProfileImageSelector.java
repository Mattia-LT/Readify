package it.unimib.readify.ui.main;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;

import org.apache.commons.validator.routines.EmailValidator;

import de.hdodenhof.circleimageview.CircleImageView;
import it.unimib.readify.R;
import it.unimib.readify.adapter.ProfileImageSelectorAdapter;
import it.unimib.readify.databinding.FragmentProfileImageSelectorBinding;
import it.unimib.readify.databinding.FragmentSettingsBinding;

public class ProfileImageSelector extends Fragment implements ProfileImageSelectorAdapter.OnImageClickListener {

    private FragmentProfileImageSelectorBinding fragmentProfileImageSelectorBinding;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ProfileImageSelectorAdapter profileImageSelectorAdapter;

    private int[] arr = {
            R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3, R.drawable.avatar4, R.drawable.avatar5,
            R.drawable.avatar6, R.drawable.avatar7, R.drawable.avatar8, R.drawable.avatar9, R.drawable.avatar10,
            R.drawable.avatar11, R.drawable.avatar12, R.drawable.avatar13, R.drawable.avatar14, R.drawable.avatar15,
            R.drawable.avatar16, R.drawable.avatar17, R.drawable.avatar18, R.drawable.avatar19, R.drawable.avatar20,
            R.drawable.avatar21, R.drawable.avatar22, R.drawable.avatar23, R.drawable.avatar24
    };

    private int imageResourceId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentProfileImageSelectorBinding = FragmentProfileImageSelectorBinding.inflate(inflater, container, false);
        return fragmentProfileImageSelectorBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadMenu();
        recyclerView = fragmentProfileImageSelectorBinding.recyclerviewProfileImage;
        layoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        profileImageSelectorAdapter = new ProfileImageSelectorAdapter(arr, this,requireActivity().getApplication());
        recyclerView.setAdapter(profileImageSelectorAdapter);
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onImageClick(int position) {
        imageResourceId = arr[position];
        Toast.makeText(requireContext(), "Immagine selezionata: " + position, Toast.LENGTH_SHORT).show();
        NavDirections action = ProfileImageSelectorDirections.actionProfileImageSelectorFragmentToSettingsFragment(imageResourceId);
        Navigation.findNavController(requireView()).navigate(action);
    }

    public void loadMenu(){
        // Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);

        // Enable the back button
        Drawable coloredIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_24);
        int newColor = getResources().getColor(R.color.white, null);
        if (coloredIcon != null) {
            coloredIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);
        }
        toolbar.setNavigationIcon(coloredIcon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}
