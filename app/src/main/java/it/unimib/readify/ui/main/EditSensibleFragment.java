package it.unimib.readify.ui.main;

import static java.text.DateFormat.getTimeInstance;

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

import org.apache.commons.validator.routines.EmailValidator;

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

    private FragmentEditSensibleBinding fragmentEditSensibleBinding;
    private UserViewModel userViewModel;
    private Observer<Boolean> emailErrorObserver;
    private Observer<Boolean> passwordErrorObserver;
    private Observer<Result> userObserver;
    private Observer<Long> lastAuthenticationTimestampObserver;
    private User user;
    private String newPassword;
    private Long lastAuthenticationTimestamp;
    private DataEncryptionUtil dataEncryptionUtil;

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
            fragmentEditSensibleBinding.editSensibleErrorEmail.setText("");
            fragmentEditSensibleBinding.editSensibleErrorEmail.setVisibility(View.GONE);
            fragmentEditSensibleBinding.editSensibleErrorPassword.setText("");
            fragmentEditSensibleBinding.editSensibleErrorPassword.setVisibility(View.GONE);
            fragmentEditSensibleBinding.editSensibleErrorConfirmPassword.setText("");
            fragmentEditSensibleBinding.editSensibleErrorConfirmPassword.setVisibility(View.GONE);

            if(fragmentEditSensibleBinding.editSensibleInputEmail.getText() != null
                    && fragmentEditSensibleBinding.editSensibleInputPassword.getText() != null
                    && fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.getText() != null) {
                if(!fragmentEditSensibleBinding.editSensibleInputEmail.getText().toString().trim().isEmpty()
                        || !fragmentEditSensibleBinding.editSensibleInputPassword.getText().toString().trim().isEmpty()
                        || !fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.getText().toString().trim().isEmpty()) {
                    if(isReAuthenticationRequired()) {
                        showAuthenticationDialog();
                    } else {
                        Toast.makeText(requireContext(), "OK", Toast.LENGTH_SHORT).show();
                        onChangeEmail();
                        onChangePassword();
                    }
                } else {
                    Toast.makeText(requireContext(), "Fill in at least one field", Toast.LENGTH_SHORT).show();
                }
            } else {
                //todo add Snack bar action (reload app?)
                Snackbar.make(view, "System error", Snackbar.LENGTH_SHORT).show();
            }
        });

        fragmentEditSensibleBinding.buttonUndoChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentEditSensibleBinding.editSensibleInputEmail.setText("");
                fragmentEditSensibleBinding.editSensibleInputPassword.setText("");
                fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.setText("");
            }
        });
    }

    private void initViewModels() {
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }

    private void initObservers() {
        userObserver = result -> {
            if(result.isSuccess()) {
                user = ((Result.UserSuccess)result).getData();
            }
        };

        emailErrorObserver = result -> {
            if(result) {
                Toast.makeText(requireContext(), "Email updated", Toast.LENGTH_SHORT).show();
                fragmentEditSensibleBinding.editSensibleInputEmail.setText("");
            } else {
                Toast.makeText(requireContext(), "failure", Toast.LENGTH_SHORT).show();
            }
        };

        passwordErrorObserver = result -> {
            if(result != null){
                if(result) {
                    String password = Objects.requireNonNull(fragmentEditSensibleBinding.editSensibleInputPassword.getText()).toString();
                    if(!password.trim().isEmpty()){
                        try {
                            String email = user.getEmail();
                            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);

                            dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                                    email.concat(":").concat(password));

                            Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show();
                            fragmentEditSensibleBinding.editSensibleInputPassword.setText("");
                            fragmentEditSensibleBinding.editSensibleInputPasswordConfirm.setText("");
                            userViewModel.resetPasswordErrorResult();
                        } catch (GeneralSecurityException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "System: password error", Toast.LENGTH_SHORT).show();
                    userViewModel.resetPasswordErrorResult();
                }
            }
        };

        lastAuthenticationTimestampObserver = result -> {
            if(result != null) {
                lastAuthenticationTimestamp = result;
                Log.d("editSensibleFragment last timestamp", getTimeInstance().format(lastAuthenticationTimestamp));
            }
        };

        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), userObserver);
        userViewModel.getSourceEmailError().observe(getViewLifecycleOwner(), emailErrorObserver);
        userViewModel.getSourcePasswordError().observe(getViewLifecycleOwner(), passwordErrorObserver);
        userViewModel.getLastAuthenticationTimestamp().observe(getViewLifecycleOwner(), lastAuthenticationTimestampObserver);
    }

    private void onChangeEmail() {
        if(fragmentEditSensibleBinding.editSensibleInputEmail.getText() != null) {
            String newEmail = fragmentEditSensibleBinding.editSensibleInputEmail.getText().toString().trim();
            if (!newEmail.isEmpty()) {
                if(!EmailValidator.getInstance().isValid((newEmail))) {
                    fragmentEditSensibleBinding.editSensibleErrorEmail.setText(R.string.email_invalid_input);
                    fragmentEditSensibleBinding.editSensibleErrorEmail.setVisibility(View.VISIBLE);
                } else if(newEmail.equals(user.getEmail())) {
                    Toast.makeText(requireContext(), "This is already your email", Toast.LENGTH_SHORT).show();
                } else {
                    userViewModel.setUserEmail(newEmail);
                }
            }
        }
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
                    newPassword = password;
                    userViewModel.changeUserPassword(newPassword);
                }
            }
        }
    }

    private boolean isReAuthenticationRequired() {
        boolean isReAuthenticationRequired = false;
        long currentTimestamp = System.currentTimeMillis();
        Log.d("currentTimestamp", getTimeInstance().format(currentTimestamp));
        Log.d("lastAuthenticationTimestamp", getTimeInstance().format(lastAuthenticationTimestamp));
        //checks if 20 seconds have passed since last authentication
        if(lastAuthenticationTimestamp + 20000 < currentTimestamp)
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
        userViewModel.getUserMediatorLiveData().removeObserver(userObserver);
        userViewModel.getSourceEmailError().removeObserver(emailErrorObserver);
        userViewModel.getSourcePasswordError().removeObserver(passwordErrorObserver);
    }
}
