package it.unimib.readify.data.source.user;


import it.unimib.readify.data.repository.user.TestDatabaseRepository;
import it.unimib.readify.data.repository.user.UserResponseCallback;
import it.unimib.readify.model.Comment;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.User;

public abstract class BaseUserDataRemoteDataSource {
    protected UserResponseCallback userResponseCallback;

    public void setUserResponseCallback(UserResponseCallback userResponseCallback) {
        this.userResponseCallback = userResponseCallback;
    }

    public abstract void saveUserData(User user);
    public abstract void updateUserData(User user, TestDatabaseRepository.UpdateUserDataCallback callback);
    public abstract void onUsernameAvailable(User user, TestDatabaseRepository.UpdateUserDataCallback callback);
    public abstract void onEmailAvailable(User user, TestDatabaseRepository.UpdateUserDataCallback callback);
    public abstract void saveWorkData(OLWorkApiResponse work);
    public abstract void getUser(String idToken);
    public abstract void getWork(String idBook);
    public abstract void searchUsers(String query);
    public abstract void fetchComments(String bookId);
    public abstract void addComment(String content, String bookId, String idToken);
    public abstract void createCollection(String idToken, String name, boolean visible);
    public abstract void deleteCollection(String idToken, String collectionId);
    public abstract void addBookToCollection(String idToken, String collectionId, String bookId);
    public abstract void removeBookFromCollection(String idToken, String collectionId, String bookId);
    public abstract void deleteComment(String bookId, Comment comment);
    public abstract void fetchCollections(String idToken);
    public abstract void fetchFollowers(String idToken);
    public abstract void fetchFollowings(String idToken);


    //se salviamo le user preferences sul dispositivo (che sembra più sensato), non ci servono
    public abstract void getUserPreferences(String idToken);
    public abstract void saveUserPreferences(String message, String idToken);
}