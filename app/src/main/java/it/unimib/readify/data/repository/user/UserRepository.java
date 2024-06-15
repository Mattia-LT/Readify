package it.unimib.readify.data.repository.user;

import static it.unimib.readify.util.Constants.NOTIFICATION_NEW_FOLLOWER;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unimib.readify.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.readify.data.source.user.BaseUserDataRemoteDataSource;
import it.unimib.readify.data.source.user.UserAuthenticationRemoteDataSource;
import it.unimib.readify.data.source.user.UserDataRemoteDataSource;
import it.unimib.readify.model.Comment;
import it.unimib.readify.model.FollowGroup;
import it.unimib.readify.model.FollowUser;
import it.unimib.readify.model.Notification;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;

public class UserRepository implements IUserRepository, UserResponseCallback {

    private final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userAuthRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;

    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<List<Result>> userSearchResultsLiveData;
    private final MutableLiveData<List<Result>> commentListLiveData;
    private final MutableLiveData<List<Result>> followerListLiveData;
    private final MutableLiveData<List<Result>> followingListLiveData;
    private final MutableLiveData<Result> otherUserLiveData;
    private final MutableLiveData<String> usernameAvailableResult;
    private final MutableLiveData<Boolean> sourcePasswordError;
    private final MutableLiveData<Boolean> logoutResult;
    private final MutableLiveData<Boolean> userAuthenticationResult;
    private final MutableLiveData<HashMap<String, ArrayList<Notification>>> fetchedNotifications;
    private final MutableLiveData<Long> lastAuthenticationTimestamp;

    public static IUserRepository getInstance() {
        return new UserRepository(new UserAuthenticationRemoteDataSource(),
                new UserDataRemoteDataSource());
    }

    private UserRepository(BaseUserAuthenticationRemoteDataSource userAuthRemoteDataSource,
                           BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userAuthRemoteDataSource = userAuthRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userSearchResultsLiveData = new MutableLiveData<>();
        this.commentListLiveData = new MutableLiveData<>();
        this.followerListLiveData = new MutableLiveData<>();
        this.followingListLiveData = new MutableLiveData<>();
        this.otherUserLiveData = new MutableLiveData<>();
        this.usernameAvailableResult = new MutableLiveData<>();
        this.sourcePasswordError = new MutableLiveData<>();
        this.fetchedNotifications = new MutableLiveData<>();
        this.logoutResult = new MutableLiveData<>();
        this.lastAuthenticationTimestamp = new MutableLiveData<>();
        this.userAuthenticationResult = new MutableLiveData<>();
        this.userAuthRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
    }

    @Override
    public void getUser(String email, String password, boolean isRegistered) {
        if(isRegistered) {
            signIn(email, password);
        }
        if(!isRegistered) {
            signUp(email, password);
        }
    }

