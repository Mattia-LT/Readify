package it.unimib.readify.data.source.user;

import java.util.List;

import it.unimib.readify.data.repository.user.UserResponseCallback;
import it.unimib.readify.model.Comment;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.User;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);
    public abstract void saveWorkData(OLWorkApiResponse work);
    public abstract void getUser(String idToken);
    public abstract void getWork(String idBook);


    //se salviamo le user preferences sul dispositivo (che sembra più sensato), non ci servono
    public abstract void getUserPreferences(String idToken);
    public abstract void saveUserPreferences(String message, String idToken);
}
