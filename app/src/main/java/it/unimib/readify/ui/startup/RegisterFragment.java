package it.unimib.readify.ui.startup;

import static it.unimib.readify.util.Constants.EMAIL_ADDRESS;
import static it.unimib.readify.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.PASSWORD;
import static it.unimib.readify.util.Constants.USER_COLLISION_ERROR;
import static it.unimib.readify.util.Constants.WEAK_PASSWORD_ERROR;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.readify.R;

import it.unimib.readify.databinding.FragmentRegisterBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class RegisterFragment extends Fragment{

    private FragmentRegisterBinding fragmentRegisterBinding;
    private TestDatabaseViewModel testDatabaseViewModel;
    private Observer<Result> observer;
    private DataEncryptionUtil dataEncryptionUtil;

    public RegisterFragment() {}

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);
        Log.d("registration fragment", "onCreate");
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater,container,false);
        Log.d("registration fragment", "onCreateView");
        return fragmentRegisterBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        Log.d("registration fragment", "onViewCreated");
        testDatabaseViewModel.setUIRunning(false);

        //registration set data
        fragmentRegisterBinding.buttonConfirmRegistration.setOnClickListener(v -> {
            //todo is the reference (textInputLayoutEmail) correct?
            String email = fragmentRegisterBinding.textInputLayoutEmail
                    .getEditText().getText().toString();
            /*
            String password = fragmentRegisterBinding.textInputLayoutPassword
                    .getEditText().getText().toString();
            String passwordConfirm = fragmentRegisterBinding.textInputLayoutPasswordConfirm
                    .getEditText().getText().toString();
             */

            String password = "password";
            String passwordConfirm = "password";

            if (isEmailOk(email) && isPasswordOk(password) && isPasswordConfirmOk(passwordConfirm)) {
                testDatabaseViewModel.setUserMutableLiveData(email, password, false);

                //registration action
                observer = result -> {
                    Log.d("registration fragment", "data changed");
                    if(testDatabaseViewModel.isUIRunning()) {
                        if(result != null) {
                            if(result.isSuccess()) {
                                User user = ((Result.UserSuccess) result).getData();
                                saveLoginData(email, password);
                                navigateToContinueRegistration();
                            } else {
                                Snackbar.make(view, ((Result.Error)result).getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                };
                testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), observer);


            } else {
                //todo managing specific behavior when an error occurs
                Snackbar.make(view, "error data insertion", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("registration fragment", "onStart");
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d("registration fragment", "onDestroyView");
        testDatabaseViewModel.getUserMediatorLiveData().removeObserver(observer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("registration fragment", "onDestroy");
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
        if (password.isEmpty()) {
            fragmentRegisterBinding.textInputLayoutPassword.setError(getString(R.string.error_password));
            return false;
        } else {
            fragmentRegisterBinding.textInputLayoutPassword.setError(null);
            return true;
        }
    }

    private boolean isPasswordConfirmOk(String passwordConfirm) {
        //todo it seems incomplete
        if (passwordConfirm.isEmpty()) {
            fragmentRegisterBinding.textInputLayoutPasswordConfirm.setError(getString(R.string.error_password));
            return false;
        } else {
            fragmentRegisterBinding.textInputLayoutPasswordConfirm.setError(null);
            return true;
        }
    }

    private String getErrorMessage(String message) {
        switch(message) {
            case WEAK_PASSWORD_ERROR:
                return requireActivity().getString(R.string.error_password);
            case USER_COLLISION_ERROR:
                return requireActivity().getString(R.string.placeholder_error_message);
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }

    private void navigateToContinueRegistration() {
        Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_continueRegistrationFragment);
    }

    private void saveLoginData(String email, String password) {
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);

            //normal login (not google)
            if (password != null) {
                dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                        email.concat(":").concat(password));
            }
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }
}