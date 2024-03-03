package it.unimib.readify.data.repository.user;

import java.util.List;

import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.User;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessFromRemoteDatabase(OLWorkApiResponse work);
    void onSuccessFromRemoteDatabase(List<User> searchResults);
    void onFailureFromRemoteDatabaseUser(String message);
    void onFailureFromRemoteDatabaseWork(String message);
    void onSuccessFromRemoteDatabaseUserFromUsername(User user);
    void onFailureFromRemoteDatabaseUserFromUsername(String message);
    void onSuccessLogout();
}
