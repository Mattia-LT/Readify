package it.unimib.readify.ui.startup;

import static it.unimib.readify.util.Constants.EMAIL_ADDRESS;
import static it.unimib.readify.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.ID_TOKEN;
import static it.unimib.readify.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.readify.util.Constants.INVALID_USER_ERROR;
import static it.unimib.readify.util.Constants.PASSWORD;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.readify.R;
import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.databinding.FragmentLoginBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.ui.main.HomeActivity;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.util.ServiceLocator;
import it.unimib.readify.util.SharedPreferencesUtil;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.UserViewModelFactory;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding fragmentLoginBinding;
    private UserViewModel userViewModel;
    private IUserRepository userRepository;


    public LoginFragment(){
        //required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater,container,false);
        return fragmentLoginBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //login
        fragmentLoginBinding.buttonLogin.setOnClickListener(v -> {
            //String email = fragmentLoginBinding.textInputEditTextEmail.getEditableText().toString();
            //String password = fragmentLoginBinding.textInputEditTextPassword.getEditableText().toString();
            if(isEmailOk("prova2@gmail.com") && isPasswordOk("password")) {
                //if(userViewModel.isAuthenticationError()) {
                    userViewModel.getUserMutableLiveData("prova2@gmail.com", "password", true)
                            .observe(getViewLifecycleOwner(), result -> {
                                if(result.isSuccess()) {
                                    User user = ((Result.UserSuccess)result).getData();
                                    navigateToHomeActivity();
                                }
                                else {
                                    navigateToHomeActivity();
                                    Log.d("sign in error", "sign in error");
                                }
                            });
                /*} else {
                    Log.d("isAuthenticationError", "isAuthenticationError");
                }*/
            }
        });

        //registration
        fragmentLoginBinding.buttonRegister.setOnClickListener( v -> {
            navigateToRegisterFragment();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }




    private void navigateToHomeActivity() {
        Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_homeActivity);
    }


    private void navigateToRegisterFragment() {
        Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_registerFragment);
    }

    private boolean isEmailOk(String email) {
        if (!EmailValidator.getInstance().isValid((email))) {
            fragmentLoginBinding.textInputLayoutEmail.setError(getString(R.string.error_email));
            return false;
        } else {
            fragmentLoginBinding.textInputLayoutEmail.setError(null);
            return true;
        }
    }

    private boolean isPasswordOk(String password) {
        if (password.isEmpty()) {
            fragmentLoginBinding.textInputLayoutPassword.setError(getString(R.string.error_password));
            return false;
        } else {
            fragmentLoginBinding.textInputLayoutPassword.setError(null);
            return true;
        }
    }

    private void saveLoginData(String email, String password, String idToken) {

    }



    private String getErrorMessage(String errorType) {
        switch (errorType) {
            case INVALID_CREDENTIALS_ERROR:
                return requireActivity().getString(R.string.error_login_password_message);
            case INVALID_USER_ERROR:
                return requireActivity().getString(R.string.error_login_user_message);
            default:
                return requireActivity().getString(R.string.unexpected_error);
        }
    }
}