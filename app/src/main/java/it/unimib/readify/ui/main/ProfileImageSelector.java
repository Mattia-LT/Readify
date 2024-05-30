package it.unimib.readify.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import it.unimib.readify.R;
import it.unimib.readify.adapter.ProfileImageSelectorAdapter;
import it.unimib.readify.databinding.FragmentProfileImageSelectorBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;

public class ProfileImageSelector extends Fragment implements ProfileImageSelectorAdapter.OnImageClickListener {

    private FragmentProfileImageSelectorBinding fragmentProfileImageSelectorBinding;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ProfileImageSelectorAdapter profileImageSelectorAdapter;
    private UserViewModel userViewModel;
    private Observer<Result> userObserver;
    private User loggedUser;

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

        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);

        userObserver = result -> {
            if(result.isSuccess()) {
                loggedUser = ((Result.UserSuccess)result).getData();
            } else {
                Snackbar.make(view, ((Result.Error)result).getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        };

        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), userObserver);

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
        loggedUser.setAvatar("avatar"+(position+1));
        userViewModel.setUserAvatar(loggedUser);
        Toast.makeText(requireContext(), "Immagine selezionata: " + position, Toast.LENGTH_SHORT).show();
        NavDirections action = ProfileImageSelectorDirections.actionProfileImageSelectorFragmentToEditProfileFragment();
        Navigation.findNavController(requireView()).navigate(action);
    }
}
