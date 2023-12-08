package it.unimib.readify.ui.startup;

import static it.unimib.readify.util.Constants.EMAIL_ADDRESS;
import static it.unimib.readify.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.PASSWORD;

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

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.readify.R;
import it.unimib.readify.ui.main.HomeActivity;
import it.unimib.readify.util.DataEncryptionUtil;


public class RegisterFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private static final boolean USE_NAVIGATION_COMPONENT = true;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutPasswordConfirm;
    private static final String TAG = LoginFragment.class.getSimpleName();

    private DataEncryptionUtil dataEncryptionUtil;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super .onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        Spinner spinner = rootView.findViewById(R.id.spinner_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        textInputUsername = rootView.findViewById(R.id.text_input_layout_username);
        textInputLayoutEmail = rootView.findViewById(R.id.text_input_layout_email);
        textInputLayoutPassword = rootView.findViewById(R.id.text_input_layout_password);
        textInputLayoutPasswordConfirm = rootView.findViewById(R.id.text_input_layout_password_confirm);
        final Button buttonRegConf = rootView.findViewById(R.id.button_confirm_registration);

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


        buttonRegConf.setOnClickListener(v -> {

            String username = textInputUsername.getEditText().getText().toString();
            String email = textInputLayoutEmail.getEditText().getText().toString();
            String password = textInputLayoutPassword.getEditText().getText().toString();
            String passwordConfirm = textInputLayoutPasswordConfirm.getEditText().getText().toString();


            if (isUsernameOk(username) &isEmailOk(email) & isPasswordOk(password) & isPasswordConfirmOk(passwordConfirm)) {
                //saveLoginData(email, password);
                Intent k = new Intent(this.getActivity(), HomeActivity.class);
                startActivity(k);
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private boolean isUsernameOk(String username) {
        if (username.isEmpty()) {
            textInputUsername.setError(getString(R.string.error_username));
            return false;
        } else {
            textInputUsername.setError(null);
            return true;
        }
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

    private boolean isPasswordConfirmOk(String passwordConfirm) {
        if (passwordConfirm.isEmpty()) {
            textInputLayoutPasswordConfirm.setError(getString(R.string.error_password));
            return false;
        } else {
            textInputLayoutPasswordConfirm.setError(null);
            return true;
        }
    }
}
