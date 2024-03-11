package it.unimib.readify.ui.startup;

import static it.unimib.readify.util.Constants.INVALID_CREDENTIALS_ERROR;
import static it.unimib.readify.util.Constants.INVALID_USER_ERROR;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.tasks.Task;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.apache.commons.validator.routines.EmailValidator;

import it.unimib.readify.R;
import it.unimib.readify.data.repository.user.TestIDatabaseRepository;
import it.unimib.readify.databinding.FragmentLoginBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.util.TestServiceLocator;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;
import androidx.activity.result.ActivityResult;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding fragmentLoginBinding;

    private TestDatabaseViewModel testDatabaseViewModel;
    private Observer<Result> observer;

    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    handleSignInResult(result.getData());
                }
            }
    );
    private SignInClient signInClient;
    private FirebaseAuth mAuth;

    public LoginFragment(){}

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    /*
        Fragment's Life Cycles
        There are lots of life cycles; here I put them in order of execution:
         1)  onAttach()
                It is called when the Fragment is first attached to its host Activity.
                At this point, the Fragment has access to the Activity via the getActivity() method.
         2)  onCreate()
                It is called to do initial creation of a fragment
                 (when the fragment doesn't exist).
         3)  onCreateView()
                It is called to have the fragment instantiate its view.
         4)  onViewCreated()
                It is called when the fragment view is completely created.
                Now is the moment to initialize and use graphical and non-graphical components.
         5)  onStart()
                It is called when the user can see the Fragment.
                Now is the moment to start animations.
         6)  onResume()
                It is called when the Fragment becomes active and ready for interaction.
         7)  onPause()
                It is called when the Fragment is about to be paused or replaced by another Fragment.
                You can perform cleanup operations or save the user interface state here.
         8)  onStop()
                It is called when the Fragment is no longer visible to the user.
                You can perform visibility-related cleaning operations in this method.
         9)  onDestroyView()
                It is called when the fragment's view is destroyed.
         10) onDestroy()
                It is called when the fragment is going to be completely destroyed and removed
                 from Backstack; the fragment doesn't exist anymore.
         11) onDetach()
                It is called when the Fragment is removed from the host Activity.

        Backstack
        It is a data structure that keeps track of Activities or Fragments in an application
         as they are added to and removed from the stack.
        This stack is primarily used to handle backward navigation within the application.

            Behavior
            When an Activity or Fragment is started, it is usually added to the backstack.
            Every time another Activity or Fragment is started, the previous one is pushed
             onto the stack. In this way, the user can navigate backwards by pressing the Back
             button on the device, and the application will take the user back to the
             previous Activity or Fragment removed from the stack.
            When the user presses the Back button, the current task or fragment is removed
             from the top of the stack, and the first previous one starts from onStart() method;
             that's specific detail is important for deciding where to initialize and deleting
             the Observer.

            When you should remove a container from Backstack
            1) End of logical flow
                If a container represents the end of a logical flow within your application.
                This prevents the user from returning to a screen that makes no sense to repeat
                 or offers no further action.
            2) Save memory
                If a container takes up a lot of memory and you no longer need
                 to keep it on the stack.
            3) Privacy and security
                If a container contains sensitive information, it may need to be removed
                 from the stack when the user exits the application or switches to another
                 security-critical activity.
                This prevents an attacker from accessing sensitive information
                 by going back up the stack.
            4) Customizing the user experience
                It may be desirable to customize the user experience by removing specific containers
                 from the stack to improve the fluidity of navigation
                 or to adapt to specific user behaviors.

        Navigation component behavior
        When you use the navigation component in Android to navigate from a fragment to another activity,
         the current fragment is simply removed from the navigation transaction and replaced
         by the target activity.
        The Fragment is not automatically destroyed unless explicitly removed via other operations,
         such as removal from the Backstack.
        //todo verify that removal from backstack destroys the container

        Differences between Back button, Navigation Component and Menu
        Having a Start point and a Destination point, both points being a container:
         Back button destroys the Start point (removing it from the Backstack);
         Menu (which depends on Navigation Component) destroys the Start point (removing it from the Backstack);
         Navigation doesn't destroys the Start point;
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("login fragment", "onCreate");
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
        initRepositories();


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
        //login action
        observer = result -> {
            if(testDatabaseViewModel.isUIRunning()) {
                if(result.isSuccess()) {
                    navigateToHomeActivity();
                } else {
                    Snackbar.make(view, ((Result.Error)result).getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        };
        testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), observer);

        //login set data
        fragmentLoginBinding.buttonLogin.setOnClickListener(v -> {
            //String email = fragmentLoginBinding.textInputEditTextEmail.getEditableText().toString();
            //String password = fragmentLoginBinding.textInputEditTextPassword.getEditableText().toString();
            String email = "prova@gmail.com";
            String password = "password";
            if(isEmailOk(email) && isPasswordOk(password)) {
                testDatabaseViewModel.setUserMutableLiveData(email, password, true);
            } else {
                //todo managing specific behavior when an error occurs
                Snackbar.make(view, "error data insertion", Snackbar.LENGTH_SHORT).show();
            }
        });

        //registration
        fragmentLoginBinding.buttonRegister.setOnClickListener( v -> {
            navigateToRegisterFragment();
        });

        //login with google
        /*
        fragmentLoginBinding.buttonGoogleLogin.setOnClickListener( v -> {
            signInWithGoogle();
        });
         */

        // Configure Google Sign In
        signInClient = Identity.getSignInClient(requireContext());
        mAuth = FirebaseAuth.getInstance();
        // Display One-Tap Sign In if user isn't logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            oneTapSignIn();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("login fragment", "onStart");
        /*
            todo setUIRunning
            when MediatorLiveData is modified in other containers, and the user return to login,
             the instruction
                testDatabaseViewModel.setUIRunning(false);
             is not going to be invoked again, causing (probably) the Observer to run when it shouldn't.
         */
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("login fragment", "onStop");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("login fragment", "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("login fragment", "onResume");
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
        testDatabaseViewModel.getUserMediatorLiveData().removeObserver(observer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("login fragment", "onDestroy");
    }

    private void signInWithGoogle() {
        GetSignInIntentRequest signInRequest = GetSignInIntentRequest.builder()
                .setServerClientId(getString(R.string.default_web_client_id))
                .build();

        signInClient.getSignInIntent(signInRequest)
                .addOnSuccessListener(new OnSuccessListener<PendingIntent>() {
                    @Override
                    public void onSuccess(PendingIntent pendingIntent) {
                        launchSignIn(pendingIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("getSignInIntent error", "Google Sign-in failed", e);
                    }
                });
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

    private void oneTapSignIn() {
        // Configure One Tap UI
        BeginSignInRequest oneTapRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id))
                                .setFilterByAuthorizedAccounts(true)
                                .build()
                )
                .build();

        // Display the One Tap UI
        signInClient.beginSignIn(oneTapRequest)
                .addOnSuccessListener(new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult beginSignInResult) {
                        launchSignIn(beginSignInResult.getPendingIntent());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                    }
                });
    }

    private void handleSignInResult(Intent data) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            SignInCredential credential = signInClient.getSignInCredentialFromIntent(data);
            String idToken = credential.getGoogleIdToken();
            Log.d("handleSignInResult", "firebaseAuthWithGoogle:" + credential.getId());
            firebaseAuthWithGoogle(idToken);
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Log.w("handleSignInResult error", "Google sign in failed", e);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("onComplete", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("onComplete error", "signInWithCredential:failure", task.getException());
                            Snackbar.make(requireView(), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
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

    private void initRepositories(){
        TestIDatabaseRepository testDatabaseRepository = TestServiceLocator.getInstance(requireActivity().getApplication())
                .getRepository(TestIDatabaseRepository.class);
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(testDatabaseRepository)
                .create(TestDatabaseViewModel.class);

        testDatabaseViewModel.setUIRunning(false);
    }


}