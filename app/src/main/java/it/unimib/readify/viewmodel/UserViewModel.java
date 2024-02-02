package it.unimib.readify.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Set;

import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;

public class UserViewModel extends ViewModel {
    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> workMutableLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
    }

    public MutableLiveData<Result> getUserMutableLiveData(
            String email, String password, boolean isUserRegistered) {
        if (userMutableLiveData == null) {
            getUserData(email, password, isUserRegistered);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getGoogleUserMutableLiveData(String token) {
        if (userMutableLiveData == null) {
            getUserData(token);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getWorkMutableLiveData(String idToken) {
        if (workMutableLiveData == null) {
            getWork(idToken);
        }
        return workMutableLiveData;
    }

    /*
    public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {
        if (idToken != null) {
            userRepository.saveUserPreferences(favoriteCountry, favoriteTopics, idToken);
        }
    }

    public MutableLiveData<Result> getUserPreferences(String idToken) {
        if (idToken != null) {
            userPreferencesMutableLiveData = userRepository.getUserPreferences(idToken);
        }
        return userPreferencesMutableLiveData;
    }
    */

    public MutableLiveData<Result> getLoggedUser() {
        //return userRepository.getLoggedUser();
        return userMutableLiveData;
    }

    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

        return userMutableLiveData;
    }

    public void getUser(String email, String password, boolean isUserRegistered) {
        userRepository.getUser(email, password, isUserRegistered);
    }

    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }

    private void getUserData(String email, String password, boolean isUserRegistered) {
        userMutableLiveData = userRepository.getUser(email, password, isUserRegistered);
    }

    public void getUserData(String token) {
        userMutableLiveData = userRepository.getGoogleUser(token);
    }

    //todo da rivedere
    public MutableLiveData<Result> getUserDataFromToken(String token) {
        userMutableLiveData = userRepository.getUserData(token);
        return userMutableLiveData;
    }

    private void getWork(String idToken){
        workMutableLiveData = userRepository.getWork(idToken);
    }

    public void createUser(String email, String password, String username, String gender) {
        //todo gestire user = null nel logout
        userRepository.signUp(email, password, username, gender);
        userMutableLiveData = userRepository.getLoggedUser();
    }

}
