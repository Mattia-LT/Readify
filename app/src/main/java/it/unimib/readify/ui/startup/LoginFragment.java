package it.unimib.readify.ui.startup;

import static it.unimib.readify.util.Constants.EMAIL_ADDRESS;
import static it.unimib.readify.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.LOGIN_ID_TOKEN;
import static it.unimib.readify.util.Constants.PASSWORD;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;

import org.apache.commons.validator.routines.EmailValidator;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.readify.R;
import it.unimib.readify.databinding.FragmentLoginBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;

public class LoginFragment extends Fragment {
    private final String TAG = LoginFragment.class.getSimpleName();

    private FragmentLoginBinding fragmentLoginBinding;
    private UserViewModel userViewModel;
    private Observer<Result> loggedUserObserver;
    private DataEncryptionUtil dataEncryptionUtil;
    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> handleSignInResult(result.getData())
    );
    private SignInClient signInClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater,container,false);
        return fragmentLoginBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        userViewModel.setUIRunning(false);

        if(userViewModel.isContinueRegistrationFirstLoading()){
            try {
                String savedEmail = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
                String savedPassword = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
                String savedIdToken = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, LOGIN_ID_TOKEN);
                if(savedEmail != null){
                    loggedUserObserver = result -> {
                        if(userViewModel.isUIRunning()) {
                            if(result.isSuccess()) {
                                User user = ((Result.UserSuccess) result).getData();
                                if(user.getUsername() == null && user.getIdToken() != null){
                                    userViewModel.setContinueRegistrationFirstLoading(false);
                                    navigateToContinueRegistrationFragment();
                                    userViewModel.setUIRunning(false);
                                }
                            } else {
                                String errorMessage = ((Result.Error) result).getMessage();
                                Log.e(TAG, "Error: Logged user fetch wasn't successful -> " + errorMessage);
                            }
                        }
                    };

                    if(savedPassword != null){
                        //Normal login
                        userViewModel.setUserMutableLiveData(savedEmail, savedPassword, true);
                        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
                    } else {
                        //Google login
                        userViewModel.signInWithGoogle(savedIdToken);
                        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
                    }
                }
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        }

        //Set login data
        fragmentLoginBinding.buttonLogin.setOnClickListener(v -> {
            String email = fragmentLoginBinding.textInputEditTextEmail.getEditableText().toString().trim();
            String password = fragmentLoginBinding.textInputEditTextPassword.getEditableText().toString().trim();
            if(isEmailOk(email) && isPasswordOk(password)) {
                userViewModel.setUserMutableLiveData(email, password, true);

                loggedUserObserver = result -> {
                    if(userViewModel.isUIRunning()) {
                        if(result.isSuccess()) {
                            User user = ((Result.UserSuccess) result).getData();
                            if(user.getEmail().equalsIgnoreCase(email) && user.getIdToken() != null){
                                saveNormalLoginData(email, password);
                                if(user.getUsername() == null){
                                    userViewModel.setContinueRegistrationFirstLoading(false);
                                    navigateToContinueRegistrationFragment();
                                    userViewModel.setUIRunning(false);
                                } else {
                                    navigateToHomeActivity();
                                    requireActivity().finish();
                                    userViewModel.setUIRunning(false);
                                }
                            }
                        } else {
                            String errorMessage = ((Result.Error) result).getMessage();
                            Log.e(TAG, "Error: Logged user fetch wasn't successful -> " + errorMessage);
                        }
                    }
                };
                userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
            }
        });

        //Registration
        fragmentLoginBinding.buttonRegister.setOnClickListener( v -> navigateToRegisterFragment());
        //Google login
        fragmentLoginBinding.buttonGoogleLogin.setOnClickListener( v -> signInWithGoogle());
        //Configure Google Sign In
        signInClient = Identity.getSignInClient(requireContext());
    }

    private void signInWithGoogle() {
        GetSignInIntentRequest signInRequest = GetSignInIntentRequest.builder()
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        signInClient.getSignInIntent(signInRequest)
                .addOnSuccessListener(this::launchSignIn)
                .addOnFailureListener(e -> Log.e("getSignInIntent error", "Google Sign-in failed", e));
    }

    private void launchSignIn(PendingIntent pendingIntent) {
        try {
            IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(pendingIntent)
                    .build();
            signInLauncher.launch(intentSenderRequest);
        } catch (Exception e) {
            Log.e("launchSignIn error", "Couldn't start Sign In: " + e.getLocalizedMessage());
        }
    }

    private void handleSignInResult(Intent data) {
        try {
            //Google Sign in successful, authenticate with Firebase
            SignInCredential credential = signInClient.getSignInCredentialFromIntent(data);
            String idToken = credential.getGoogleIdToken();
            if(idToken != null){
                userViewModel.signInWithGoogle(idToken);
                loggedUserObserver = result -> {
                    if(userViewModel.isUIRunning()) {
                        if(result.isSuccess()) {
                            User user = ((Result.UserSuccess) result).getData();
                            if(user.getEmail().equalsIgnoreCase(credential.getId()) && user.getIdToken() != null){
                                saveGoogleLoginData(idToken);
                                if(user.getUsername() == null){
                                    navigateToContinueRegistrationFragment();
                                    userViewModel.setUIRunning(false);
                                } else {
                                    navigateToHomeActivity();
                                    requireActivity().finish();
                                    userViewModel.setUIRunning(false);
                                }
                            }
                        } else {
                            String errorMessage = ((Result.Error) result).getMessage();
                            Log.e(TAG, "Error: Logged user fetch wasn't successful -> " + errorMessage);
                        }
                    }
                };
                userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
            }
        } catch (ApiException e) {
            //Google Sign In failed
            Log.w("handleSignInResult error", "Google sign in failed", e);
        }
    }

    private void navigateToHomeActivity() {
        NavDirections action = LoginFragmentDirections.actionLoginFragmentToHomeActivity();
        Navigation.findNavController(requireView()).navigate(action);
    }

    private void navigateToRegisterFragment() {
        NavDirections action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment();
        Navigation.findNavController(requireView()).navigate(action);
    }

    private void navigateToContinueRegistrationFragment() {
        NavDirections action = LoginFragmentDirections.actionLoginFragmentToContinueRegistrationFragment();
        Navigation.findNavController(requireView()).navigate(action);
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

    private void initViewModels(){
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }

    private void saveNormalLoginData(String email, String password) {
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

    private void saveGoogleLoginData(String loginIdToken){
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, LOGIN_ID_TOKEN, loginIdToken);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentLoginBinding = null;
        userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
    }
}