package it.unimib.readify.data.source.user;

import it.unimib.readify.data.repository.user.UserResponseCallback;
import it.unimib.readify.model.User;

public abstract class BaseUserAuthenticationRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }
    public abstract User getLoggedUser();
    public abstract void logout();
    public abstract void signUp(String email, String password, String username, String gender);
    public abstract void signIn(String email, String password);
    public abstract void signInWithGoogle(String idToken);

}
