package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.PASSWORD;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FragmentEditSensibleBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.viewmodel.CustomViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;

public class EditSensibleFragment extends Fragment {
    private final String TAG = EditSensibleFragment.class.getSimpleName();

    private FragmentEditSensibleBinding fragmentEditSensibleBinding;
    private DataEncryptionUtil dataEncryptionUtil;
    private UserViewModel userViewModel;
    private Observer<Boolean> passwordErrorObserver;
    private Observer<Result> loggedUserObserver;
    private Observer<Long> lastAuthenticationTimestampObserver;
    private User user;
    private Long lastAuthenticationTimestamp;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentEditSensibleBinding = FragmentEditSensibleBinding.inflate(inflater,container,false);
        return fragmentEditSensibleBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());

        initViewModels();
        initObservers();

        fragmentEditSensibleBinding.buttonConfirmEdit.setOnClickListener(v -> {
            fragmentEditSensibleBinding.editSensibleErrorPassword.setText("");
            fragmentEditSensibleBinding.editSensibleErrorPassword.setVisibility(View.GONE);
            fragmentEditSensibleBinding.editSensibleErrorConfirmPassword.setText("");
            fragmentEditSensibleBinding.editSensibleErrorConfirmPassword.setVisibility(View.GONE);

            if(fragmentEditSensibleBinding.editSensibleInputPassword.getText() != null
                    && fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.getText() != null) {
                if(!fragmentEditSensibleBinding.editSensibleInputPassword.getText().toString().trim().isEmpty()
                        || !fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.getText().toString().trim().isEmpty()) {
                    if(isReAuthenticationRequired()) {
                        showAuthenticationDialog();
                    } else {
                        onChangePassword();
                    }
                } else {
                    Toast.makeText(requireContext(), R.string.fields_not_filled_in, Toast.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(view, R.string.generic_error, Snackbar.LENGTH_SHORT).show();
            }
        });

        fragmentEditSensibleBinding.buttonUndoChanges.setOnClickListener(v -> {
            fragmentEditSensibleBinding.editSensibleInputPassword.setText("");
            fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.setText("");
        });
    }

    private void initViewModels() {
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }

    private void initObservers() {
        loggedUserObserver = result -> {
            if(result.isSuccess()) {
                user = ((Result.UserSuccess)result).getData();
            } else {
                String errorMessage = ((Result.Error) result).getMessage();
                Log.e(TAG, "Error: Logged user fetch wasn't successful -> " + errorMessage);
            }
        };

        passwordErrorObserver = result -> {
            if(result != null){
                if(result) {
                    String password = Objects.requireNonNull(fragmentEditSensibleBinding.editSensibleInputPassword.getText()).toString().trim();
                    if(!password.trim().isEmpty()){
                        try {
                            String email = user.getEmail();
                            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);

                            dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                                    email.concat(":").concat(password));

                            Toast.makeText(requireContext(), R.string.updated_password, Toast.LENGTH_SHORT).show();
                            fragmentEditSensibleBinding.editSensibleInputPassword.setText("");
                            fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.setText("");
                            userViewModel.resetPasswordErrorResult();
                        } catch (GeneralSecurityException | IOException e) {
                            Log.e(TAG, String.valueOf(e.getLocalizedMessage()));
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), R.string.generic_error_password, Toast.LENGTH_SHORT).show();
                    userViewModel.resetPasswordErrorResult();
                }
            }
        };

        lastAuthenticationTimestampObserver = result -> {
            if(result != null) {
                lastAuthenticationTimestamp = result;
            }
        };

        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
        userViewModel.getSourcePasswordError().observe(getViewLifecycleOwner(), passwordErrorObserver);
        userViewModel.getLastAuthenticationTimestamp().observe(getViewLifecycleOwner(), lastAuthenticationTimestampObserver);
    }

    private void onChangePassword() {
        if(fragmentEditSensibleBinding.editSensibleInputPassword.getText() != null
                && fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.getText() != null) {
            String password = fragmentEditSensibleBinding.editSensibleInputPassword.getText().toString().trim();
            String confirmPassword = fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.getText().toString().trim();
            TextView errorPassword = fragmentEditSensibleBinding.editSensibleErrorPassword;
            TextView errorConfirmPassword = fragmentEditSensibleBinding.editSensibleErrorConfirmPassword;
            if(password.isEmpty() && !confirmPassword.isEmpty()) {
                errorPassword.setText(R.string.field_not_filled_in);
                errorPassword.setVisibility(View.VISIBLE);
            } else if(!password.isEmpty() && confirmPassword.isEmpty()) {
                errorConfirmPassword.setText(R.string.field_not_filled_in);
                errorConfirmPassword.setVisibility(View.VISIBLE);
            } else if(!password.isEmpty()) {
                if(password.length() < 6) {
                    errorPassword.setText(R.string.error_password_length);
                    errorPassword.setVisibility(View.VISIBLE);
                } else if(!confirmPassword.equals(password)) {
                    errorConfirmPassword.setText(R.string.error_confirm_password_equal);
                    errorConfirmPassword.setVisibility(View.VISIBLE);
                } else {
                    userViewModel.changeUserPassword(password);
                }
            }
        }
    }

    private boolean isReAuthenticationRequired() {
        boolean isReAuthenticationRequired = false;
        long currentTimestamp = System.currentTimeMillis();
        //checks if 4 minutes have passed since last authentication
        if(lastAuthenticationTimestamp + 240000 < currentTimestamp)
            isReAuthenticationRequired = true;
        return  isReAuthenticationRequired;
    }

    private void showAuthenticationDialog() {
        AuthenticationDialogFragment dialog = new AuthenticationDialogFragment();
        dialog.show(requireActivity().getSupportFragmentManager(), "AuthenticationDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
        userViewModel.getSourcePasswordError().removeObserver(passwordErrorObserver);
        userViewModel.getLastAuthenticationTimestamp().removeObserver(lastAuthenticationTimestampObserver);
    }
}