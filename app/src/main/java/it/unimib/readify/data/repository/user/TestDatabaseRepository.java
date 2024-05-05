package it.unimib.readify.data.repository.user;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unimib.readify.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.readify.data.source.user.BaseUserDataRemoteDataSource;
import it.unimib.readify.data.source.user.UserAuthenticationRemoteDataSource;
import it.unimib.readify.data.source.user.UserDataRemoteDataSource;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.Comment;
import it.unimib.readify.model.ExternalUser;
import it.unimib.readify.model.Notification;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.SharedPreferencesUtil;

public class TestDatabaseRepository implements TestIDatabaseRepository, UserResponseCallback {

    private final BaseUserAuthenticationRemoteDataSource userAuthRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;

    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<List<Result>> userSearchResultsLiveData;
    private final MutableLiveData<List<Result>> commentListLiveData;
    private final MutableLiveData<List<Result>> loggedUserCollectionListLiveData;
    private final MutableLiveData<List<Result>> otherUserCollectionListLiveData;
    private final MutableLiveData<List<Result>> followerListLiveData;
    private final MutableLiveData<List<Result>> followingListLiveData;
    private final MutableLiveData<Result> otherUserLiveData;
    private final MutableLiveData<String> sourceUsernameError;
    private final MutableLiveData<String> sourceEmailError;
    private final MutableLiveData<Boolean> sourcePasswordError;
    private final MutableLiveData<HashMap<String, ArrayList<Notification>>> fetchedNotifications;

    public static TestIDatabaseRepository getInstance(Application application) {
        return new TestDatabaseRepository(new UserAuthenticationRemoteDataSource(),
                new UserDataRemoteDataSource(new SharedPreferencesUtil(application)));
    }

