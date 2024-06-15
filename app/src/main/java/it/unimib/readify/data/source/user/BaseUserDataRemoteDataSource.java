package it.unimib.readify.data.source.user;

import it.unimib.readify.data.repository.user.UserResponseCallback;
import it.unimib.readify.model.Comment;
import it.unimib.readify.model.FollowGroup;
import it.unimib.readify.model.User;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);
    public abstract void setUsername(User user);
    public abstract void setGender(User user);
    public abstract void setVisibility(User user);
    public abstract void setRecommended(User user);
    public abstract void setAvatar(User user);
    public abstract void setBiography(User user);
    public abstract void setFollowers(User user);
    public abstract void setFollowing(User user);
    public abstract void setTotalNumberOfBooks(User user);
    public abstract void fetchNotifications(String idToken);
    public abstract void readNotifications(String idToken, String notificationType);
    public abstract void searchUsers(String query, int limit);
    public abstract void fetchComments(String bookId);
    public abstract void addComment(String commentContent, String bookId, String idToken);
    public abstract void deleteComment(String bookId, Comment comment);
    public abstract void fetchFollowers(String idToken);
    public abstract void fetchFollowings(String idToken);
    public abstract void followUser(String idTokenLoggedUser, String idTokenFollowedUser);
    public abstract void unfollowUser(String idTokenLoggedUser, String idTokenFollowedUser);
    public abstract void endFollowOperation(String idTokenLoggedUser, String idTokenFollowedUser, FollowGroup loggedUserFollowings, FollowGroup followedUserFollowers);
    public abstract void endUnfollowOperation(String idTokenLoggedUser, String idTokenFollowedUser, FollowGroup loggedUserFollowings, FollowGroup followedUserFollowers);
    public abstract void fetchOtherUser(String otherUserIdToken);
    public abstract void refreshLoggedUserData(String loggedUserIdToken);
    public abstract void addNotification(String receivingIdToken, String content, String loggedUserIdToken);
    public abstract void removeNotification(String targetIdToken, String content, String loggedUserIdToken);
    public abstract void isUsernameAvailable(String username);
}