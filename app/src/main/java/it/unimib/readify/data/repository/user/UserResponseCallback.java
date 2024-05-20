package it.unimib.readify.data.repository.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unimib.readify.model.Comment;
import it.unimib.readify.model.ExternalUser;
import it.unimib.readify.model.Notification;
import it.unimib.readify.model.User;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessFromRemoteDatabase(List<User> searchResults);
    void onFailureFromRemoteDatabaseUser(String message);
    void onSuccessFetchCommentsFromRemoteDatabase(List<Comment> comments);
    void onFailureFetchCommentsFromRemoteDatabase(String message);
    void onSuccessFetchFollowersFromRemoteDatabase(List<ExternalUser> followerList);
    void onFailureFetchFollowersFromRemoteDatabase(String message);
    void onSuccessFetchFollowingFromRemoteDatabase(List<ExternalUser> followingList);
    void onFailureFetchFollowingFromRemoteDatabase(String message);
    void onAddCommentResult(Comment comment);
    void onFetchOtherUserResult(User otherUser);
    void onDeleteCommentResult();
    void onUserFollowResult();
    void onUserUnfollowResult();
    void onSuccessLogout();
    void onUsernameAvailable(String result);
    void onEmailChanged(Boolean result);
    void onPasswordChanged(Boolean result);
    void onSuccessFetchNotifications(HashMap<String, ArrayList<Notification>> notifications);
    void onFailureFetchNotifications(String message);
    void onSuccessCompleteFetchNotifications(HashMap<String, ArrayList<Notification>> notifications);
    void onFailureCompleteFetchNotifications(String message);
}