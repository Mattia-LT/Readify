package it.unimib.readify.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.readify.model.Comment;
import it.unimib.readify.model.Result;

public interface TestIDatabaseRepository {

    void getUser(String email, String password, boolean isRegistered);
    void signIn(String email, String password);
    void signUp(String email, String password);

    void fetchComments(String bookId);
    void searchUsers(String query);
    void addComment(String content, String bookId, String idToken);
    void deleteComment(String bookId, Comment comment);
    MutableLiveData<Result> getUserMutableLiveData();
    MutableLiveData<List<Result>> getUserSearchResultsLiveData();
    MutableLiveData<List<Result>> getCommentListLiveData();

}
