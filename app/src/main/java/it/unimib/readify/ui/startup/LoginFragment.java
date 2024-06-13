package it.unimib.readify.ui.startup;

import static it.unimib.readify.util.Constants.EMAIL_ADDRESS;
import static it.unimib.readify.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.LOGIN_ID_TOKEN;
import static it.unimib.readify.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.readify.util.Constants.INVALID_USER_ERROR;
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
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.snackbar.Snackbar;

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
    private FragmentLoginBinding fragmentLoginBinding;
    private UserViewModel userViewModel;
    private DataEncryptionUtil dataEncryptionUtil;
    private Observer<Result> loggedUserObserver;

    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> handleSignInResult(result.getData())
    );
    private SignInClient signInClient;
    public LoginFragment(){}

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater,container,false);
        Log.d("login fragment", "onCreateView");
        return fragmentLoginBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("login fragment", "onViewCreated");
        initViewModels();

        if(userViewModel.isContinueRegistrationFirstLoading()){
            try {
                String savedEmail = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
                String savedPassword = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
                String savedIdToken = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, LOGIN_ID_TOKEN);
                Log.d(String.valueOf(savedEmail), String.valueOf(savedEmail));
                Log.d(String.valueOf(savedIdToken), String.valueOf(savedIdToken));
                Log.d(String.valueOf(savedPassword), String.valueOf(savedPassword));
                if(savedEmail != null){
                    loggedUserObserver = result -> {
                        if(userViewModel.isUIRunning()) {
                            if(result.isSuccess()) {
                                User user = ((Result.UserSuccess) result).getData();
                                if(user.getUsername() == null){
                                    userViewModel.setContinueRegistrationFirstLoading(false);
                                    Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_continueRegistrationFragment);
                                    userViewModel.setUIRunning(false);
                                }
                            }
                        }
                    };

                    if(savedPassword != null){
                        //login normale
                        userViewModel.setUserMutableLiveData(savedEmail, savedPassword, true);
                        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
                    } else {
                        //login google
                        userViewModel.signInWithGoogle(savedIdToken);
                        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
                    }
                }
            } catch (GeneralSecurityException | IOException e) {
                throw new RuntimeException(e);
            }
        }



        /*
            Observer Class allows to observe a particular instance of LiveData,
             contained in the ViewModel, and invoke its body (interface) each time this instance changes
             its value. The Observer is going to catch an update when the value changes regarding
             both its reference and internal data.
            UI data visualization is one of its biggest usages.

            Anyway, this class can be tricky if its internal behavior is not known:
                1) Managing the correct instance of LiveData
                    Initializing the Observer with the correct LiveData instance
                     is the most important thing: once the LiveData instance is initialized
                     in the ViewModel, it shouldn't be changed.
                    For example, in the ViewModel we currently have this initialization:
                        copiedData = new MediatorLiveData<>();
                    This is the first and only time the variable @copiedData is initialized;
                     that is important for the reason that the Observer is going to observe only
                     the instance of the variable we pass to it.
                    It means that, each time we CREATE another instance of @copiedData
                     in the observed method, not only a) a new Observer IS GOING TO be created
                     and observe the newborn instance, causing some MESS in the memory,
                     but also b) we lose the capability to change and observe the correct
                     instance of LiveData.
                    In other words, it is greatly recommended a) to use postValue() to update the
                     value of LiveData when it is observed, avoiding the practice of
                     Object Referencing, and b) implementing simple and solid methods
                     (like the current ones)
                    However, everything can be done (professor version is working),
                     but in my opinion this current implementation is the easiest to understand,
                     build and use.
                2) Previous changes in other containers
                    First of all, a container is intended to be an Activity or a Fragment.
                    It can happen that a portion of the data (assuming the user data)
                     changes in a previous container:
                     for example, before logging in the HomeActivity, the user has to log in
                     in the LoginFragment (or in the RegistrationFragment).
                    Doing so, the data memorized in the ViewModel is UPDATED
                     (from null value to UserSuccess), implying that EVERY Observer
                     pointed to @copiedData (user data) is going to capture this update and
                     invoke its body, when its container is active.
                    A little clarification:
                     a) To capture an update, an Observer doesn't need to be initialized,
                        nor its container to be active;
                        Any Observer catches any update.
                     b) To invoke its body, an Observer needs to be initialized and active.
                        An observer is active from its initialization to its deletion OR
                         until its container is active.
                     c) To be initialized, an Observer needs its container to be active
                        and to run its initialization;
                     d) If the observed data changes MULTIPLE TIMES when the Observer isn't active,
                         it is going to catch only the last update, invoking its body
                         when it is initialized.
                        That seems to be because the Observer compares the last memorized value
                         of the LiveData with the current one: if they aren't identical, it detects
                         the update and invokes its body; if the Observer wasn't even initialized at the
                         time of the update, the Observer IS GOING TO KNOW the starter value of
                         the LiveData (somehow I guess).
                        That's smooth.
                3) Observer deletion
                    An Observer is going to catch an update until it is deleted,
                     even when its container has been destroyed; this may have a negative impact on the memory.
                    Remember to delete it when its working is unnecessary.
        */
        //login set data
        fragmentLoginBinding.buttonLogin.setOnClickListener(v -> {
            String email = fragmentLoginBinding.textInputEditTextEmail.getEditableText().toString();
            String password = fragmentLoginBinding.textInputEditTextPassword.getEditableText().toString();
            //String email = "prova@gmail.com";
            //String password = "password";
            if(isEmailOk(email) && isPasswordOk(password)) {
                userViewModel.setUserMutableLiveData(email, password, true);
                loggedUserObserver = result -> {
                    if(userViewModel.isUIRunning()) {
                        if(result.isSuccess()) {
                            User user = ((Result.UserSuccess) result).getData();
                            Log.e("USER SAVED", user.toString());
                            if(user.getEmail().equalsIgnoreCase(email)){
                                saveNormalLoginData(email, password);
                                if(user.getUsername() == null){
                                    userViewModel.setContinueRegistrationFirstLoading(false);
                                    Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_continueRegistrationFragment);
                                    userViewModel.setUIRunning(false);
                                } else {
                                    Log.d("NAVIGAZIONE", "APERTO DA ELSE DI BUTTON LISTENER");
                                    navigateToHomeActivity();
                                    requireActivity().finish();
                                    userViewModel.setUIRunning(false);
                                }
                            }
                        } else {
                            Snackbar.make(requireView(), ((Result.Error)result).getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                };

                userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);

            } else {
                //todo managing specific behavior when an error occurs
                Snackbar.make(view, "error data insertion", Snackbar.LENGTH_SHORT).show();
            }
        });

        //registration
        fragmentLoginBinding.buttonRegister.setOnClickListener( v -> navigateToRegisterFragment());

        //login with google

        fragmentLoginBinding.buttonGoogleLogin.setOnClickListener( v -> signInWithGoogle());


        // Configure Google Sign In
        signInClient = Identity.getSignInClient(requireContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("login fragment", "onStart");
        /*
            todo setUIRunning
            when MediatorLiveData is modified in other containers, and the user return to login,
             the instruction
                userViewModel.setUIRunning(false);
             is not going to be invoked again, causing (probably) the Observer to run when it shouldn't.
         */

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("login fragment", "onDestroyView");
        fragmentLoginBinding = null;
        /*
            (Read first Life Cycles, Backstack and Navigation explanation)
            Here I put the removeObserver() because
             onDestroyView() is a safe place to put it and the logic alternatives aren't.
            With the current implementation (initializing the Observer in onViewCreated() ),
             the only logic alternative to put removeObserver() is inside the Observer's body itself,
             right after the navigation action, but doing so, if the user comes back with
             the Back button or after the logout, the Observer is not going to work because
             it was deleted and never re-initialized
             (because onViewCreated is not going to be invoked anymore).
            It would have been different if we initialized the Observer in onStart():
             like this, method removeObserver() can be put in its body.
         */
        Log.e("ON DESTROY login", "TRIGGERED");
        userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
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
            // Google Sign In was successful, authenticate with Firebase
            SignInCredential credential = signInClient.getSignInCredentialFromIntent(data);
            String idToken = credential.getGoogleIdToken();
            Log.d("handleSignInResult", "firebaseAuthWithGoogle:" + credential.getId());
            if(idToken != null){
                userViewModel.signInWithGoogle(idToken);
                loggedUserObserver = result -> {
                    if(userViewModel.isUIRunning()) {
                        if(result.isSuccess()) {
                            User user = ((Result.UserSuccess) result).getData();
                            Log.e("user72kkkkk",user.toString());
                            Log.e("idtoken_problematico", idToken);
                            Log.e("idtoken_FORSE", credential.getId());
                            if(user.getEmail().equalsIgnoreCase(credential.getId())){
                                Log.e("user73kkkkk",user.toString());
                                saveGoogleLoginData(idToken);
                                if(user.getUsername() == null){
                                    Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_continueRegistrationFragment);
                                    userViewModel.setUIRunning(false);
                                } else {
                                    Log.d("NAVIGAZIONE", "APERTO DA ELSE DI GOOGLE BUTTON LISTENER");
                                    navigateToHomeActivity();
                                    requireActivity().finish();
                                    userViewModel.setUIRunning(false);
                                }
                            }
                        } else {
                            Snackbar.make(requireView(), ((Result.Error)result).getMessage(), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                };
                userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
            }


        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w("handleSignInResult error", "Google sign in failed", e);
        }
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

    private void initViewModels(){
        userViewModel = CustomViewModelFactory.getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);

        userViewModel.setUIRunning(false);
    }

    private void saveNormalLoginData(String email, String password) {
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

    private void saveGoogleLoginData(String loginIdToken){
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, LOGIN_ID_TOKEN, loginIdToken);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("ON DESTROY login", "TRIGGERED");
        userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
    }
}