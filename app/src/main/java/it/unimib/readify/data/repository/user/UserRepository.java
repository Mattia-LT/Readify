package it.unimib.readify.data.repository.user;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.data.repository.book.BookResponseCallback;
import it.unimib.readify.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.readify.data.source.user.BaseUserDataRemoteDataSource;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.ResponseCallback;

public class UserRepository implements IUserRepository, UserResponseCallback, ResponseCallback, BookResponseCallback {

    private final BaseUserAuthenticationRemoteDataSource userAuthRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> workMutableLiveData;
    private final MutableLiveData<Result> userPreferencesMutableLiveData;
    private MutableLiveData<Result> externalUserMutableLiveData;
    private final MutableLiveData<List<Result>> userSearchResultLiveData;


    public UserRepository(BaseUserAuthenticationRemoteDataSource userAuthRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userAuthRemoteDataSource = userAuthRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userPreferencesMutableLiveData = new MutableLiveData<>();
        this.workMutableLiveData = new MutableLiveData<>();
        this.userSearchResultLiveData = new MutableLiveData<>();
        this.userAuthRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
    }


    @Override
    public MutableLiveData<Result> getLoggedUser(String email, String password, boolean isRegistered) {
        if(isRegistered) {
            Log.d("sign in", "sign in");
            signIn(email, password);
        }
        if(!isRegistered) {
            Log.d("sign up", "sign up");
            signUp(email, password);
        }
        return userMutableLiveData;
    }

    public User getLoggedUser() {
        return userAuthRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(email, password);
        }
        return userMutableLiveData;
    }

    public MutableLiveData<Result> getUserData(String idToken) {
        userDataRemoteDataSource.getUser(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getWork(String idBook) {
        userDataRemoteDataSource.getWork(idBook);
        return workMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserPreferences(String idToken) {
        //TODO implementa o rimuovi
        return null;
    }

    @Override
    public MutableLiveData<Result> logout() {
        userAuthRemoteDataSource.logout();
        return userMutableLiveData;
    }



    @Override
    public void signUp(String email, String password) {
        userAuthRemoteDataSource.signUp(email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userAuthRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(String token) {
        userAuthRemoteDataSource.signInWithGoogle(token);
    }

    @Override
    public void saveUserPreferences(String message, String idToken) {
        //todo implementa o rimuovi
    }

    @Override
    public MutableLiveData<List<Result>> searchUsers(String query){
        Log.d("UserRepository", "Query: " + query);
        userDataRemoteDataSource.searchUsers(query);
        return userSearchResultLiveData;
    }


    //giusto
    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        Log.d("repo onFailureFromAuthentication", "onFailureFromAuthentication");
        //postValue? setValue?
        userMutableLiveData.postValue(result);
    }

    //giusto
    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserSuccess result = new Result.UserSuccess(user);
        Log.d("sign in on going", "onSuccessFromRemoteDatabase");
        userMutableLiveData.postValue(result);
    }

    //giusto
    @Override
    public void onSuccessFromRemoteDatabase(OLWorkApiResponse work) {
        Result.WorkSuccess result = new Result.WorkSuccess(work);
        workMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(List<User> searchResults) {
        List<Result> resultList = new ArrayList<>();
        for(User user : searchResults){
            Log.d("UserRepository", "User: " + user);
            resultList.add(new Result.UserSuccess(user));
        }
        userSearchResultLiveData.postValue(resultList);
    }

    //giusto
    @Override
    public void onFailureFromRemoteDatabaseUser(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    //giusto
    @Override
    public void onFailureFromRemoteDatabaseWork(String message) {
        Result.Error result = new Result.Error(message);
        workMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {
        userMutableLiveData.postValue(null);
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
    public void onSuccessFromGettingUserPreferences() {
        userPreferencesMutableLiveData.postValue(new Result.UserSuccess(null));
    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }
}
