package it.unimib.readify.viewmodel;

import android.util.Log;

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
    private final IUserRepository testDatabaseRepository;
    /*
        With this configuration
         (having two LiveData variables that memorizes data, repositoryData and copiedData),
         ViewModel can have a different version of the data in the Repository, and so in the Database.
        Even if it isn't yet known the purpose of this choice, it is the most flexible one
         for future changes and implementations.

        How to implement the copy of Database data:
        1)  Using MutableLiveData
        2)  Using MediatorLiveData
            This class can observe one or more other LiveData objects
             and propagate their changes to its set of observers.
            It can be used to create complex data update logic in the ViewModel,
             for example combining data from multiple sources or applying transformations
             to the data before exposing it to the user interface.
            This is (again) the most flexible choice for future changes and implementations;
             in addition, it is an opportunity to use a different JetPack element.
     */
    private final MutableLiveData<Result> repositoryData;
    private final MediatorLiveData<Result> copiedData = new MediatorLiveData<>();
    /*
        It can happen that the user inserts incorrect credentials
         (generating userCollisionError, invalidCredentials exceptions).
        With this implementation, WITHOUT @isUIRunning, IF the user inserts incorrect credentials
         in the Login AND AFTER moves from Login to Registration, the Registration is going
         to print the previous error made in the Login; that's because the Observer catches the update
         (@copiedData has changed to a new Result.Error instance).
        It is the same with roles reversed (Login printing a Registration error).
        @isUIRunning prevents the problem, allowing Login / Registration actions only after
         having set the data using setUserMutableLiveData(), each time a container is created.
     */
    private MutableLiveData<List<Result>> userSearchResultsLiveData;
    private MutableLiveData<List<Result>> commentListLiveData;
    private MutableLiveData<List<Result>> followersListLiveData;
    private MutableLiveData<List<Result>> followingListLiveData;
    private MutableLiveData<Result> otherUserLiveData;

    private final MutableLiveData<String> usernameAvailableResult;
    private final MutableLiveData<Boolean> sourceEmailError;
    private final MutableLiveData<Boolean> sourcePasswordError;
    private MutableLiveData<Boolean> logoutResult;
    private MutableLiveData<Boolean> userAuthenticationResult;
    private final MutableLiveData<HashMap<String, ArrayList<Notification>>> notifications;
    private boolean isUIRunning;
    private boolean firstLoading = true;
    private boolean continueRegistrationFirstLoading = true;
    public UserViewModel(IUserRepository testDatabaseRepository) {
        this.testDatabaseRepository = testDatabaseRepository;
        /*
            With the coming MutableLiveData initialization, repositoryData(1) (ViewModel)
             is pointing to the instance of userMutableLiveData(2) (Repository).
            This means that each of them will overwrite the value of the other with method postValue().
            Class MutableLiveData seems to have the same behavior as a POINTER, even though
             pointers don't exist in Java: this is an example of OBJECT REFERENCING.
            In this case, object referencing is useful: it allows the programmer to not implement
             any method that would communicate the change of data from ViewModel to Repository
             (for example)
         */
        repositoryData = testDatabaseRepository.getUserMutableLiveData();

        /*
            To allow ViewModel having a different version of the data memorized in Repository,
             it can be used another LiveData instance(3)
             which gets (only) the value from the original one (1 or 2) using postValue().
            Using (a) postValue() isn't the same as (b) initialising a LiveData with another instance:
                (a) Changes only the value of an already created instance (instance of LiveData).
                    es. copiedData.postValue(result);
                (b) Changes the reference of the current instance (object referencing)
                    es. repositoryData = testDatabaseRepository.getUserMutableLiveData();
            Using (a) is the best practice when an INDEPENDENT LiveData instance is needed.
            Nevertheless, (b) can be useful in some cases.
         */
        copiedData.addSource(repositoryData, newData -> {
            Log.d("viewModel", "data changed");
            Result result;
            if(newData.isSuccess()) {
                /*
                    Setting the value of Mutable / MediatorLiveData
                    Object referencing is a practice
                     which assets depending on the situation it is used on.
                    Regarding the assignment of the value of LiveData instances,
                     object referencing the value is:
                        1) suboptimal because some memory is going to be allocated
                            without necessity (not completely unnecessary in this case);
                        2) definitely useful because, IN THIS CASE, having the reference of the data
                            contained in the Repository, even for a brief moment, CAN be
                            a lack of security;

                    In this case, newData and result, despite being of the same class and
                     memorizing the very same data, they are two DIFFERENT instances.
                    The instance of LiveData is UNCHANGED because postValue() is used.
                 */
                result = new Result.UserSuccess(((Result.UserSuccess)newData).getData());
            } else {
                result = new Result.Error(((Result.Error)newData).getMessage());
            }
            copiedData.postValue(result);
        });
        usernameAvailableResult = testDatabaseRepository.getUsernameAvailableResult();
        sourceEmailError = testDatabaseRepository.getSourceEmailError();
        sourcePasswordError = testDatabaseRepository.getSourcePasswordError();
        notifications = testDatabaseRepository.getFetchedNotifications();
        userAuthenticationResult = testDatabaseRepository.getUserAuthenticationResult();
    }

    //new logic
    public void setUserMutableLiveData(String email, String password, boolean isRegistered) {
        testDatabaseRepository.getUser(email, password, isRegistered);
        if(!isUIRunning) {
            setUIRunning(true);
        }
    }

    public MediatorLiveData<Result> getUserMediatorLiveData() {
        return copiedData;
    }

    public void setUserMediatorLiveData(Result newData) {
        copiedData.postValue(newData);
    }

    public boolean isUIRunning() {
        return isUIRunning;
    }

    public void setUIRunning(boolean UIRunning) {
        isUIRunning = UIRunning;
    }

    /*
        Why professor use boolean authenticationError (USELESS EXPLANATION)
        (Having only this function)
            public MutableLiveData<Result> getLoggedUser(String email, String password, boolean isRegistered) {
            if(userMutableLiveData.getValue() == null) {
                userMutableLiveData = testDatabaseRepository.getUser(email, password, isRegistered);
            }
            return userMutableLiveData;
        In case the user insert incorrect credentials
        in the login / registration fragment, which are going to cause database error "invalid credentials",
        (such as insert an email that is not memorized in the Authentication Database yet), these
        (incorrect) credentials will set the value of userMutableLiveData to a Result.Error and,
        after correcting the inputs (email / password) in the login section, the getLoggedUser() method
        is not going to invoke the Repository call because the value of userMutableLiveData is not null,
        causing the system to not be able to update the userMutableLiveData variable
        in the viewModel and Repository.
        This would cause the impossibility to log into the HomeActivity

        Solutions:
        1) adding boolean authenticationError, getter / setter methods and other system logic
            --> this will update the userMutableLiveData only in the Repository and solve the problem????
        2) implementing a mechanism which will set the value of userMutableLiveData to null / new variable
            in case its value is going to be a Result.Error
            --> this will work for the system logic, but each time the system will set the decided
                base value, another observer will be created because we return
                a new instance of userMutableLiveData; that's obviously a problem

         UPDATE:
         Implementing the new logic, the problem can't happen.
     */

    public void searchUsers(String query){
        Log.d("UserViewModel", "Query: " + query);
        testDatabaseRepository.searchUsers(query);
    }

    public MutableLiveData<List<Result>> getUserSearchResultsLiveData(){
        if(userSearchResultsLiveData == null){
            userSearchResultsLiveData = testDatabaseRepository.getUserSearchResultsLiveData();
        }
        return userSearchResultsLiveData;
    }

    public MutableLiveData<List<Result>> getCommentList(){
        if(commentListLiveData == null){
            Log.d("ViewModel", "getCommentList : case NULL");
            commentListLiveData = testDatabaseRepository.getCommentListLiveData();
        } else {
            Log.d("ViewModel", "getCommentList : case OK");
        }
        return commentListLiveData;
    }


    public void fetchComments(String bookId){
        Log.d("ViewModel", "fetchComments start");
        testDatabaseRepository.fetchComments(bookId);
    }

    public void addComment(String content, String bookId, String idToken){
        testDatabaseRepository.addComment(content,bookId,idToken);
    }

    public void deleteComment(String bookId, Comment comment){
        testDatabaseRepository.deleteComment(bookId, comment);
    }

    public void changeUserPassword(String newPassword) {testDatabaseRepository.changeUserPassword(newPassword);}

    public void setUserUsername(User user) {
        testDatabaseRepository.setUserUsername(user);
    }

    public void setUserEmail(String newEmail) {
        testDatabaseRepository.setUserEmail(newEmail);
    }

    public void setUserGender(User user) {
        testDatabaseRepository.setUserGender(user);
    }

    public void setUserVisibility(User user) {
        testDatabaseRepository.setUserVisibility(user);
    }

    public void setUserRecommended(User user) {
        testDatabaseRepository.setUserRecommended(user);
    }

    public void setUserBiography(User user) {
        testDatabaseRepository.setUserBiography(user);
    }

    public void setUserAvatar(User user) {
        testDatabaseRepository.setUserAvatar(user);
    }

    public void setUserFollowing(User user) {testDatabaseRepository.setUserFollowing(user);}

    public void setUserFollowers(User user) {testDatabaseRepository.setUserFollowers(user);}
    public void setUserTotalNumberOfBooks(User user) {testDatabaseRepository.setUserTotalNumberOfBooks(user);}

    public void fetchNotifications(String idToken) {
        testDatabaseRepository.fetchNotifications(idToken);
    }

    public void completeFetchNotifications(HashMap<String, ArrayList<Notification>> notifications) {
        testDatabaseRepository.completeNotificationsFetch(notifications);
    }

    public void setNotificationsList(String idToken, String content, HashMap<String, ArrayList<Notification>> notifications) {
        testDatabaseRepository.setNotificationsList(idToken, content, notifications);
    }

    public void addNotification(String receivingIdToken, String content, String loggedUserIdToken) {
        testDatabaseRepository.addNotification(receivingIdToken, content, loggedUserIdToken);
    }

    public void removeNotification(String targetIdToken, String content, String loggedUserIdToken) {
        testDatabaseRepository.removeNotification(targetIdToken, content, loggedUserIdToken);
    }

    public void fetchFollowers(String idToken){
        testDatabaseRepository.fetchFollowers(idToken);
    }

    public void fetchFollowing(String idToken){
        testDatabaseRepository.fetchFollowing(idToken);
    }

    public MutableLiveData<List<Result>> getFollowersListLiveData() {
        if(followersListLiveData == null){
            followersListLiveData = testDatabaseRepository.getFollowersListLiveData();
        }
        return followersListLiveData;
    }

    public MutableLiveData<List<Result>> getFollowingListLiveData() {
        if(followingListLiveData == null){
            followingListLiveData = testDatabaseRepository.getFollowingListLiveData();
        }
        return followingListLiveData;
    }

    public MutableLiveData<String> getUsernameAvailableResult() {
        return usernameAvailableResult;
    }

    public MutableLiveData<Boolean> getSourceEmailError() {
        return sourceEmailError;
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
        testDatabaseRepository.followUser(idTokenLoggedUser, idTokenFollowedUser);
        Log.d("ViewModel", "followButtonClick premuto con idtoken: " + idTokenLoggedUser);
    }
    public void unfollowUser(String idTokenLoggedUser, String idTokenFollowedUser){
        testDatabaseRepository.unfollowUser(idTokenLoggedUser, idTokenFollowedUser);
        Log.d("ViewModel", "unfollowButtonClick premuto con idtoken: " + idTokenLoggedUser);
    }

    public MutableLiveData<Result> getOtherUserLiveData(){
        if(otherUserLiveData == null){
            otherUserLiveData = testDatabaseRepository.getOtherUserLiveData();
        }
        return otherUserLiveData;
    }

    public void fetchOtherUser(String otherUserIdToken){
        testDatabaseRepository.fetchOtherUser(otherUserIdToken);
    }

    public void signInWithGoogle(String idToken){
        testDatabaseRepository.signInWithGoogle(idToken);
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
            logoutResult = testDatabaseRepository.getLogoutResult();
        }
        return logoutResult;
    }

    public void logout(){
        testDatabaseRepository.logout();
    }

    public boolean isContinueRegistrationFirstLoading() {
        return continueRegistrationFirstLoading;
    }

    public void setContinueRegistrationFirstLoading(boolean continueRegistrationFirstLoading) {
        this.continueRegistrationFirstLoading = continueRegistrationFirstLoading;
    }

    public void isUsernameAvailable(String username){
        testDatabaseRepository.isUsernameAvailable(username);
    }

    public void userAuthentication(String email, String password){
        testDatabaseRepository.userAuthentication(email, password);
    }


}