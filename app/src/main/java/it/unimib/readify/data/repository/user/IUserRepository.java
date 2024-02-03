package it.unimib.readify.data.repository.user;

import androidx.lifecycle.MutableLiveData;


import java.util.List;

import it.unimib.readify.model.Result;


public interface IUserRepository {
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getGoogleUser(String idToken);
    MutableLiveData<Result> getWork(String idToken);
    MutableLiveData<Result> getUserData(String idToken);
    MutableLiveData<Result> getUserPreferences(String idToken);
    MutableLiveData<List<Result>> searchUsers(String query);
    MutableLiveData<Result> logout();
    MutableLiveData<Result> getLoggedUser();

    void signUp(String email, String password, String username, String gender);
    void signIn(String email, String password);
    void signInWithGoogle(String token);
    void saveUserPreferences(String message, String idToken);
}
