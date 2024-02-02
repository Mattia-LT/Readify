package it.unimib.readify.data.repository.user;

import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.User;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessFromRemoteDatabase(OLWorkApiResponse work);
    void onFailureFromRemoteDatabaseUser(String message);
    void onFailureFromRemoteDatabaseWork(String message);
    void onSuccessLogout();
}
