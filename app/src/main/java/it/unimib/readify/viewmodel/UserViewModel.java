package it.unimib.readify.viewmodel;

import static it.unimib.readify.util.Constants.USER_SEARCH_LIMIT;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.model.Comment;
import it.unimib.readify.model.Notification;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;

public class UserViewModel extends ViewModel {

    private final IUserRepository userRepository;

    private final MediatorLiveData<Result> userData = new MediatorLiveData<>();

    private MutableLiveData<List<Result>> userSearchResultsLiveData;
    private MutableLiveData<List<Result>> commentListLiveData;
    private MutableLiveData<List<Result>> followersListLiveData;
    private MutableLiveData<List<Result>> followingListLiveData;
    private MutableLiveData<Result> otherUserLiveData;

    private final MutableLiveData<String> usernameAvailableResult;
    private final MutableLiveData<Boolean> sourcePasswordError;
    private MutableLiveData<Boolean> logoutResult;
    private final MutableLiveData<Boolean> userAuthenticationResult;
    private final MutableLiveData<HashMap<String, ArrayList<Notification>>> notifications;
    private boolean isUIRunning;
    private boolean firstLoading = true;
    private boolean continueRegistrationFirstLoading = true;
    private final MutableLiveData<Long> lastAuthenticationTimestamp;

    public UserViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;

        MutableLiveData<Result> repositoryData = userRepository.getUserMutableLiveData();
        userData.addSource(repositoryData, newData -> {
            Result result;
            if(newData.isSuccess()) {
                result = new Result.UserSuccess(((Result.UserSuccess)newData).getData());
            } else {
                result = new Result.Error(((Result.Error)newData).getMessage());
            }
            userData.postValue(result);
        });

