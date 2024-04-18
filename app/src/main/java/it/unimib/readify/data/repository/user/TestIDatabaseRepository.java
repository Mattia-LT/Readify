package it.unimib.readify.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.readify.model.Comment;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;

public interface TestIDatabaseRepository {

    void getUser(String email, String password, boolean isRegistered);
    void signIn(String email, String password);
    void signUp(String email, String password);
    void updateUserData(User user, String newPassword);

    void fetchComments(String bookId);
    void fetchCollections(String idToken);
    void searchUsers(String query);
    void addComment(String content, String bookId, String idToken);
    void deleteComment(String bookId, Comment comment);
    void createCollection(String bookId, String collectionName, boolean visibility);
    void deleteCollection(String idToken, String collectionId);
    void addBookToCollection(String idToken, String bookId, String collectionId);
    void removeBookFromCollection(String idToken, String bookId, String collectionId);
    void fetchFollowers(String idToken);
    void fetchFollowing(String idToken);
    void followUser(String idTokenLoggedUser, String idTokenFollowedUser);
    void unfollowUser(String idTokenLoggedUser, String idTokenFollowedUser);
    MutableLiveData<Result> getUserMutableLiveData();
    MutableLiveData<List<Result>> getUserSearchResultsLiveData();
    MutableLiveData<List<Result>> getCommentListLiveData();
    MutableLiveData<List<Result>> getCollectionListLiveData();
    MutableLiveData<List<Result>> getFollowersListLiveData();
    MutableLiveData<List<Result>> getFollowingListLiveData();
    MutableLiveData<String> getSourceUsernameError();
    MutableLiveData<String> getSourceEmailError();
    MutableLiveData<Boolean> getSourcePasswordError();
}