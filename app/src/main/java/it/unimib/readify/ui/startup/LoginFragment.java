package it.unimib.readify.ui.startup;

import static it.unimib.readify.util.Constants.EMAIL_ADDRESS;
import static it.unimib.readify.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.PASSWORD;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.readify.R;
import it.unimib.readify.util.DataEncryptionUtil;

public class LoginFragment extends Fragment {

    private static final boolean USE_NAVIGATION_COMPONENT = true;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private static final String TAG = LoginFragment.class.getSimpleName();

    private DataEncryptionUtil dataEncryptionUtil;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textInputLayoutEmail = findViewById(R.id.text_input_layout_email);
        textInputLayoutPassword = findViewById(R.id.text_input_layout_password);
        final Button buttonLogin = findViewById(R.id.button_login);

        dataEncryptionUtil = new DataEncryptionUtil(this);

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

        buttonLogin.setOnClickListener(v -> {

            String email = textInputLayoutEmail.getEditText().getText().toString();
            String password = textInputLayoutPassword.getEditText().getText().toString();

            // Start login if email and password are ok
            if (isEmailOk(email) & isPasswordOk(password)) {
                Log.d(TAG, "Email and password are ok");
                saveLoginData(email, password);
            } else {
                Snackbar.make(findViewById(android.R.id.content),
                        R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isEmailOk(String email) {
        if (!EmailValidator.getInstance().isValid((email))) {
            textInputLayoutEmail.setError(getString(R.string.error_email));
            return false;
        } else {
            textInputLayoutEmail.setError(null);
            return true;
        }
    }

    private boolean isPasswordOk(String password) {
        if (password.isEmpty()) {
            textInputLayoutPassword.setError(getString(R.string.error_password));
            return false;
        } else {
            textInputLayoutPassword.setError(null);
            return true;
        }
    }

    private void saveLoginData(String email, String password) {
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);

            dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                    email.concat(":").concat(password));
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }



 /*
    public LoginFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Button buttonLogin = view.findViewById(R.id.button_login);

        buttonLogin.setOnClickListener( v -> {
            startActivityBasedOnCondition(WelcomeActivity.class,
                    R.id.action_loginFragment_to_homeActivity);
        });

        //TODO guarda codice delle slide del prof

    }


    private void startActivityBasedOnCondition(Class<?> destinationActivity, int destination) {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(requireView()).navigate(destination);
        } else {
            Intent intent = new Intent(requireContext(), destinationActivity);
            startActivity(intent);
        }
        requireActivity().finish();
    }*/







}