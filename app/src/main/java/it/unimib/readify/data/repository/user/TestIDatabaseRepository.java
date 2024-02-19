package it.unimib.readify.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import it.unimib.readify.model.Result;

public interface TestIDatabaseRepository {

    void getUser(String email, String password, boolean isRegistered);
    void signIn(String email, String password);
    void signUp(String email, String password);

    void testSet(Result result);
    MutableLiveData<Result> getUserMutableLiveData();
}
