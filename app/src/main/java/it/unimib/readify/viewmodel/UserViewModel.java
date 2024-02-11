package it.unimib.readify.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Set;

import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;

public class UserViewModel extends ViewModel {
    private final IUserRepository userRepository;
    private MutableLiveData<Result> userMutableLiveData;
    private MutableLiveData<Result> externalUserMutableLiveData;
    private MutableLiveData<Result> workMutableLiveData;
    private MutableLiveData<List<Result>> userSearchResultLiveData;
    private boolean authenticationError;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
        authenticationError = false;
    }

    public MutableLiveData<Result> getLoggedUser(String email, String password, boolean isRegistered) {
        if(userMutableLiveData == null) {
            Log.d("viewModel null", "viewModel null");
            setUserMutableLiveData(email, password, isRegistered);
        }
        Log.d("viewModel out null", "viewModel out null");
        return userMutableLiveData;
    }

    public void setUserMutableLiveData(String email, String password, boolean isRegistered) {
        userMutableLiveData = userRepository.getLoggedUser(email, password, isRegistered);
    }

    public boolean isAuthenticationError() {
        return authenticationError;
    }

    public void setAuthenticationError(boolean authenticationError) {
        this.authenticationError = authenticationError;
    }



    public User getLoggedUser() {
        //return userMutableLiveData;
        return userRepository.getLoggedUser();
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



    public MutableLiveData<Result> logout() {
        if (userMutableLiveData == null) {
            userMutableLiveData = userRepository.logout();
        } else {
            userRepository.logout();
        }

        return userMutableLiveData;
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

    public MutableLiveData<List<Result>> searchUsers(String query){
        Log.d("UserViewModel", "Query: " + query);
        userSearchResultLiveData = userRepository.searchUsers(query);
        return userSearchResultLiveData;
    }

}
