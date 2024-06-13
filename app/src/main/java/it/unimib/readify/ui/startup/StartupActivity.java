package it.unimib.readify.ui.startup;

import static it.unimib.readify.util.Constants.EMAIL_ADDRESS;
import static it.unimib.readify.util.Constants.ENCRYPTED_DATA_FILE_NAME;
import static it.unimib.readify.util.Constants.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME;
import static it.unimib.readify.util.Constants.GOOGLE_SESSION_EXPIRED;
import static it.unimib.readify.util.Constants.LOGIN_ID_TOKEN;
import static it.unimib.readify.util.Constants.PASSWORD;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import java.io.IOException;
import java.security.GeneralSecurityException;

import it.unimib.readify.R;
import it.unimib.readify.databinding.ActivityStartupBinding;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.ui.main.HomeActivity;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;

public class StartupActivity extends AppCompatActivity {

    private final String TAG = StartupActivity.class.getSimpleName();
    private DataEncryptionUtil dataEncryptionUtil;
    private Observer<Result> loggedUserObserver;
    private UserViewModel userViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startup_loading);
        dataEncryptionUtil = new DataEncryptionUtil(this.getApplication());
        userViewModel = CustomViewModelFactory.getInstance(this.getApplication())
                .create(UserViewModel.class);
        try {
            String savedEmail = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, EMAIL_ADDRESS);
            String savedPassword = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, PASSWORD);
            String savedGoogleLoginIdToken = dataEncryptionUtil.readSecretDataWithEncryptedSharedPreferences(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, LOGIN_ID_TOKEN);
            loggedUserObserver = loggedUserResult -> {
                if(userViewModel.isUIRunning()) {
                    if(loggedUserResult.isSuccess()) {
                        User user = ((Result.UserSuccess) loggedUserResult).getData();
                        Intent intent;
                        if(user.getUsername() == null){
                            intent = new Intent(this, WelcomeActivity.class);
                        } else {
                            //skippa login
                            intent = new Intent(this, HomeActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMessage = ((Result.Error) loggedUserResult).getMessage();
                        Log.e(TAG, "Error: Logged user fetch wasn't successful -> " + errorMessage);
                        if(errorMessage.equals(GOOGLE_SESSION_EXPIRED)){
                            dataEncryptionUtil.deleteAll(ENCRYPTED_SHARED_PREFERENCES_FILE_NAME, ENCRYPTED_DATA_FILE_NAME);
                            showStartupLayout();
                        }
                    }
                }
            };

            if(savedEmail != null){
                if(savedPassword != null){
                    //login normale
                    userViewModel.setUserMutableLiveData(savedEmail, savedPassword, true);
                    userViewModel.getUserMediatorLiveData().observe(this, loggedUserObserver);
                }
            } else if(savedGoogleLoginIdToken != null){
                //login google
                Log.d("dovrebbe entrare qui", "please");
                userViewModel.signInWithGoogle(savedGoogleLoginIdToken);
                userViewModel.getUserMediatorLiveData().observe(this, loggedUserObserver);

            } else {
                showStartupLayout();
            }
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(loggedUserObserver != null){
            userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
        }
    }

    private void showStartupLayout(){
        ActivityStartupBinding binding = ActivityStartupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonStartup.setOnClickListener(view -> {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
