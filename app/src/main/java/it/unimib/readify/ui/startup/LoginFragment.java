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

    private static final boolean USE_NAVIGATION_COMPONENT = true;

    private static final String TAG = LoginFragment.class.getSimpleName();

    private DataEncryptionUtil dataEncryptionUtil;
    private FragmentLoginBinding fragmentLoginBinding;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher;
    private ActivityResultContracts.StartIntentSenderForResult startIntentSenderForResult;

    private UserViewModel userViewModel;


    public LoginFragment(){
        //required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        dataEncryptionUtil = new DataEncryptionUtil(requireActivity().getApplication());
        oneTapClient = Identity.getSignInClient(requireActivity());

        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        // Nonostante dia errore dovrebbe compilare ugualmente
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        startIntentSenderForResult = new ActivityResultContracts.StartIntentSenderForResult();

        activityResultLauncher = registerForActivityResult(startIntentSenderForResult, activityResult -> {
            if (activityResult.getResultCode() == Activity.RESULT_OK) {
                Log.d(TAG, "result.getResultCode() == Activity.RESULT_OK");
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(activityResult.getData());
                    String idToken = credential.getGoogleIdToken();
                    if (idToken !=  null) {
                        // Got an ID token from Google. Use it to authenticate with Firebase.
                        userViewModel.getGoogleUserMutableLiveData(idToken).observe(getViewLifecycleOwner(), authenticationResult -> {
                            if (authenticationResult.isSuccess()) {
                                User user = ((Result.UserSuccess) authenticationResult).getData();
                                saveLoginData(user.getEmail(), null, user.getIdToken());
                                userViewModel.setAuthenticationError(false);
                                retrieveUserInformationAndStartActivity(user, R.id.action_loginFragment_to_homeActivity);
                            } else {
                                userViewModel.setAuthenticationError(true);
                                //progressIndicator.setVisibility(View.GONE);
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        getErrorMessage(((Result.Error) authenticationResult).getMessage()),
                                        Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (ApiException e) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            requireActivity().getString(R.string.unexpected_error),
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater,container,false);
        return fragmentLoginBinding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (userViewModel.getLoggedUser() != null) {
            SharedPreferencesUtil sharedPreferencesUtil =
                    new SharedPreferencesUtil(requireActivity().getApplication());
//            if (sharedPreferencesUtil.readStringData(SHARED_PREFERENCES_FILE_NAME,
//                    SHARED_PREFERENCES_COUNTRY_OF_INTEREST) != null &&
//                    sharedPreferencesUtil.readStringSetData(SHARED_PREFERENCES_FILE_NAME,
//                            SHARED_PREFERENCES_TOPICS_OF_INTEREST) != null) {
//
//                startActivityBasedOnCondition(HomeActivity.class,
//                        R.id.action_loginFragment_to_homeActivity);
//            } else {
//                startActivityBasedOnCondition(NewsPreferencesActivity.class,
//                        R.id.navigate_to_newsPreferencesActivity);
//            }
            startActivityBasedOnCondition(HomeActivity.class,
                        R.id.action_loginFragment_to_homeActivity);
        }

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

        fragmentLoginBinding.buttonLogin.setOnClickListener(v -> {

            String email = fragmentLoginBinding.textInputLayoutEmail.getEditText().getText().toString();
            String password = fragmentLoginBinding.textInputLayoutPassword.getEditText().getText().toString();


            if (isEmailOk(email) & isPasswordOk(password)) {
                if (!userViewModel.isAuthenticationError()) {
                    //progressIndicator.setVisibility(View.VISIBLE);
                    userViewModel.getUserMutableLiveData(email, password, true).observe(
                            getViewLifecycleOwner(), result -> {
                                if (result.isSuccess()) {
                                    User user = ((Result.UserSuccess) result).getData();
                                    saveLoginData(email, password, user.getIdToken());
                                    userViewModel.setAuthenticationError(false);
                                    //todo quaprima andava alla selezione delle preferenze
                                    retrieveUserInformationAndStartActivity(user, R.id.action_loginFragment_to_homeActivity);
                                } else {
                                    userViewModel.setAuthenticationError(true);
                                    //progressIndicator.setVisibility(View.GONE);
                                    Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                            getErrorMessage(((Result.Error) result).getMessage()),
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    userViewModel.getUser(email, password, true);
                }
                //navigateToHomeActivity();
            } else {
                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                        R.string.check_login_data_message, Snackbar.LENGTH_SHORT).show();
            }
        });

        fragmentLoginBinding.buttonGoogleLogin.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        Log.d(TAG, "onSuccess from oneTapClient.beginSignIn(BeginSignInRequest)");
                        IntentSenderRequest intentSenderRequest =
                                new IntentSenderRequest.Builder(result.getPendingIntent()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Log.d(TAG, e.getLocalizedMessage());
                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                requireActivity().getString(R.string.error_no_google_account_found_message),
                                Snackbar.LENGTH_SHORT).show();
                    }
                }));


        fragmentLoginBinding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRegisterFragment();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        userViewModel.setAuthenticationError(false);
    }




    private void navigateToHomeActivity() {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_homeActivity);
        } else {
            Intent intent = new Intent(requireContext(), HomeActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }}


    private void navigateToRegisterFragment() {
        if (USE_NAVIGATION_COMPONENT) {
            Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_registerFragment);
        } else {
            Intent intent = new Intent(requireContext(), HomeActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }}

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
        try {
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS, email);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD, password);
            dataEncryptionUtil.writeSecretDataWithEncryptedSharedPreferences(
                    ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ID_TOKEN, idToken);

            if (password != null) {
                dataEncryptionUtil.writeSecreteDataOnFile(ENCRYPTED_DATA_FILE_NAME,
                        email.concat(":").concat(password));
            }

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }



    private void retrieveUserInformationAndStartActivity(User user, int destination) {
        //progressIndicator.setVisibility(View.VISIBLE);
        /*
        userViewModel.getUserFavoriteNewsMutableLiveData(user.getIdToken()).observe(
                getViewLifecycleOwner(), userFavoriteNewsRetrievalResult -> {
                    progressIndicator.setVisibility(View.GONE);
                    startActivityBasedOnCondition(NewsPreferencesActivity.class, destination);
                }
        );
        */

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



    private void startActivityBasedOnCondition(Class<?> destinationActivity, int destination) {
        Navigation.findNavController(requireView()).navigate(destination);
        requireActivity().finish();
    }


}