package it.unimib.readify.ui.startup;

import static it.unimib.readify.util.Constants.EMAIL_ADDRESS;
import static it.unimib.readify.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.ID_TOKEN;
import static it.unimib.readify.util.Constants.PASSWORD;
import static it.unimib.readify.util.Constants.USER_COLLISION_ERROR;
import static it.unimib.readify.util.Constants.WEAK_PASSWORD_ERROR;

import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.readify.R;

import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.databinding.FragmentRegisterBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.util.ServiceLocator;
import it.unimib.readify.viewmodel.UserViewModel;


public class RegisterFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    private UserViewModel userViewModel;
    private IUserRepository userRepository;
    private FragmentRegisterBinding fragmentRegisterBinding;

    public RegisterFragment() {}

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new it.unimib.readify.viewmodel.UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater,container,false);
        return fragmentRegisterBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.requireActivity(),
                    R.array.gender, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fragmentRegisterBinding.spinnerGender.setAdapter(adapter);
            fragmentRegisterBinding.spinnerGender.setOnItemSelectedListener(this);

            fragmentRegisterBinding.buttonConfirmRegistration.setOnClickListener(v -> {

                //String username = fragmentRegisterBinding.textInputLayoutUsername.getEditText().getText().toString();
                //String email = fragmentRegisterBinding.textInputLayoutEmail.getEditText().getText().toString();
                //String password = fragmentRegisterBinding.textInputLayoutPassword.getEditText().getText().toString();
                //String passwordConfirm = fragmentRegisterBinding.textInputLayoutPasswordConfirm.getEditText().getText().toString();
                //String gender = fragmentRegisterBinding.spinnerGender.getSelectedItem().toString();

                String username = "aww";
                String email = "prova@gmail.com";
                String password = "password";
                String passwordConfirm = "password";
                String gender = "F";

                if (isUsernameOk(username) & isEmailOk(email) & isPasswordOk(password) &
                        isPasswordConfirmOk(passwordConfirm) &
                        (fragmentRegisterBinding.spinnerGender.getSelectedItemPosition() != 0)) {
                            userViewModel.createUser(email, password, username, gender);
                            //se la registrazione non va a buon fine, userViewModel.getLoggedUser() sarà uguale ad un Result.Error
                            if(userViewModel.getLoggedUser() != null) {
                                Snackbar.make(view, "Utente già esistente", Snackbar.LENGTH_SHORT).show();
                            } else {
                                navigateToLoginFragment();
                            }
                } else {
                    Log.d("registration error", "registration error");
                }
            });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ((TextView) view).setTextColor(Color.WHITE);
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

    private void navigateToLoginFragment() {
        Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_loginFragment);
    }
}
