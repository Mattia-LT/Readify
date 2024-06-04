package it.unimib.readify.ui.main;

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

import org.apache.commons.validator.routines.EmailValidator;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FragmentEditProfileBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;

public class EditProfileFragment extends Fragment {

    private FragmentEditProfileBinding fragmentEditProfileBinding;
    private UserViewModel userViewModel;
    private User user;
    private User onSaveUser;
    private boolean isUsernameAvailable;
    private Observer<Result> userObserver;
    private Observer<String> usernameErrorObserver;
    private Observer<Boolean> emailErrorObserver;
    private Observer<Boolean> passwordErrorObserver;
    private String newPassword;
    private int imageResourceId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentEditProfileBinding = FragmentEditProfileBinding.inflate(inflater,container,false);
        return fragmentEditProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        isUsernameAvailable = false;

        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);

        userObserver = result -> {
            if(result.isSuccess()) {
                user = ((Result.UserSuccess)result).getData();
                onSaveUser = new User(user);
                updateUI();
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

        emailErrorObserver = result -> {
            /*
            switch (result) {
                case "available":
                    Toast.makeText(requireContext(), "Email updated", Toast.LENGTH_SHORT).show();
                    fragmentSettingsBinding.textInputEditTextEmail.setText("");
                    break;
                case "notAvailable":
                    fragmentSettingsBinding.settingsEmailErrorMessage.setText(R.string.email_already_taken);
                    fragmentSettingsBinding.settingsEmailErrorMessage.setVisibility(View.VISIBLE);
                    break;
                case "error":
                    //todo use snack bar instead (to implement an action)?
                    Toast.makeText(requireContext(), "System: email error", Toast.LENGTH_SHORT).show();
                    break;
            }

             */
            if(result) {
                Toast.makeText(requireContext(), "Email updated", Toast.LENGTH_SHORT).show();
                fragmentEditProfileBinding.textInputEditTextEmail.setText("");
            } else {
                Toast.makeText(requireContext(), "failure", Toast.LENGTH_SHORT).show();
            }
        };

        passwordErrorObserver = result -> {
            Log.d("email changed", result.toString());
            if(result) {
                Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show();
                fragmentEditProfileBinding.textInputEditTextPassword.setText("");
                fragmentEditProfileBinding.textInputEditTextPasswordConfirm.setText("");
            } else {
                //todo use snack bar instead (to implement an action)?
                Toast.makeText(requireContext(), "System: password error", Toast.LENGTH_SHORT).show();
            }
        };

        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), userObserver);
        userViewModel.getUsernameAvailableResult().observe(getViewLifecycleOwner(), usernameErrorObserver);
        userViewModel.getSourceEmailError().observe(getViewLifecycleOwner(), emailErrorObserver);
        userViewModel.getSourcePasswordError().observe(getViewLifecycleOwner(), passwordErrorObserver);
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
            fragmentEditProfileBinding.settingsEmailErrorMessage.setText("");
            fragmentEditProfileBinding.settingsEmailErrorMessage.setVisibility(View.GONE);
            fragmentEditProfileBinding.settingsBioErrorMessage.setText("");
            fragmentEditProfileBinding.settingsBioErrorMessage.setVisibility(View.GONE);
            fragmentEditProfileBinding.settingsPasswordErrorMessage.setText("");
            fragmentEditProfileBinding.settingsPasswordErrorMessage.setVisibility(View.GONE);
            fragmentEditProfileBinding.settingsConfirmPasswordErrorMessage.setText("");
            fragmentEditProfileBinding.settingsConfirmPasswordErrorMessage.setVisibility(View.GONE);

            if(fragmentEditProfileBinding.textInputEditTextUsername.getText() != null
                    && fragmentEditProfileBinding.textInputEditTextEmail.getText() != null
                    && fragmentEditProfileBinding.textInputEditTextBio.getText() != null
                    && fragmentEditProfileBinding.textInputEditTextPassword.getText() != null
                    && fragmentEditProfileBinding.textInputEditTextPasswordConfirm.getText() != null) {
                if(!fragmentEditProfileBinding.textInputEditTextUsername.getText().toString().isEmpty()
                        || !fragmentEditProfileBinding.textInputEditTextEmail.getText().toString().isEmpty()
                        || !fragmentEditProfileBinding.textInputEditTextBio.getText().toString().isEmpty()
                        || !fragmentEditProfileBinding.textInputEditTextPassword.getText().toString().isEmpty()
                        || !fragmentEditProfileBinding.textInputEditTextPasswordConfirm.getText().toString().isEmpty()) {
                    //todo add loading screen
                    //todo deselect accepted field
                    //todo setText("") when accepted or same value
                    //todo field value as placeholder?
                    //todo put result icon?
                    //todo interface pop up message every time it reloads the fragment

                    onChangeUsername();
                    onChangeEmail();
                    onChangeBio();
                    onChangePassword();
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
                fragmentEditProfileBinding.textInputEditTextEmail.setText("");
                fragmentEditProfileBinding.textInputEditTextBio.setText("");
                fragmentEditProfileBinding.textInputEditTextPassword.setText("");
                fragmentEditProfileBinding.textInputEditTextPasswordConfirm.setText("");
            }
        });
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
                    onSaveUser.setUsername(fragmentEditProfileBinding.textInputEditTextUsername.getText().toString().trim());
                    userViewModel.setUserUsername(onSaveUser);
                    fragmentEditProfileBinding.textInputEditTextUsername.setText("");
                    Toast.makeText(requireContext(), "Username updated", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void onChangeEmail() {
        if(fragmentEditProfileBinding.textInputEditTextEmail.getText() != null) {
            if (!fragmentEditProfileBinding.textInputEditTextEmail.getText().toString().trim().isEmpty()) {
                if(!EmailValidator.getInstance().isValid((fragmentEditProfileBinding.textInputEditTextEmail.getText().toString().trim()))) {
                    fragmentEditProfileBinding.settingsEmailErrorMessage.setText(R.string.email_invalid_input);
                    fragmentEditProfileBinding.settingsEmailErrorMessage.setVisibility(View.VISIBLE);
                } else if(fragmentEditProfileBinding.textInputEditTextEmail.getText().toString().trim().equals(user.getEmail())) {
                    Toast.makeText(requireContext(), "This is already your email", Toast.LENGTH_SHORT).show();
                } else {
                    String newEmail = fragmentEditProfileBinding.textInputEditTextEmail.getText().toString().trim();
                    userViewModel.setUserEmail(newEmail);
                }
            }
        }
    }

    private void onChangeBio() {
        if(fragmentEditProfileBinding.textInputEditTextBio.getText() != null) {
            if (!fragmentEditProfileBinding.textInputEditTextBio.getText().toString().trim().isEmpty()) {
                if (fragmentEditProfileBinding.textInputEditTextBio.getText().toString().trim().length() > 100) {
                    fragmentEditProfileBinding.textInputEditTextBio.setText("La bio non deve superare 100 caratteri");
                    fragmentEditProfileBinding.textInputEditTextBio.setVisibility(View.VISIBLE);
                }else if(fragmentEditProfileBinding.textInputEditTextBio.getText().toString().trim().equals(user.getBiography())) {
                    Toast.makeText(requireContext(), "Questa è gia la tua bio", Toast.LENGTH_SHORT).show();
                } else {
                    onSaveUser.setBiography(fragmentEditProfileBinding.textInputEditTextBio.getText().toString().trim());
                    userViewModel.setUserBiography(onSaveUser);
                }
            }
        }
    }


    private void onChangePassword() {
        if(fragmentEditProfileBinding.textInputEditTextPassword.getText() != null
                && fragmentEditProfileBinding.textInputEditTextPasswordConfirm.getText() != null) {
            if(fragmentEditProfileBinding.textInputEditTextPassword.getText().toString().trim().isEmpty()
                    && !fragmentEditProfileBinding.textInputEditTextPasswordConfirm.getText().toString().trim().isEmpty()) {
                fragmentEditProfileBinding.settingsPasswordErrorMessage.setText(R.string.field_not_filled_in);
                fragmentEditProfileBinding.settingsPasswordErrorMessage.setVisibility(View.VISIBLE);
            } else if(!fragmentEditProfileBinding.textInputEditTextPassword.getText().toString().trim().isEmpty()
                    && fragmentEditProfileBinding.textInputEditTextPasswordConfirm.getText().toString().trim().isEmpty()) {
                fragmentEditProfileBinding.settingsConfirmPasswordErrorMessage.setText(R.string.field_not_filled_in);
                fragmentEditProfileBinding.settingsConfirmPasswordErrorMessage.setVisibility(View.VISIBLE);
            } else if(!fragmentEditProfileBinding.textInputEditTextPassword.getText().toString().trim().isEmpty()
                    && !fragmentEditProfileBinding.textInputEditTextPasswordConfirm.getText().toString().trim().isEmpty()) {
                if(fragmentEditProfileBinding.textInputEditTextPassword.getText().toString().trim().length() < 6) {
                    fragmentEditProfileBinding.settingsPasswordErrorMessage.setText(R.string.error_password_length);
                    fragmentEditProfileBinding.settingsPasswordErrorMessage.setVisibility(View.VISIBLE);
                } else if(!fragmentEditProfileBinding.textInputEditTextPasswordConfirm.getText().toString().trim()
                        .equals(fragmentEditProfileBinding.textInputEditTextPassword.getText().toString().trim())) {
                    fragmentEditProfileBinding.settingsConfirmPasswordErrorMessage.setText(R.string.error_confirm_password_equal);
                    fragmentEditProfileBinding.settingsConfirmPasswordErrorMessage.setVisibility(View.VISIBLE);
                } else {
                    newPassword = fragmentEditProfileBinding.textInputEditTextPassword.getText().toString().trim();
                    userViewModel.changeUserPassword(newPassword);
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