package it.unimib.readify.ui.startup;

import static it.unimib.readify.util.Constants.EMAIL_ADDRESS;
import static it.unimib.readify.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.ID_TOKEN;
import static it.unimib.readify.util.Constants.PASSWORD;
import static it.unimib.readify.util.Constants.USER_COLLISION_ERROR;
import static it.unimib.readify.util.Constants.WEAK_PASSWORD_ERROR;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FragmentLoginBinding;
import it.unimib.readify.databinding.FragmentRegisterBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.ui.main.HomeActivity;
import it.unimib.readify.util.DataEncryptionUtil;


public class RegisterFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final boolean USE_NAVIGATION_COMPONENT = true;

    private static final String TAG = LoginFragment.class.getSimpleName();

    private DataEncryptionUtil dataEncryptionUtil;
    private UserViewModel userViewModel;
    private FragmentRegisterBinding fragmentRegisterBinding;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.setAuthenticationError(false);
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater,container,false);
        return fragmentRegisterBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.gender, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fragmentRegisterBinding.spinnerGender.setAdapter(adapter);
            fragmentRegisterBinding.spinnerGender.setOnItemSelectedListener(this);


            dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());

            try {
                Log.d(TAG, "Email address from encrypted SharedPref: " + dataEncryptionUtil.
                        readSecretDataWithEncryptedSharedPreferences(
                                ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS));
                Log.d(TAG, "Password from encrypted SharedPref: " + dataEncryptionUtil.
                        readSecretDataWithEncryptedSharedPreferences(
                                ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD));
                Log.d(TAG, "Login data from encrypted file: " + dataEncryptionUtil.
                        readSecretDataOnFile(ENCRYPTED_DATA_FILE_NAME));
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
            }


            fragmentRegisterBinding.buttonConfirmRegistration.setOnClickListener(v -> {

                String username = fragmentRegisterBinding.textInputLayoutUsername.getEditText().getText().toString();
                String email = fragmentRegisterBinding.textInputLayoutEmail.getEditText().getText().toString();
                String password = fragmentRegisterBinding.textInputLayoutPassword.getEditText().getText().toString();
                String passwordConfirm = fragmentRegisterBinding.textInputLayoutPasswordConfirm.getEditText().getText().toString();

                Log.d("username", username);
                Log.d("email", email);
                Log.d("password", password);
                Log.d("confirmpassword", passwordConfirm);

                if (isUsernameOk(username) & isEmailOk(email) & isPasswordOk(password) & isPasswordConfirmOk(passwordConfirm)) {
                    //fragmentRegisterBinding.progressBar.setVisibility(View.VISIBLE);
                    if (!userViewModel.isAuthenticationError()) {
                        userViewModel.getUserMutableLiveData(email, password, false).observe(
                                getViewLifecycleOwner(), result -> {
                                    if (result.isSuccess()) {
                                        User user = ((Result.UserSuccess) result).getData();
                                        saveLoginData(email, password, user.getIdToken());
                                        userViewModel.setAuthenticationError(false);
                                        Navigation.findNavController(view).navigate(
                                                R.id.action_registerFragment_to_homeActivity);
                                    } else {
                                        userViewModel.setAuthenticationError(true);
                                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                                getErrorMessage(((Result.Error) result).getMessage()),
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        userViewModel.getUser(email, password, false);
                    }




                } else {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
                }
            });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private boolean isUsernameOk(String username) {
        if (username.isEmpty()) {
            fragmentRegisterBinding.textInputLayoutUsername.setError(getString(R.string.error_username));
            return false;
        } else {
            fragmentRegisterBinding.textInputLayoutUsername.setError(null);
            return true;
        }
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
        if (passwordConfirm.isEmpty()) {
            fragmentRegisterBinding.textInputLayoutPasswordConfirm.setError(getString(R.string.error_password));
            return false;
        } else {
            fragmentRegisterBinding.textInputLayoutPasswordConfirm.setError(null);
            return true;
        }
    }


    private void saveLoginData(String email, String password, String idToken) {
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN, idToken);
            dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                    email.concat(":").concat(password));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
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
}
