package it.unimib.readify.data.source.user;

import java.util.Set;

import it.unimib.readify.data.repository.user.UserResponseCallback;
import it.unimib.readify.model.User;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);
    public abstract void getWork(String idToken);
    public abstract void getUserPreferences(String idToken);
    public abstract void saveUserPreferences(String message, String idToken);
}