    public MutableLiveData<Result> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    @Override
    public void signIn(String email, String password) {
        userAuthRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signUp(String email, String password) {
        userAuthRemoteDataSource.signUp(email, password);
    }

    @Override
    public void userAuthentication(String email, String password) {
        userAuthRemoteDataSource.userAuthentication(email, password);
    }

    @Override
    public void signInWithGoogle(String idToken) {
        userAuthRemoteDataSource.signInWithGoogle(idToken);
    }

    @Override
    public void logout() {
        userAuthRemoteDataSource.logout();
    }

    @Override
    public void changeUserPassword(String newPassword) {
        userAuthRemoteDataSource.changePassword(newPassword);
    }

    @Override
    public void setUserUsername(User user) {
        userDataRemoteDataSource.setUsername(user);
    }

    @Override
    public void setUserGender(User user) {
        userDataRemoteDataSource.setGender(user);
    }

    @Override
    public void setUserVisibility(User user) {
        userDataRemoteDataSource.setVisibility(user);
    }

    public void setUserRecommended(User user) {
        userDataRemoteDataSource.setRecommended(user);
    }

    public void setUserAvatar(User user) {
        userDataRemoteDataSource.setAvatar(user);
    }

    @Override
    public void setUserBiography(User user) {
        userDataRemoteDataSource.setBiography(user);
    }

    @Override
    public void setUserFollowing(User user) {
        userDataRemoteDataSource.setFollowing(user);
    }

    @Override
    public void setUserFollowers(User user) {
        userDataRemoteDataSource.setFollowers(user);
    }

    @Override
    public void setUserTotalNumberOfBooks(User user) {
        userDataRemoteDataSource.setTotalNumberOfBooks(user);
    }

    @Override
    public void fetchNotifications(String idToken) {
        userDataRemoteDataSource.fetchNotifications(idToken);
    }

    @Override
    public void readNotifications(String idToken, String notificationType) {
        userDataRemoteDataSource.readNotifications(idToken, notificationType);
    }

    @Override
    public void addNotification(String receivingIdToken, String content, String loggedUserIdToken) {
        userDataRemoteDataSource.addNotification(receivingIdToken, content, loggedUserIdToken);
    }

    @Override
    public void removeNotification(String targetIdToken, String content, String loggedUserIdToken) {
        userDataRemoteDataSource.removeNotification(targetIdToken, content, loggedUserIdToken);
    }

    @Override
    public void searchUsers(String query, int limit){
        userDataRemoteDataSource.searchUsers(query, limit);
    }

    @Override
    public MutableLiveData<List<Result>> getCommentListLiveData() {
        return commentListLiveData;
    }

    @Override
    public MutableLiveData<List<Result>> getFollowersListLiveData() {
        return followerListLiveData;
    }

    @Override
    public MutableLiveData<List<Result>> getFollowingListLiveData() {
        return followingListLiveData;
    }

    @Override
    public MutableLiveData<Result> getOtherUserLiveData() {
        return otherUserLiveData;
    }

    @Override
    public MutableLiveData<List<Result>> getUserSearchResultsLiveData(){
        return userSearchResultsLiveData;
    }

    @Override
    public void fetchComments(String bookId) {
        userDataRemoteDataSource.fetchComments(bookId);
    }

    @Override
    public MutableLiveData<String> getUsernameAvailableResult() {
        return usernameAvailableResult;
    }

    @Override
    public MutableLiveData<Boolean> getSourcePasswordError() {
        return sourcePasswordError;
    }

    @Override
    public MutableLiveData<Boolean> getLogoutResult() {
        return logoutResult;
    }

    @Override
    public MutableLiveData<HashMap<String, ArrayList<Notification>>> getFetchedNotifications() {
        return fetchedNotifications;
    }

    public MutableLiveData<Boolean> getUserAuthenticationResult() {
        return userAuthenticationResult;
    }

    public MutableLiveData<Long> getLastAuthenticationTimestamp() {
        return lastAuthenticationTimestamp;
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            Log.d(TAG, "Authentication success.");
            userDataRemoteDataSource.saveUserData(user);
            logoutResult.postValue(null);
            lastAuthenticationTimestamp.postValue(System.currentTimeMillis());
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Log.e(TAG, message);
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Log.d(TAG, "User data updated successfully.");
        Result.UserSuccess result = new Result.UserSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessUserSearch(List<User> searchResults) {
        Log.d(TAG, "User search success");
        List<Result> resultList = new ArrayList<>();
        for(User user : searchResults){
            resultList.add(new Result.UserSuccess(user));
        }
        userSearchResultsLiveData.postValue(resultList);
    }

    @Override
    public void onFailureUserSearch(String message) {
        Log.e(TAG, "Error on user search : " + message);
    }

    @Override
    public void onFailureFromRemoteDatabaseUser(String message) {
        Log.e(TAG, message);
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFetchCommentsFromRemoteDatabase(List<Comment> comments) {
        List<Result> commentResultList = new ArrayList<>();
        for(Comment comment : comments){
            commentResultList.add(new Result.CommentSuccess(comment));
        }
        Log.d(TAG, "Comments fetched successfully.");
        commentListLiveData.postValue(commentResultList);
    }

    @Override
    public void onFailureFetchCommentsFromRemoteDatabase(String message) {
        Log.e(TAG, "Error during comments retrieving.\n" + message);
    }

    @Override
    public void onSuccessFetchFollowersFromRemoteDatabase(List<FollowUser> followerList) {
        Log.d(TAG, "Followers fetched successfully");
        List<Result> followersResultList = new ArrayList<>();
        for(FollowUser follower : followerList){
            followersResultList.add(new Result.FollowUserSuccess(follower));
        }
        followerListLiveData.postValue(followersResultList);
    }

    @Override
    public void onFailureFetchFollowersFromRemoteDatabase(String message) {
        Log.e(TAG, "Error during followers retrieving.\n" + message);
    }

    @Override
    public void onSuccessFetchFollowingFromRemoteDatabase(List<FollowUser> followingList) {
        Log.d(TAG, "Followings fetched successfully");
        List<Result> followingResultList = new ArrayList<>();
        for(FollowUser following : followingList){
            followingResultList.add(new Result.FollowUserSuccess(following));
        }
        followingListLiveData.postValue(followingResultList);
    }

    @Override
    public void onFailureFetchFollowingFromRemoteDatabase(String message) {
        Log.e(TAG, "Error during followings retrieving.\n" + message);
    }

    @Override
    public void onSuccessAddComment(String bookId, Comment comment) {
        Log.d(TAG,"Comment added successfully");
        fetchComments(bookId);
    }

    @Override
    public void onFailureAddComment(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessDeleteComment(String bookId, Comment comment) {
        Log.d(TAG,"Comment deleted successfully");
        fetchComments(bookId);
    }

    @Override
    public void onFailureDeleteComment(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onFailureFetchSingleComment(String message) {
        //Only a warning because the operation might be successful anyways.
        Log.w(TAG, message);
    }

    @Override
    public void onFailureFetchSingleFollower(String message) {
        //Only a warning because the operation might be successful anyways.
        Log.w(TAG, message);
    }

    @Override
    public void onFailureFetchSingleFollowing(String message) {
        //Only a warning because the operation might be successful anyways.
        Log.w(TAG, message);
    }

    @Override
    public void onFailureFetchSingleNotification(String message) {
        //Only a warning because the operation might be successful anyways.
        Log.w(TAG, message);
    }

    @Override
    public void addComment(String commentContent, String bookId, String idToken){
        userDataRemoteDataSource.addComment(commentContent,bookId,idToken);
    }

    @Override
    public void deleteComment(String bookId, Comment deletedComment) {
        userDataRemoteDataSource.deleteComment(bookId, deletedComment);
    }

    @Override
    public void fetchFollowers(String idToken) {
        userDataRemoteDataSource.fetchFollowers(idToken);
    }

    @Override
    public void fetchFollowing(String idToken) {
        userDataRemoteDataSource.fetchFollowings(idToken);
    }

    @Override
    public void followUser(String idTokenLoggedUser, String idTokenFollowedUser) {
        userDataRemoteDataSource.followUser(idTokenLoggedUser, idTokenFollowedUser);
    }

    @Override
    public void unfollowUser(String idTokenLoggedUser, String idTokenFollowedUser) {
        userDataRemoteDataSource.unfollowUser(idTokenLoggedUser, idTokenFollowedUser);
    }

    @Override
    public void fetchOtherUser(String otherUserIdToken) {
        userDataRemoteDataSource.fetchOtherUser(otherUserIdToken);
    }

    @Override
    public void isUsernameAvailable(String username) {
        userDataRemoteDataSource.isUsernameAvailable(username);
    }

    @Override
    public void deleteUserInfo() {
        if(userMutableLiveData.getValue() != null){
            Result currentValue = userMutableLiveData.getValue();
            if(currentValue.isSuccess()) {
                User currentUser = ((Result.UserSuccess)(userMutableLiveData.getValue())).getData();
                currentUser.setIdToken(null);
                userMutableLiveData.postValue(new Result.UserSuccess(currentUser));
            }
        }
    }

    @Override
    public void resetAuthenticationResult() {
        userAuthenticationResult.postValue(null);
    }

    @Override
    public void resetPasswordErrorResult() {
        sourcePasswordError.postValue(null);
    }

    @Override
    public void onSuccessFetchOtherUser(User otherUser) {
        Log.d(TAG, "Other user data fetched successfully");
        Result otherUserResult = new Result.UserSuccess(otherUser);
        otherUserLiveData.postValue(otherUserResult);
    }

    @Override
    public void onFailureFetchOtherUser(String message) {
        Log.e(TAG, message);
        Result otherUserResult = new Result.Error(message);
        otherUserLiveData.postValue(otherUserResult);
    }

    @Override
    public void onSuccessFollowUser(String loggedUserIdToken, String followedUserIdToken) {
        Log.d(TAG, "User " + loggedUserIdToken + " started following " +  followedUserIdToken + " successfully");
        userDataRemoteDataSource.refreshLoggedUserData(loggedUserIdToken);
        userDataRemoteDataSource.fetchOtherUser(followedUserIdToken);
        addNotification(followedUserIdToken, NOTIFICATION_NEW_FOLLOWER, loggedUserIdToken);
    }

    @Override
    public void onFailureFollowUser(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessUnfollowUser(String loggedUserIdToken, String unfollowedUserIdToken) {
        Log.d(TAG, "User " + loggedUserIdToken + " unfollowed" +  unfollowedUserIdToken + " successfully");
        userDataRemoteDataSource.refreshLoggedUserData(loggedUserIdToken);
        userDataRemoteDataSource.fetchOtherUser(unfollowedUserIdToken);
        removeNotification(unfollowedUserIdToken, NOTIFICATION_NEW_FOLLOWER, loggedUserIdToken);
    }

    @Override
    public void onFailureUnfollowUser(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessFetchInfoForFollow(String loggedUserIdToken, String followedUserIdToken, FollowGroup loggedUserFollowing, FollowGroup followedUserFollowers) {
        userDataRemoteDataSource.endFollowOperation(loggedUserIdToken, followedUserIdToken, loggedUserFollowing, followedUserFollowers);
    }

    @Override
    public void onFailureFetchInfoForFollow(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessFetchInfoForUnfollow(String loggedUserIdToken, String followedUserIdToken, FollowGroup loggedUserFollowing, FollowGroup followedUserFollowers) {
        userDataRemoteDataSource.endUnfollowOperation(loggedUserIdToken, followedUserIdToken, loggedUserFollowing, followedUserFollowers);
    }

    @Override
    public void onFailureFetchInfoForUnfollow(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessLogout() {
        Log.d(TAG, "logout succeeded");
        logoutResult.postValue(true);
    }

    @Override
    public void onFailureLogout() {
        Log.d(TAG, "logout failed");
        logoutResult.postValue(false);
    }

    @Override
    public void onUsernameAvailable(String result) {
        usernameAvailableResult.postValue(result);
    }

    @Override
    public void onPasswordChanged(Boolean result) {
        sourcePasswordError.postValue(result);
        if(result) {
            Log.d(TAG, "Password changed");
        } else {
            Log.w(TAG, "Password didn't change");
        }
    }

    @Override
    public void onSuccessFetchNotifications(HashMap<String, ArrayList<Notification>> notifications) {
        Log.d(TAG, "Notifications fetched successfully");
        this.fetchedNotifications.postValue(notifications);
    }

    @Override
    public void onFailureFetchNotifications(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void onSuccessReAuthentication() {
        Log.d(TAG, "Re-authentication succeeded");
        userAuthenticationResult.postValue(true);
        lastAuthenticationTimestamp.postValue(System.currentTimeMillis());
    }

    @Override
    public void onFailureReAuthentication() {
        Log.w(TAG, "Re-authentication failed");
        userAuthenticationResult.postValue(false);
    }

    @Override
    public void onSuccessReadNotification(String loggedUserIdToken) {
        Log.d(TAG, "Notifications read succeeded");
        fetchNotifications(loggedUserIdToken);
    }

    @Override
    public void onFailureReadNotification(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessAddNotification() {
        Log.d(TAG, "Notification added successfully");
    }

    @Override
    public void onFailureAddNotification(String message) {
        Log.e(TAG, message);
    }

    @Override
    public void onSuccessRemoveNotification() {
        Log.d(TAG, "Notification removed successfully");
    }

    @Override
    public void onFailureRemoveNotification(String message) {
        Log.e(TAG, message);
    }
}