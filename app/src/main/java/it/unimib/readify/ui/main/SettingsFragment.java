package it.unimib.readify.ui.main;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;

import org.apache.commons.validator.routines.EmailValidator;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding fragmentSettingsBinding;

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
        loadMenu();
        fragmentSettingsBinding.buttonConfirmEdit.setOnClickListener(v -> {

            String username = fragmentSettingsBinding.textInputLayoutUsername.getEditText().getText().toString();
            String email = fragmentSettingsBinding.textInputLayoutEmail.getEditText().getText().toString();
            String password = fragmentSettingsBinding.textInputLayoutPassword.getEditText().getText().toString();
            String passwordConfirm = fragmentSettingsBinding.textInputLayoutPasswordConfirm.getEditText().getText().toString();

            if (isUsernameOk(username) & isEmailOk(email) & isPasswordOk(password) & isPasswordConfirmOk(passwordConfirm)) {
                //collegarsi col db e modificare i dati
            } else {
                // ????
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

    private boolean isUsernameOk(String username) {
        if (username.isEmpty()) {
            fragmentSettingsBinding.textInputLayoutUsername.setError(getString(R.string.error_username));
            return false;
        } else {
            fragmentSettingsBinding.textInputLayoutUsername.setError(null);
            return true;
        }
    }
    private boolean isEmailOk(String email) {
        if (!EmailValidator.getInstance().isValid((email))) {
            fragmentSettingsBinding.textInputLayoutEmail.setError(getString(R.string.error_email));
            return false;
        } else {
            fragmentSettingsBinding.textInputLayoutEmail.setError(null);
            return true;
        }
    }

    private boolean isPasswordOk(String password) {
        if (password.isEmpty()) {
            fragmentSettingsBinding.textInputLayoutPassword.setError(getString(R.string.error_password));
            return false;
        } else {
            fragmentSettingsBinding.textInputLayoutPassword.setError(null);
            return true;
        }
    }

    private boolean isPasswordConfirmOk(String passwordConfirm) {
        if (passwordConfirm.isEmpty()) {
            fragmentSettingsBinding.textInputLayoutPasswordConfirm.setError(getString(R.string.error_password));
            return false;
        } else {
            fragmentSettingsBinding.textInputLayoutPasswordConfirm.setError(null);
            return true;
        }
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