    private TestDatabaseRepository(BaseUserAuthenticationRemoteDataSource userAuthRemoteDataSource,
                                  BaseUserDataRemoteDataSource userDataRemoteDataSource) {
        this.userAuthRemoteDataSource = userAuthRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userSearchResultsLiveData = new MutableLiveData<>();
        this.commentListLiveData = new MutableLiveData<>();
        this.loggedUserCollectionListLiveData = new MutableLiveData<>();
        this.otherUserCollectionListLiveData = new MutableLiveData<>();
        this.followerListLiveData = new MutableLiveData<>();
        this.followingListLiveData = new MutableLiveData<>();
        this.otherUserLiveData = new MutableLiveData<>();
        this.sourceUsernameError = new MutableLiveData<>();
        this.sourceEmailError = new MutableLiveData<>();
        this.sourcePasswordError = new MutableLiveData<>();
        this.fetchedNotifications = new MutableLiveData<>();
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

    public void testSet(Result result) {
        userMutableLiveData.postValue(result);
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
    public void updateUserData(User user, String newPassword) {
        userDataRemoteDataSource.updateUserData(user);
        if(newPassword != null) {
            userAuthRemoteDataSource.changePassword(newPassword);
        }
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
    public void setUserFollowing(User user) {
        userDataRemoteDataSource.setFollowing(user);
    }

    @Override
    public void setUserFollowers(User user) {
        userDataRemoteDataSource.setFollowers(user);
    }

    @Override
    public void fetchNotifications(String idToken) {
        userDataRemoteDataSource.fetchNotifications(idToken);
    }

    @Override
    public void completeNotificationsFetch(HashMap<String, ArrayList<Notification>> notifications) {
        userDataRemoteDataSource.completeNotificationsFetch(notifications);
    }

    @Override
    public void setNotificationsList(String idToken, String content, HashMap<String, ArrayList<Notification>> notifications) {
        userDataRemoteDataSource.setViewedNotificationsListToRead(idToken, content, notifications);
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
    public void searchUsers(String query){
        Log.d("UserRepository", "Query: " + query);
        userDataRemoteDataSource.searchUsers(query);
    }

    @Override
    public MutableLiveData<List<Result>> getCommentListLiveData() {
        Log.d("Repository", "getCommentsListLiveData");
        return commentListLiveData;
    }

    @Override
    public MutableLiveData<List<Result>> getLoggedUserCollectionListLiveData() {
        return loggedUserCollectionListLiveData;
    }

    @Override
    public MutableLiveData<List<Result>> getOtherUserCollectionListLiveData() {
        return otherUserCollectionListLiveData;
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
        Log.d("Repository", "fetch comments : START");
        userDataRemoteDataSource.fetchComments(bookId);
    }

    @Override
    public void fetchLoggedUserCollections(String idToken) {
        userDataRemoteDataSource.fetchLoggedUserCollections(idToken);
    }

    @Override
    public void fetchOtherUserCollections(String otherUserIdToken) {
        userDataRemoteDataSource.fetchOtherUserCollections(otherUserIdToken);
    }

    @Override
    public MutableLiveData<String> getSourceUsernameError() {
        return sourceUsernameError;
    }

    @Override
    public MutableLiveData<String> getSourceEmailError() {
        return sourceEmailError;
    }

    @Override
    public MutableLiveData<Boolean> getSourcePasswordError() {
        return sourcePasswordError;
    }

    @Override
    public MutableLiveData<HashMap<String, ArrayList<Notification>>> getFetchedNotifications() {
        return fetchedNotifications;
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserSuccess result = new Result.UserSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(List<User> searchResults) {
        List<Result> resultList = new ArrayList<>();
        for(User user : searchResults){
            Log.d("UserRepository", "User: " + user);
            resultList.add(new Result.UserSuccess(user));
        }
        userSearchResultsLiveData.postValue(resultList);
    }

    @Override
    public void onFailureFromRemoteDatabaseUser(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFetchCommentsFromRemoteDatabase(List<Comment> comments) {
        Log.d("Repository", "fetchComments result OK");
        List<Result> commentResultList = new ArrayList<>();
        for(Comment comment : comments){
            commentResultList.add(new Result.CommentSuccess(comment));
        }
        Log.d("Repository", "fetchComments result --> " + commentResultList);
        commentListLiveData.postValue(commentResultList);
    }

    @Override
    public void onFailureFetchCommentsFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        List<Result> commentResultList = new ArrayList<>();
        commentResultList.add(result);
        commentListLiveData.setValue(commentResultList);
    }

    @Override
    public void onSuccessFetchFollowersFromRemoteDatabase(List<ExternalUser> followerList) {
        List<Result> followersResultList = new ArrayList<>();
        for(ExternalUser follower : followerList){
            followersResultList.add(new Result.ExternalUserSuccess(follower));
        }
        followerListLiveData.postValue(followersResultList);
    }

    @Override
    public void onFailureFetchFollowersFromRemoteDatabase(String message) {
        //TODO
    }

    @Override
    public void onSuccessFetchFollowingFromRemoteDatabase(List<ExternalUser> followingList) {
        List<Result> followingResultList = new ArrayList<>();
        for(ExternalUser following : followingList){
            followingResultList.add(new Result.ExternalUserSuccess(following));
        }
        followingListLiveData.postValue(followingResultList);
    }

    @Override
    public void onFailureFetchFollowingFromRemoteDatabase(String message) {
        //TODO
    }

    @Override
    public void addComment(String content, String bookId, String idToken){
        userDataRemoteDataSource.addComment(content,bookId,idToken);
    }

    @Override
    public void deleteComment(String bookId, Comment comment) {
        userDataRemoteDataSource.deleteComment(bookId, comment);
    }

    @Override
    public void createCollection(String idToken, String collectionName, boolean visible) {
        userDataRemoteDataSource.createCollection(idToken, collectionName, visible);
    }

    @Override
    public void deleteCollection(String idToken, String collectionId) {
        userDataRemoteDataSource.deleteCollection(idToken, collectionId);
    }

    @Override
    public void addBookToCollection(String idToken, String bookId, String collectionId) {
        userDataRemoteDataSource.addBookToCollection(idToken, bookId, collectionId);
    }

    @Override
    public void removeBookFromCollection(String idToken, String bookId, String collectionId) {
        userDataRemoteDataSource.removeBookFromCollection(idToken, bookId, collectionId);
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
        Log.d("REPO", "followButtonClick premuto con idtoken: " + idTokenLoggedUser);
    }

    @Override
    public void unfollowUser(String idTokenLoggedUser, String idTokenFollowedUser) {
        userDataRemoteDataSource.unfollowUser(idTokenLoggedUser, idTokenFollowedUser);
        Log.d("REPO", "unfollowButtonClick premuto con idtoken: " + idTokenLoggedUser);
    }

    @Override
    public void fetchOtherUser(String otherUserIdToken) {
        userDataRemoteDataSource.fetchOtherUser(otherUserIdToken);
    }

    @Override
    public void renameCollection(String loggedUserIdToken, String collectionId, String newCollectionName) {
        userDataRemoteDataSource.renameCollection(loggedUserIdToken, collectionId, newCollectionName);
    }

    @Override
    public void onAddCommentResult(Comment comment) {
        if(comment == null){
            //todo error
        }
    }

    @Override
    public void onCreateCollectionResult(Collection collection) {

    }

    @Override
    public void onFetchOtherUserResult(User otherUser) {
        Result otherUserResult = new Result.UserSuccess(otherUser);
        otherUserLiveData.postValue(otherUserResult);
    }

    @Override
    public void onDeleteCollectionResult() {

    }

    @Override
    public void onAddBookToCollectionResult(List<String> books) {                                                                                                                                                                   

    }

    @Override
    public void onDeleteCommentResult() {

    }

    @Override
    public void onUserFollowResult() {

    }

    @Override
    public void onUserUnfollowResult() {

    }

    @Override
    public void onSuccessFetchLoggedUserCollectionsFromRemoteDatabase(List<Collection> collections) {
        List<Result> collectionResultList = new ArrayList<>();
        for(Collection collection : collections){
            collectionResultList.add(new Result.CollectionSuccess(collection));
        }
        loggedUserCollectionListLiveData.postValue(collectionResultList);
    }

    @Override
    public void onFailureFetchLoggedUserCollectionsFromRemoteDatabase(String message) {

    }

    @Override
    public void onSuccessFetchOtherUserCollectionsFromRemoteDatabase(List<Collection> collections) {
        List<Result> collectionResultList = new ArrayList<>();
        for(Collection collection : collections){
            collectionResultList.add(new Result.CollectionSuccess(collection));
        }
        otherUserCollectionListLiveData.postValue(collectionResultList);
    }

    @Override
    public void onFailureFetchOtherUserCollectionsFromRemoteDatabase(String message) {

    }

    @Override
    public void onSuccessLogout() {

    }

    @Override
    public void onUsernameAvailable(String result) {
        sourceUsernameError.postValue(result);
    }

    @Override
    public void onEmailAvailable(String result) {
        sourceEmailError.postValue(result);
    }

    @Override
    public void onPasswordChanged(Boolean result) {
        sourcePasswordError.postValue(result);
    }

    @Override
    public void onSuccessFetchNotifications(HashMap<String, ArrayList<Notification>> notifications) {
        this.fetchedNotifications.postValue(notifications);
    }

    @Override
    public void onFailureFetchNotifications(String message) {
        Log.d("onFailureFetchNotifications", message);
    }

    @Override
    public void onSuccessCompleteFetchNotifications(HashMap<String, ArrayList<Notification>> notifications) {
        fetchedNotifications.postValue(notifications);
    }

    @Override
    public void onFailureCompleteFetchNotifications(String message) {
        Log.d("onFailureCompleteFetchNotification", message);
    }
}