        usernameAvailableResult = userRepository.getUsernameAvailableResult();
        sourcePasswordError = userRepository.getSourcePasswordError();
        notifications = userRepository.getFetchedNotifications();
        userAuthenticationResult = userRepository.getUserAuthenticationResult();
        lastAuthenticationTimestamp = userRepository.getLastAuthenticationTimestamp();
    }

    public void setUserMutableLiveData(String email, String password, boolean isRegistered) {
        userRepository.getUser(email, password, isRegistered);
        if(!isUIRunning) {
            setUIRunning(true);
        }
    }

    public MediatorLiveData<Result> getUserMediatorLiveData() {
        return userData;
    }

    public boolean isUIRunning() {
        return isUIRunning;
    }

    public void setUIRunning(boolean UIRunning) {
        isUIRunning = UIRunning;
    }

    public void searchUsers(String query){
        userRepository.searchUsers(query, USER_SEARCH_LIMIT);
    }

    public MutableLiveData<List<Result>> getUserSearchResultsLiveData(){
        if(userSearchResultsLiveData == null){
            userSearchResultsLiveData = userRepository.getUserSearchResultsLiveData();
        }
        return userSearchResultsLiveData;
    }

    public MutableLiveData<List<Result>> getCommentList(){
        if(commentListLiveData == null){
            commentListLiveData = userRepository.getCommentListLiveData();
        }
        return commentListLiveData;
    }

    public MutableLiveData<Long> getLastAuthenticationTimestamp() {
        return lastAuthenticationTimestamp;
    }

    public void fetchComments(String bookId){
        userRepository.fetchComments(bookId);
    }

    public void addComment(String commentContent, String bookId, String idToken){
        userRepository.addComment(commentContent,bookId,idToken);
    }

    public void deleteComment(String bookId, Comment deletedComment){
        userRepository.deleteComment(bookId, deletedComment);
    }

    public void changeUserPassword(String newPassword) {
        userRepository.changeUserPassword(newPassword);}

    public void setUserUsername(User user) {
        userRepository.setUserUsername(user);
    }

    public void setUserGender(User user) {
        userRepository.setUserGender(user);
    }

    public void setUserVisibility(User user) {
        userRepository.setUserVisibility(user);
    }

    public void setUserRecommended(User user) {
        userRepository.setUserRecommended(user);
    }

    public void setUserBiography(User user) {
        userRepository.setUserBiography(user);
    }

    public void setUserAvatar(User user) {
        userRepository.setUserAvatar(user);
    }

    public void setUserFollowing(User user) {
        userRepository.setUserFollowing(user);}

    public void setUserFollowers(User user) {
        userRepository.setUserFollowers(user);}

    public void setUserTotalNumberOfBooks(User user) {
        userRepository.setUserTotalNumberOfBooks(user);}

    public void fetchNotifications(String idToken) {
        userRepository.fetchNotifications(idToken);
    }

    public void readNotifications(String idToken, String notificationType) {
        userRepository.readNotifications(idToken, notificationType);
    }

    public void fetchFollowers(String idToken){
        userRepository.fetchFollowers(idToken);
    }

    public void fetchFollowing(String idToken){
        userRepository.fetchFollowing(idToken);
    }

    public MutableLiveData<List<Result>> getFollowersListLiveData() {
        if(followersListLiveData == null){
            followersListLiveData = userRepository.getFollowersListLiveData();
        }
        return followersListLiveData;
    }

    public MutableLiveData<List<Result>> getFollowingListLiveData() {
        if(followingListLiveData == null){
            followingListLiveData = userRepository.getFollowingListLiveData();
        }
        return followingListLiveData;
    }

    public MutableLiveData<String> getUsernameAvailableResult() {
        return usernameAvailableResult;
    }

    public MutableLiveData<Boolean> getSourcePasswordError() {
        return sourcePasswordError;
    }

    public MutableLiveData<HashMap<String, ArrayList<Notification>>> getNotifications() {
        return notifications;
    }

    public MutableLiveData<Boolean> getUserAuthenticationResult() {
        return userAuthenticationResult;
    }

    public void followUser(String idTokenLoggedUser, String idTokenFollowedUser){
        userRepository.followUser(idTokenLoggedUser, idTokenFollowedUser);
    }

    public void unfollowUser(String idTokenLoggedUser, String idTokenFollowedUser){
        userRepository.unfollowUser(idTokenLoggedUser, idTokenFollowedUser);
    }

    public MutableLiveData<Result> getOtherUserLiveData(){
        if(otherUserLiveData == null){
            otherUserLiveData = userRepository.getOtherUserLiveData();
        }
        return otherUserLiveData;
    }

    public void fetchOtherUser(String otherUserIdToken){
        userRepository.fetchOtherUser(otherUserIdToken);
    }

    public void signInWithGoogle(String idToken){
        userRepository.signInWithGoogle(idToken);
        if(!isUIRunning) {
            setUIRunning(true);
        }
    }

    public boolean isFirstLoading() {
        return firstLoading;
    }

    public void setFirstLoading(boolean firstLoading) {
        this.firstLoading = firstLoading;
    }

    public MutableLiveData<Boolean> getLogoutResult() {
        if(logoutResult == null){
            logoutResult = userRepository.getLogoutResult();
        }
        return logoutResult;
    }

    public void logout(){
        userRepository.logout();
    }

    public boolean isContinueRegistrationFirstLoading() {
        return continueRegistrationFirstLoading;
    }

    public void setContinueRegistrationFirstLoading(boolean continueRegistrationFirstLoading) {
        this.continueRegistrationFirstLoading = continueRegistrationFirstLoading;
    }

    public void resetAuthenticationResult() {
        userRepository.resetAuthenticationResult();
    }

    public void isUsernameAvailable(String username){
        userRepository.isUsernameAvailable(username);
    }

    public void userAuthentication(String email, String password){
        userRepository.userAuthentication(email, password);
    }

    public void deleteUserInfo(){
        userRepository.deleteUserInfo();
    }

    public void resetPasswordErrorResult() {
        userRepository.resetPasswordErrorResult();
    }
}