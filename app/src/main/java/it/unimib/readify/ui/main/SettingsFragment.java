package it.unimib.readify.ui.main;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

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
            if (isUsernameOk(view) & isEmailOk() & isPasswordOk() & isPasswordConfirmOk()) {
                //todo add loading screen
                Log.d("settings if", "okay");
                testDatabaseViewModel.updateUserData(onSaveUser, new TestDatabaseRepository.UpdateUserDataCallback() {
                    @Override
                    public void onUsernameAvailable(String result) {
                        switch (result) {
                            case "available":
                                Snackbar.make(view, "Username updated", Snackbar.LENGTH_SHORT).show();
                                break;
                            case "notAvailable":
                                fragmentSettingsBinding.settingsUsernameErrorMessage.setText(R.string.username_already_taken);
                                fragmentSettingsBinding.settingsUsernameErrorMessage.setVisibility(View.VISIBLE);
                                break;
                            case "identical":
                                Snackbar.make(view, "This is already your username", Snackbar.LENGTH_SHORT).show();
                                break;
                            case "error":
                                Snackbar.make(view, "System error", Snackbar.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
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

    private boolean isUsernameOk(View view) {
        if(fragmentSettingsBinding.textInputEditTextUsername.getText() != null) {
            if (!fragmentSettingsBinding.textInputEditTextUsername.getText().toString().isEmpty()) {
                if (fragmentSettingsBinding.textInputEditTextUsername.getText().toString().length() > 20) {
                    fragmentSettingsBinding.settingsUsernameErrorMessage.setText(R.string.error_username_length);
                    fragmentSettingsBinding.settingsUsernameErrorMessage.setVisibility(View.VISIBLE);
                    return false;
                }
                else if (fragmentSettingsBinding.textInputEditTextUsername.getText().toString().contains("/")
                            || fragmentSettingsBinding.textInputEditTextUsername.getText().toString().contains("@")) {
                    fragmentSettingsBinding.settingsUsernameErrorMessage.setText(R.string.error_username_illegal_symbols);
                    fragmentSettingsBinding.settingsUsernameErrorMessage.setVisibility(View.VISIBLE);
                    return false;
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
        Snackbar.make(view, "System error: Username null", Snackbar.LENGTH_SHORT).show();
        return false;
    }

    private boolean isEmailOk() {
        return  true;
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