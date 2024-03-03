package it.unimib.readify.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.readify.model.Result;

public interface TestIDatabaseRepository {

    void getUser(String email, String password, boolean isRegistered);
    void signIn(String email, String password);
    void signUp(String email, String password);

    void testSet(Result result);
    void getUserFromUsername(String username);
    MutableLiveData<Result> getUserMutableLiveData();
    MutableLiveData<Result> getCommentUserLiveData();
    MutableLiveData<List<Result>> searchUsers(String query);
}
