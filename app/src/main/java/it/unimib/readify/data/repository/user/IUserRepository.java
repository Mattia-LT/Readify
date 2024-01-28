package it.unimib.readify.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;


public interface IUserRepository {
    MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered);
    MutableLiveData<Result> getGoogleUser(String idToken);
    MutableLiveData<Result> logout();
    User getLoggedUser();
    void registration(String email, String password, String username, String genre );
    void login(String email, String password);
    void signInWithGoogle(String token);

}
