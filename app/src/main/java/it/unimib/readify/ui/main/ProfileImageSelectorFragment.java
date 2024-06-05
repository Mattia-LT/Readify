package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.PROFILE_IMAGE_TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.adapter.ProfileImageSelectorAdapter;
import it.unimib.readify.databinding.FragmentProfileImageSelectorBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;

public class ProfileImageSelectorFragment extends Fragment {

    private FragmentProfileImageSelectorBinding fragmentProfileImageSelectorBinding;
    private UserViewModel userViewModel;
    private User loggedUser;
    private List<Integer> avatars;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentProfileImageSelectorBinding = FragmentProfileImageSelectorBinding.inflate(inflater, container, false);
        return fragmentProfileImageSelectorBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        avatars = new ArrayList<>(Arrays.asList(
                R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3, R.drawable.avatar4, R.drawable.avatar5,
                R.drawable.avatar6, R.drawable.avatar7, R.drawable.avatar8, R.drawable.avatar9, R.drawable.avatar10,
                R.drawable.avatar11, R.drawable.avatar12, R.drawable.avatar13, R.drawable.avatar14, R.drawable.avatar15,
                R.drawable.avatar16, R.drawable.avatar17, R.drawable.avatar18, R.drawable.avatar19, R.drawable.avatar20,
                R.drawable.avatar21, R.drawable.avatar22, R.drawable.avatar23, R.drawable.avatar24
        ));

        initViewModels();
        initObservers();
        initRecyclerView();

    }

    private void initViewModels() {
        userViewModel = CustomViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }

    private void initObservers() {
        Observer<Result> userObserver = result -> {
            if (result.isSuccess()) {
                loggedUser = ((Result.UserSuccess) result).getData();
            } else {
                Snackbar.make(requireView(), ((Result.Error) result).getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        };

        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), userObserver);
    }

    private void initRecyclerView() {
        RecyclerView avatarsRecyclerView = fragmentProfileImageSelectorBinding.recyclerviewProfileImage;
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        avatarsRecyclerView.setLayoutManager(layoutManager);

        ProfileImageSelectorAdapter profileImageSelectorAdapter = new ProfileImageSelectorAdapter(resourceId -> {
            int newAvatarPosition = avatars.indexOf(resourceId);
            String newAvatar = PROFILE_IMAGE_TAG + (newAvatarPosition + 1);
            loggedUser.setAvatar(newAvatar);
            userViewModel.setUserAvatar(loggedUser);
            Log.d("ProfileImageSelectorFragment","Selected avatar: " + newAvatarPosition);
            NavDirections action = ProfileImageSelectorFragmentDirections.actionProfileImageSelectorFragmentToEditProfileFragment();
            Navigation.findNavController(requireView()).navigate(action);
        });

        avatarsRecyclerView.setAdapter(profileImageSelectorAdapter);
        avatarsRecyclerView.setHasFixedSize(true);

        profileImageSelectorAdapter.submitList(avatars);
    }

}
