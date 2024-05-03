package it.unimib.readify.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unimib.readify.model.Comment;
import it.unimib.readify.model.Notification;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;

public interface TestIDatabaseRepository {

    void getUser(String email, String password, boolean isRegistered);
    void signIn(String email, String password);
    void signUp(String email, String password);
    void updateUserData(User user, String newPassword);
    void setUserGender(User user);
    void setUserVisibility(User user);
    void setUserRecommended(User user);
    void setUserAvatar(User user);
    void fetchNotifications(String idToken);
    void completeNotificationsFetch(HashMap<String, ArrayList<Notification>> notifications);

    void fetchComments(String bookId);
    void fetchLoggedUserCollections(String idToken);
    void fetchOtherUserCollections(String otherUserIdToken);
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
    void fetchOtherUser(String otherUserIdToken);
    void renameCollection(String loggedUserIdToken, String collectionId, String newCollectionName);
    MutableLiveData<Result> getUserMutableLiveData();
    MutableLiveData<List<Result>> getUserSearchResultsLiveData();
    MutableLiveData<List<Result>> getCommentListLiveData();
    MutableLiveData<List<Result>> getLoggedUserCollectionListLiveData();
    MutableLiveData<List<Result>> getOtherUserCollectionListLiveData();
    MutableLiveData<List<Result>> getFollowersListLiveData();
    MutableLiveData<List<Result>> getFollowingListLiveData();
    MutableLiveData<Result> getOtherUserLiveData();
    MutableLiveData<String> getSourceUsernameError();
    MutableLiveData<String> getSourceEmailError();
    MutableLiveData<Boolean> getSourcePasswordError();
    MutableLiveData<HashMap<String, ArrayList<Notification>>> getFetchedNotifications();
}