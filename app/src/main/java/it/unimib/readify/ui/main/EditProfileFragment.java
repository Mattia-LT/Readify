package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.EMAIL_ADDRESS;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.PASSWORD;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private FragmentEditProfileBinding fragmentEditProfileBinding;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;
    private User user;
    private User onSaveUser;
    private boolean isUsernameAvailable;
    private boolean isUserChanged;
    private Observer<Result> userObserver;
    private Observer<String> usernameErrorObserver;

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

        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);

        userObserver = result -> {
            if(result.isSuccess()) {
                user = ((Result.UserSuccess)result).getData();
                onSaveUser = new User(user);
                updateUI();

                if(isUserChanged) {
                    isUserChanged = false;
                    Toast.makeText(requireContext(), "Profile has been updated", Toast.LENGTH_SHORT).show();
                }
            } else {
                //todo navigate to login(?)
                Snackbar.make(view, ((Result.Error)result).getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        };

        usernameErrorObserver = result -> {
            switch (result) {
                case "available":
                    isUsernameAvailable = true;
                    break;
                case "notAvailable":
                    isUsernameAvailable = false;
                    break;
                case "error":
                    Toast.makeText(requireContext(), "System: username error", Toast.LENGTH_SHORT).show();
                    isUsernameAvailable = false;
                    break;
            }
        };

        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), userObserver);
        userViewModel.getUsernameAvailableResult().observe(getViewLifecycleOwner(), usernameErrorObserver);

        fragmentEditProfileBinding.profileImageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireView()).navigate(R.id.action_editProfileFragment_to_profileImageSelectorFragment);
            }
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
                userViewModel.isUsernameAvailable(s.toString());
            }
        });

        fragmentEditProfileBinding.buttonConfirmEdit.setOnClickListener(v -> {
            fragmentEditProfileBinding.settingsUsernameErrorMessage.setText("");
            fragmentEditProfileBinding.settingsUsernameErrorMessage.setVisibility(View.GONE);
            fragmentEditProfileBinding.settingsBioErrorMessage.setText("");
            fragmentEditProfileBinding.settingsBioErrorMessage.setVisibility(View.GONE);
            if(fragmentEditProfileBinding.textInputEditTextUsername.getText() != null
                    && fragmentEditProfileBinding.textInputEditTextBio.getText() != null) {
                if(!fragmentEditProfileBinding.textInputEditTextUsername.getText().toString().isEmpty()
                        || !fragmentEditProfileBinding.textInputEditTextBio.getText().toString().isEmpty()) {
                    onChangeUsername();
                    onChangeBio();
                } else {
                    Toast.makeText(requireContext(), "Fill in at least one field", Toast.LENGTH_SHORT).show();
                }
            } else {
                //todo add Snack bar action (reload app?)
                Snackbar.make(view, "System error", Snackbar.LENGTH_SHORT).show();
            }
        });

        fragmentEditProfileBinding.buttonUndoChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentEditProfileBinding.textInputEditTextUsername.setText("");
                fragmentEditProfileBinding.textInputEditTextBio.setText("");
            }
        });

        //handling sensible data navigation
        try {
            String savedEmail = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
            String savedPassword = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
            if(savedEmail != null && savedPassword != null) {
                fragmentEditProfileBinding.buttonNavigateToSensible.setVisibility(View.VISIBLE);
                fragmentEditProfileBinding.buttonNavigateToSensible.setOnClickListener(e -> {
                    Navigation.findNavController(requireView()).navigate(R.id.action_editProfileFragment_to_editSensibleFragment);
                });
            } else {
                fragmentEditProfileBinding.buttonNavigateToSensible.setVisibility(View.GONE);
                fragmentEditProfileBinding.buttonNavigateToSensible.setOnClickListener(null);
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }



    }

    private void onChangeUsername() {
        if(fragmentEditProfileBinding.textInputEditTextUsername.getText() != null) {
            if (!fragmentEditProfileBinding.textInputEditTextUsername.getText().toString().trim().isEmpty()) {
                if (fragmentEditProfileBinding.textInputEditTextUsername.getText().toString().trim().length() > 20) {
                    fragmentEditProfileBinding.settingsUsernameErrorMessage.setText(R.string.error_username_length);
                    fragmentEditProfileBinding.settingsUsernameErrorMessage.setVisibility(View.VISIBLE);
                }
                else if (fragmentEditProfileBinding.textInputEditTextUsername.getText().toString().trim().contains("/")
                        || fragmentEditProfileBinding.textInputEditTextUsername.getText().toString().trim().contains("@")) {
                    fragmentEditProfileBinding.settingsUsernameErrorMessage.setText(R.string.error_username_illegal_symbols);
                    fragmentEditProfileBinding.settingsUsernameErrorMessage.setVisibility(View.VISIBLE);
                } else if(fragmentEditProfileBinding.textInputEditTextUsername.getText().toString().trim().equals(user.getUsername())) {
                    Toast.makeText(requireContext(), "This is already your username", Toast.LENGTH_SHORT).show();
                } else if(!isUsernameAvailable) {
                    fragmentEditProfileBinding.settingsUsernameErrorMessage.setText(R.string.username_already_taken);
                    fragmentEditProfileBinding.settingsUsernameErrorMessage.setVisibility(View.VISIBLE);
                } else {
                    onSaveUser.setUsername(fragmentEditProfileBinding.textInputEditTextUsername.getText().toString().trim().toLowerCase());
                    userViewModel.setUserUsername(onSaveUser);
                    fragmentEditProfileBinding.textInputEditTextUsername.setText("");
                    isUserChanged = true;
                }
            }
        }
    }

    private void onChangeBio() {
        if(fragmentEditProfileBinding.textInputEditTextBio.getText() != null) {
            if (!fragmentEditProfileBinding.textInputEditTextBio.getText().toString().trim().isEmpty()) {
                if (fragmentEditProfileBinding.textInputEditTextBio.getText().toString().trim().length() > 100) {
                    fragmentEditProfileBinding.settingsBioErrorMessage.setText("La bio non deve superare 100 caratteri");
                    fragmentEditProfileBinding.settingsBioErrorMessage.setVisibility(View.VISIBLE);
                }else if(fragmentEditProfileBinding.textInputEditTextBio.getText().toString().trim().equals(user.getBiography())) {
                    Toast.makeText(requireContext(), "Questa è gia la tua bio", Toast.LENGTH_SHORT).show();
                } else {
                    onSaveUser.setBiography(fragmentEditProfileBinding.textInputEditTextBio.getText().toString().trim());
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
        } else {
            Log.d("USER NULL", "USER NULL");
        }
    }
}