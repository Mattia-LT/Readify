package it.unimib.readify.data.repository.user;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.readify.data.source.user.BaseUserDataRemoteDataSource;
import it.unimib.readify.data.source.user.UserAuthenticationRemoteDataSource;
import it.unimib.readify.data.source.user.UserDataRemoteDataSource;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.Comment;
import it.unimib.readify.model.ExternalUser;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.SharedPreferencesUtil;

public class TestDatabaseRepository implements TestIDatabaseRepository, UserResponseCallback {

    private final BaseUserAuthenticationRemoteDataSource userAuthRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;

    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<List<Result>> userSearchResultsLiveData;
    private final MutableLiveData<List<Result>> commentListLiveData;
    private final MutableLiveData<List<Result>> collectionListLiveData;
    private final MutableLiveData<List<Result>> followerListLiveData;
    private final MutableLiveData<List<Result>> followingListLiveData;

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
        this.collectionListLiveData = new MutableLiveData<>();
        this.followerListLiveData = new MutableLiveData<>();
        this.followingListLiveData = new MutableLiveData<>();
        this.userAuthRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
    }

    public interface UpdateUserDataCallback {
        void onUsernameAvailable(String result);
        void onEmailAvailable(String result);
        void onPasswordChanged(Boolean result);
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
    public void updateUserData(User user, String newPassword, TestDatabaseRepository.UpdateUserDataCallback callback) {
        userDataRemoteDataSource.updateUserData(user, callback);
        if(newPassword != null) {
            userAuthRemoteDataSource.changePassword(newPassword, callback);
        }
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
    public MutableLiveData<List<Result>> getCollectionListLiveData() {
        return collectionListLiveData;
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
    public MutableLiveData<List<Result>> getUserSearchResultsLiveData(){
        return userSearchResultsLiveData;
    }

    @Override
    public void fetchComments(String bookId) {
        Log.d("Repository", "fetch comments : START");
        userDataRemoteDataSource.fetchComments(bookId);
    }

    @Override
    public void fetchCollections(String idToken) {
        userDataRemoteDataSource.fetchCollections(idToken);
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
    public void onSuccessFromRemoteDatabase(OLWorkApiResponse work) {

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
    public void onFailureFromRemoteDatabaseWork(String message) {

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
    public void onAddCommentResult(Comment comment) {
        if(comment == null){
            //todo error
        }
    }

    @Override
    public void onCreateCollectionResult(Collection collection) {

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
    public void onSuccessFetchCollectionsFromRemoteDatabase(List<Collection> collections) {
        List<Result> collectionResultList = new ArrayList<>();
        for(Collection collection : collections){
            collectionResultList.add(new Result.CollectionSuccess(collection));
        }
        collectionListLiveData.postValue(collectionResultList);
    }

    @Override
    public void onFailureFetchCollectionsFromRemoteDatabase(String message) {

    }

    @Override
    public void onSuccessLogout() {

    }
}