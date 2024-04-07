package it.unimib.readify.ui.main;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.validator.routines.EmailValidator;

import it.unimib.readify.R;
import it.unimib.readify.data.repository.user.TestDatabaseRepository;
import it.unimib.readify.databinding.FragmentSettingsBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding fragmentSettingsBinding;
    private TestDatabaseViewModel testDatabaseViewModel;
    private User user;
    private User onSaveUser;
    private Observer<Result> userObserver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater,container,false);
        return fragmentSettingsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);

        userObserver = result -> {
            if(result.isSuccess()) {
                user = ((Result.UserSuccess)result).getData();
                onSaveUser = new User(user);
            } else {
                //todo navigate to login(?)
                Snackbar.make(view, ((Result.Error)result).getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        };
        testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), userObserver);

        loadMenu();

        fragmentSettingsBinding.buttonConfirmEdit.setOnClickListener(v -> {
            if(fragmentSettingsBinding.textInputEditTextUsername.getText() != null
                    && fragmentSettingsBinding.textInputEditTextEmail.getText() != null
                    && fragmentSettingsBinding.textInputEditTextPassword.getText() != null
                    && fragmentSettingsBinding.textInputEditTextPasswordConfirm.getText() != null) {
                if(!fragmentSettingsBinding.textInputEditTextUsername.getText().toString().isEmpty()
                        || !fragmentSettingsBinding.textInputEditTextEmail.getText().toString().isEmpty()
                        || !fragmentSettingsBinding.textInputEditTextPassword.getText().toString().isEmpty()
                        || !fragmentSettingsBinding.textInputEditTextPasswordConfirm.getText().toString().isEmpty()) {
                    if (isUsernameOk() & isEmailOk() & isPasswordOk() & isPasswordConfirmOk()) {
                        //todo add loading screen
                        //todo deselect accepted field
                        //todo field value as placeholder?
                        testDatabaseViewModel.updateUserData(onSaveUser, new TestDatabaseRepository.UpdateUserDataCallback() {
                            @Override
                            public void onUsernameAvailable(String result) {
                                switch (result) {
                                    case "available":
                                        Toast.makeText(requireContext(), "Username updated", Toast.LENGTH_SHORT).show();
                                        fragmentSettingsBinding.textInputEditTextUsername.setText("");
                                        break;
                                    case "notAvailable":
                                        fragmentSettingsBinding.settingsUsernameErrorMessage.setText(R.string.username_already_taken);
                                        fragmentSettingsBinding.settingsUsernameErrorMessage.setVisibility(View.VISIBLE);
                                        break;
                                    case "error":
                                        //todo use snack bar instead (to implement an action)?
                                        Toast.makeText(requireContext(), "System: username error", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }

                            @Override
                            public void onEmailAvailable(String result) {
                                switch (result) {
                                    case "available":
                                        Toast.makeText(requireContext(), "Email updated", Toast.LENGTH_SHORT).show();
                                        fragmentSettingsBinding.textInputEditTextEmail.setText("");
                                        break;
                                    case "notAvailable":
                                        fragmentSettingsBinding.settingsEmailErrorMessage.setText(R.string.email_already_taken);
                                        fragmentSettingsBinding.settingsEmailErrorMessage.setVisibility(View.VISIBLE);
                                        break;
                                    case "identical":
                                        Toast.makeText(requireContext(), "This is already your email", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "error":
                                        //todo use snack bar instead (to implement an action)?
                                        Toast.makeText(requireContext(), "System: email error", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                    }
                } else {
                    Toast.makeText(requireContext(), "Fill in at least one field", Toast.LENGTH_SHORT).show();
                }
            } else {
                //todo add Snack bar action (reload app?)
                Snackbar.make(view, "System error", Snackbar.LENGTH_SHORT).show();
            }
        });

        fragmentSettingsBinding.buttonUndoChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSettingsBinding.textInputEditTextUsername.setText("");
                fragmentSettingsBinding.textInputEditTextEmail.setText("");
                fragmentSettingsBinding.textInputEditTextPassword.setText("");
                fragmentSettingsBinding.textInputEditTextPasswordConfirm.setText("");
            }
        });
    }

    private boolean isUsernameOk() {
        if(fragmentSettingsBinding.textInputEditTextUsername.getText() != null) {
            if (!fragmentSettingsBinding.textInputEditTextUsername.getText().toString().isEmpty()) {
                if (fragmentSettingsBinding.textInputEditTextUsername.getText().toString().trim().length() > 20) {
                    fragmentSettingsBinding.settingsUsernameErrorMessage.setText(R.string.error_username_length);
                    fragmentSettingsBinding.settingsUsernameErrorMessage.setVisibility(View.VISIBLE);
                    return false;
                }
                else if (fragmentSettingsBinding.textInputEditTextUsername.getText().toString().trim().contains("/")
                            || fragmentSettingsBinding.textInputEditTextUsername.getText().toString().trim().contains("@")) {
                    fragmentSettingsBinding.settingsUsernameErrorMessage.setText(R.string.error_username_illegal_symbols);
                    fragmentSettingsBinding.settingsUsernameErrorMessage.setVisibility(View.VISIBLE);
                    return false;
                } else if(fragmentSettingsBinding.textInputEditTextUsername.getText().toString().trim().equals(user.getUsername())) {
                    Toast.makeText(requireContext(), "This is already your username", Toast.LENGTH_SHORT).show();
                    fragmentSettingsBinding.settingsUsernameErrorMessage.setText("");
                    fragmentSettingsBinding.settingsUsernameErrorMessage.setVisibility(View.GONE);
                    return true;
                }
                onSaveUser.setUsername(fragmentSettingsBinding.textInputEditTextUsername.getText().toString().trim());
                fragmentSettingsBinding.settingsUsernameErrorMessage.setText("");
                fragmentSettingsBinding.settingsUsernameErrorMessage.setVisibility(View.GONE);
                return true;
            }
            fragmentSettingsBinding.settingsUsernameErrorMessage.setText("");
            fragmentSettingsBinding.settingsUsernameErrorMessage.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private boolean isEmailOk() {
        if(fragmentSettingsBinding.textInputEditTextEmail.getText() != null) {
            if (!fragmentSettingsBinding.textInputEditTextEmail.getText().toString().trim().isEmpty()) {
                if(!EmailValidator.getInstance().isValid((fragmentSettingsBinding.textInputEditTextEmail.getText().toString().trim()))) {
                    fragmentSettingsBinding.settingsEmailErrorMessage.setText(R.string.email_invalid_input);
                    fragmentSettingsBinding.settingsEmailErrorMessage.setVisibility(View.VISIBLE);
                    return false;
                } else if(fragmentSettingsBinding.textInputEditTextEmail.getText().toString().trim().equals(user.getEmail())) {
                    Toast.makeText(requireContext(), "This is already your email", Toast.LENGTH_SHORT).show();
                    fragmentSettingsBinding.settingsEmailErrorMessage.setText("");
                    fragmentSettingsBinding.settingsEmailErrorMessage.setVisibility(View.GONE);
                    return true;
                }
                onSaveUser.setEmail(fragmentSettingsBinding.textInputEditTextEmail.getText().toString().trim());
                fragmentSettingsBinding.settingsEmailErrorMessage.setText("");
                fragmentSettingsBinding.settingsEmailErrorMessage.setVisibility(View.GONE);
                return true;
            }
            fragmentSettingsBinding.settingsEmailErrorMessage.setText("");
            fragmentSettingsBinding.settingsEmailErrorMessage.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private boolean isPasswordOk() {
        return true;
    }

    private boolean isPasswordConfirmOk() {
        return true;
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