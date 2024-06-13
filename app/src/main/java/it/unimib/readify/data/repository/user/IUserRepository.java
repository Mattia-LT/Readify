package it.unimib.readify.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unimib.readify.model.Comment;
import it.unimib.readify.model.Notification;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;

public interface IUserRepository {

    void getUser(String email, String password, boolean isRegistered);
    void signIn(String email, String password);
    void signUp(String email, String password);
    void userAuthentication(String email, String password);
    void signInWithGoogle(String idToken);
    void logout();
    void changeUserPassword(String newPassword);
    void setUserUsername(User user);
    void setUserEmail(String newEmail);
    void setUserGender(User user);
    void setUserVisibility(User user);
    void setUserRecommended(User user);
    void setUserAvatar(User user);
    void setUserBiography(User user);
    void setUserFollowing(User user);
    void setUserFollowers(User user);
    void setUserTotalNumberOfBooks(User user);
    void fetchNotifications(String idToken);
    void readNotifications(String idToken, String notificationType);
    void addNotification(String receivingIdToken, String content, String loggedUserIdToken);
    void removeNotification(String targetIdToken, String content, String loggedUserIdToken);
    void fetchComments(String bookId);
    void searchUsers(String query, int limit);
    void addComment(String content, String bookId, String idToken);
    void deleteComment(String bookId, Comment comment);
    void fetchFollowers(String idToken);
    void fetchFollowing(String idToken);
    void followUser(String idTokenLoggedUser, String idTokenFollowedUser);
    void unfollowUser(String idTokenLoggedUser, String idTokenFollowedUser);
    void fetchOtherUser(String otherUserIdToken);
    void isUsernameAvailable(String username);
    void deleteUserInfo();
    void resetAuthenticationResult();
    void resetPasswordErrorResult();
    MutableLiveData<Result> getUserMutableLiveData();
    MutableLiveData<List<Result>> getUserSearchResultsLiveData();
    MutableLiveData<List<Result>> getCommentListLiveData();
    MutableLiveData<List<Result>> getFollowersListLiveData();
    MutableLiveData<List<Result>> getFollowingListLiveData();
    MutableLiveData<Result> getOtherUserLiveData();
    MutableLiveData<String> getUsernameAvailableResult();
    MutableLiveData<Boolean> getSourceEmailError();
    MutableLiveData<Boolean> getSourcePasswordError();
    MutableLiveData<Boolean> getLogoutResult();
    MutableLiveData<Boolean> getUserAuthenticationResult();
    MutableLiveData<HashMap<String, ArrayList<Notification>>> getFetchedNotifications();
    MutableLiveData<Long> getLastAuthenticationTimestamp();
}