package it.unimib.readify.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.ResponseCallback;

public class UserRepository implements IUserRepository, UserResponseCallback, ResponseCallback {






























    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        return null;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        return null;
    }

    @Override
    public MutableLiveData<Result> logout() {
        return null;
    }

    @Override
    public User getLoggedUser() {
        return null;
    }

    @Override
    public void registration(String email, String password, String username, String genre) {

    }

    @Override
    public void login(String email, String password) {

    }

    @Override
    public void signInWithGoogle(String token) {

    }

    @Override
    public void onSuccessFromAuthentication(User user) {

    }

    @Override
    public void onFailureFromAuthentication(String message) {

    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {

    }

    @Override
    public void onSuccessFromRemoteDatabase(List<OLWorkApiResponse> bookList) {

    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {

    }

    @Override
    public void onSuccessLogout() {

    }

    @Override
    public void onSuccessSearchFromRemote(List<OLWorkApiResponse> searchApiResponse) {

    }

    @Override
    public void onSuccessFetchBookFromRemote(OLWorkApiResponse workApiResponse) {

    }

    @Override
    public void onSuccessFetchBooksFromRemote(List<OLWorkApiResponse> workApiResponseList, String reference) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }
}
