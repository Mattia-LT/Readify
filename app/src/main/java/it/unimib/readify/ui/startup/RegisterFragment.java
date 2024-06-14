package it.unimib.readify.ui.startup;

import static it.unimib.readify.util.Constants.EMAIL_ADDRESS;
import static it.unimib.readify.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.PASSWORD;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import it.unimib.readify.R;

import it.unimib.readify.databinding.FragmentRegisterBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.viewmodel.CustomViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;

public class RegisterFragment extends Fragment{
    private final String TAG = RegisterFragment.class.getSimpleName();

    private FragmentRegisterBinding fragmentRegisterBinding;
    private UserViewModel userViewModel;
    private Observer<Result> loggedUserObserver;
    private DataEncryptionUtil dataEncryptionUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater,container,false);
        return fragmentRegisterBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        userViewModel.setUIRunning(false);

        //Set registration data
        fragmentRegisterBinding.buttonConfirmRegistration.setOnClickListener(v -> {
            String email = Objects.requireNonNull(fragmentRegisterBinding.textInputLayoutEmail
                    .getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(fragmentRegisterBinding.textInputLayoutPassword
                    .getEditText()).getText().toString().trim();
            String passwordConfirm = Objects.requireNonNull(fragmentRegisterBinding.textInputLayoutPasswordConfirm
                    .getEditText()).getText().toString().trim();

            if (isEmailOk(email) && isPasswordOk(password) && isPasswordConfirmOk(passwordConfirm)) {
                userViewModel.setUserMutableLiveData(email, password, false);
                //Registration
                loggedUserObserver = result -> {
                    if(userViewModel.isUIRunning()) {
                        if(result != null) {
                            if(result.isSuccess()) {
                                saveLoginData(email, password);
                                NavDirections action = RegisterFragmentDirections.actionRegisterFragmentToContinueRegistrationFragment();
                                Navigation.findNavController(requireView()).navigate(action);
                            } else {
                                String errorMessage = ((Result.Error) result).getMessage();
                                Log.e(TAG, "Error: Logged user fetch wasn't successful -> " + errorMessage);
                            }
                        }
                    }
                };
                userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
            }
        });

        fragmentRegisterBinding.buttonBackToLogin.setOnClickListener(v -> {
            NavDirections action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment();
            Navigation.findNavController(requireView()).navigate(action);
        });
    }

    public void initViewModels() {
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }

    private boolean isEmailOk(String email) {
        if (!EmailValidator.getInstance().isValid((email))) {
            fragmentRegisterBinding.textInputLayoutEmail.setError(getString(R.string.error_email));
            return false;
        } else {
            fragmentRegisterBinding.textInputLayoutEmail.setError(null);
            return true;
        }
    }

    private boolean isPasswordOk(String password) {
        //todo it is incomplete
        if (password.isEmpty()) {
            fragmentRegisterBinding.textInputLayoutPassword.setError(getString(R.string.error_password));
            return false;
        } else if(password.length() < 6){
            fragmentRegisterBinding.textInputLayoutPassword.setError(getString(R.string.error_password));
            return false;
        } else {
            fragmentRegisterBinding.textInputLayoutPassword.setError(null);
            return true;
        }
    }

    private boolean isPasswordConfirmOk(String passwordConfirm) {
        //todo it is incomplete
        if (passwordConfirm.isEmpty()) {
            fragmentRegisterBinding.textInputLayoutPasswordConfirm.setError(getString(R.string.error_password));
            return false;
        } else {
            fragmentRegisterBinding.textInputLayoutPasswordConfirm.setError(null);
            return true;
        }
    }

    private void saveLoginData(String email, String password) {
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);
            //Normal login
            if (password != null) {
                dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                        email.concat(":").concat(password));
            }
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, String.valueOf(e.getLocalizedMessage()));
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        fragmentRegisterBinding = null;
        userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
    }
}