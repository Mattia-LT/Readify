package it.unimib.readify.data.repository.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unimib.readify.model.Comment;
import it.unimib.readify.model.FollowGroup;
import it.unimib.readify.model.FollowUser;
import it.unimib.readify.model.Notification;
import it.unimib.readify.model.User;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessUserSearch(List<User> searchResults);
    void onFailureUserSearch(String message);
    void onFailureFromRemoteDatabaseUser(String message);
    void onSuccessFetchCommentsFromRemoteDatabase(List<Comment> comments);
    void onFailureFetchCommentsFromRemoteDatabase(String message);
    void onSuccessFetchFollowersFromRemoteDatabase(List<FollowUser> followerList);
    void onFailureFetchFollowersFromRemoteDatabase(String message);
    void onSuccessFetchFollowingFromRemoteDatabase(List<FollowUser> followingList);
    void onFailureFetchFollowingFromRemoteDatabase(String message);
    void onSuccessAddComment(String bookId, Comment comment);
    void onFailureAddComment(String message);
    void onSuccessDeleteComment(String bookId, Comment comment);
    void onFailureDeleteComment(String message);
    void onFailureFetchSingleComment(String message);
    void onFailureFetchSingleFollower(String message);
    void onFailureFetchSingleFollowing(String message);
    void onSuccessFetchOtherUser(User otherUser);
    void onFailureFetchOtherUser(String message);
    void onSuccessFollowUser(String loggedUserIdToken, String followedUserIdToken);
    void onFailureFollowUser(String message);
    void onSuccessUnfollowUser(String loggedUserIdToken, String unfollowedUserIdToken);
    void onFailureUnfollowUser(String message);
    void onSuccessFetchInfoForFollow(String loggedUserIdToken, String followedUserIdToken, FollowGroup loggedUserFollowing, FollowGroup followedUserFollowers);
    void onFailureFetchInfoForFollow(String message);
    void onSuccessFetchInfoForUnfollow(String loggedUserIdToken, String followedUserIdToken, FollowGroup loggedUserFollowing, FollowGroup followedUserFollowers);
    void onFailureFetchInfoForUnfollow(String message);
    void onSuccessLogout();
    void onFailureLogout();
    void onUsernameAvailable(String result);
    void onEmailChanged(Boolean result);
    void onPasswordChanged(Boolean result);
    void onSuccessFetchNotifications(HashMap<String, ArrayList<Notification>> notifications);
    void onFailureFetchNotifications(String message);
    void onSuccessCompleteFetchNotifications(HashMap<String, ArrayList<Notification>> notifications);
    void onFailureCompleteFetchNotifications(String message);
    void onSuccessReAuthentication();
    void onFailureReAuthentication();
}