package it.unimib.readify.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import java.util.Set;

import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;


public interface IUserRepository {
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getGoogleUser(String idToken);
    MutableLiveData<Result> getWork(String idToken);
    MutableLiveData<Result> getUserPreferences(String idToken);
    MutableLiveData<Result> logout();
    User getLoggedUser();
    void signUp(String email, String password);
    void signIn(String email, String password);
    void signInWithGoogle(String token);
    void saveUserPreferences(String message, String idToken);


}
