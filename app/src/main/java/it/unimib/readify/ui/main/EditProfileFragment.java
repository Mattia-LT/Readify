package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.EMAIL_ADDRESS;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.PASSWORD;
import static it.unimib.readify.util.Constants.USERNAME_AVAILABLE;
import static it.unimib.readify.util.Constants.USERNAME_ERROR;
import static it.unimib.readify.util.Constants.USERNAME_NOT_AVAILABLE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FragmentEditProfileBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;

public class EditProfileFragment extends Fragment {
    private final String TAG = EditProfileFragment.class.getSimpleName();

    private FragmentEditProfileBinding fragmentEditProfileBinding;
    private UserViewModel userViewModel;
    private Observer<Result> loggedUserObserver;
    private Observer<String> usernameErrorObserver;
    private DataEncryptionUtil dataEncryptionUtil;
    private User user;
    private User onSaveUser;
    private boolean isUsernameAvailable;
    private boolean isUserChanged;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentEditProfileBinding = FragmentEditProfileBinding.inflate(inflater,container,false);
        return fragmentEditProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        isUsernameAvailable = false;
        isUserChanged = false;

        initViewModels();
        initObservers();

        fragmentEditProfileBinding.profileImageSelect.setOnClickListener(v -> {
            NavDirections action = EditProfileFragmentDirections.actionEditProfileFragmentToProfileImageSelectorFragment();
            Navigation.findNavController(requireView()).navigate(action);
        });

        fragmentEditProfileBinding.textInputEditTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed for this implementation
            }

            @Override
            public void afterTextChanged(Editable s) {
                userViewModel.isUsernameAvailable(s.toString().trim());
            }
        });

        fragmentEditProfileBinding.buttonConfirmEdit.setOnClickListener(v -> {
            fragmentEditProfileBinding.settingsUsernameErrorMessage.setText("");
            fragmentEditProfileBinding.settingsUsernameErrorMessage.setVisibility(View.GONE);
            fragmentEditProfileBinding.settingsBioErrorMessage.setText("");
            fragmentEditProfileBinding.settingsBioErrorMessage.setVisibility(View.GONE);
            if(fragmentEditProfileBinding.textInputEditTextUsername.getText() != null
                    && fragmentEditProfileBinding.textInputEditTextBio.getText() != null) {
                if(!fragmentEditProfileBinding.textInputEditTextUsername.getText().toString().trim().isEmpty()
                        || !fragmentEditProfileBinding.textInputEditTextBio.getText().toString().trim().isEmpty()) {
                    onChangeUsername();
                    onChangeBio();
                } else {
                    Toast.makeText(requireContext(), R.string.fields_not_filled_in, Toast.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(view, R.string.unexpected_error, Snackbar.LENGTH_SHORT).show();
            }
        });

        fragmentEditProfileBinding.buttonUndoChanges.setOnClickListener(v -> {
            fragmentEditProfileBinding.textInputEditTextUsername.setText("");
            fragmentEditProfileBinding.textInputEditTextBio.setText("");
        });

        //Handling sensible data navigation
        try {
            String savedEmail = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
            String savedPassword = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
            if(savedEmail != null && savedPassword != null) {
                fragmentEditProfileBinding.buttonNavigateToSensible.setVisibility(View.VISIBLE);
                fragmentEditProfileBinding.buttonNavigateToSensible
                        .setOnClickListener(e -> {
                            NavDirections action = EditProfileFragmentDirections.actionEditProfileFragmentToEditSensibleFragment();
                            Navigation.findNavController(requireView()).navigate(action);
                        });
            } else {
                fragmentEditProfileBinding.buttonNavigateToSensible.setVisibility(View.GONE);
                fragmentEditProfileBinding.buttonNavigateToSensible.setOnClickListener(null);
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initViewModels() {
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }

    public void initObservers() {
        loggedUserObserver = result -> {
            if(result.isSuccess()) {
                user = ((Result.UserSuccess)result).getData();
                onSaveUser = new User(user);
                updateUI();
                if(isUserChanged) {
                    isUserChanged = false;
                    Toast.makeText(requireContext(), R.string.updated_profile, Toast.LENGTH_SHORT).show();
                }
            } else {
                String errorMessage = ((Result.Error) result).getMessage();
                Log.e(TAG, "Error: Logged user fetch wasn't successful -> " + errorMessage);
            }
        };

        usernameErrorObserver = result -> {
            switch (result) {
                case USERNAME_AVAILABLE:
                    isUsernameAvailable = true;
                    break;
                case USERNAME_NOT_AVAILABLE:
                    isUsernameAvailable = false;
                    break;
                case USERNAME_ERROR:
                    Toast.makeText(requireContext(), R.string.generic_error_username, Toast.LENGTH_SHORT).show();
                    isUsernameAvailable = false;
                    break;
            }
        };

        userViewModel.getUserMediatorLiveData()
                .observe(getViewLifecycleOwner(), loggedUserObserver);
        userViewModel.getUsernameAvailableResult()
                .observe(getViewLifecycleOwner(), usernameErrorObserver);
    }

    private void onChangeUsername() {
        if(fragmentEditProfileBinding.textInputEditTextUsername.getText() != null) {
            String username = fragmentEditProfileBinding.textInputEditTextUsername.getText().toString().trim();
            TextView usernameError = fragmentEditProfileBinding.settingsUsernameErrorMessage;
            if (!username.isEmpty()) {
                if (username.length() > 20) {
                    usernameError.setText(R.string.error_username_length);
                    usernameError.setVisibility(View.VISIBLE);
                }
                else if (username.contains("/")
                        || username.contains("@")) {
                    usernameError.setText(R.string.error_username_illegal_symbols);
                    usernameError.setVisibility(View.VISIBLE);
                } else if(username.equals(user.getUsername())) {
                    Toast.makeText(requireContext(), R.string.username_already_own, Toast.LENGTH_SHORT).show();
                } else if(!isUsernameAvailable) {
                    usernameError.setText(R.string.username_already_taken);
                    usernameError.setVisibility(View.VISIBLE);
                } else {
                    onSaveUser.setUsername(username.toLowerCase());
                    userViewModel.setUserUsername(onSaveUser);
                    fragmentEditProfileBinding.textInputEditTextUsername.setText("");
                    isUserChanged = true;
                }
            }
        }
    }

    private void onChangeBio() {
        if(fragmentEditProfileBinding.textInputEditTextBio.getText() != null) {
            String bio = fragmentEditProfileBinding.textInputEditTextBio.getText().toString().trim();
            if (!bio.isEmpty()) {
                if (bio.length() > 100) {
                    fragmentEditProfileBinding.settingsBioErrorMessage.setText(R.string.error_bio_length);
                    fragmentEditProfileBinding.settingsBioErrorMessage.setVisibility(View.VISIBLE);
                } else {
                    onSaveUser.setBiography(bio);
                    userViewModel.setUserBiography(onSaveUser);
                    fragmentEditProfileBinding.textInputEditTextBio.setText("");
                    isUserChanged = true;
                }
            }
        }
    }

    public void updateUI(){
        int avatarId;
        if(user != null){
            try {
                avatarId = R.drawable.class.getDeclaredField(user.getAvatar().toLowerCase()).getInt(null);
            } catch (Exception e) {
                avatarId = R.drawable.ic_baseline_profile_24;
            }
            Glide.with(requireActivity().getApplication())
                    .load(avatarId)
                    .dontAnimate()
                    .into(fragmentEditProfileBinding.profileImageSelect);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentEditProfileBinding = null;
        userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
        userViewModel.getUsernameAvailableResult().removeObserver(usernameErrorObserver);
    }
}