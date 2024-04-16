package it.unimib.readify.data.repository.user;

import java.util.List;

import it.unimib.readify.model.Collection;
import it.unimib.readify.model.Comment;
import it.unimib.readify.model.ExternalUser;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.User;

public interface UserResponseCallback {
    void onSuccessFromAuthentication(User user);
    void onFailureFromAuthentication(String message);
    void onSuccessFromRemoteDatabase(User user);
    void onSuccessFromRemoteDatabase(OLWorkApiResponse work);
    void onSuccessFromRemoteDatabase(List<User> searchResults);
    void onFailureFromRemoteDatabaseUser(String message);
    void onFailureFromRemoteDatabaseWork(String message);
    void onSuccessFetchCommentsFromRemoteDatabase(List<Comment> comments);
    void onFailureFetchCommentsFromRemoteDatabase(String message);
    void onSuccessFetchFollowersFromRemoteDatabase(List<ExternalUser> followerList);
    void onFailureFetchFollowersFromRemoteDatabase(String message);
    void onSuccessFetchFollowingFromRemoteDatabase(List<ExternalUser> followingList);
    void onFailureFetchFollowingFromRemoteDatabase(String message);
    void onAddCommentResult(Comment comment);
    void onCreateCollectionResult(Collection collection);
    void onDeleteCollectionResult();
    void onAddBookToCollectionResult(List<String> books);
    void onDeleteCommentResult();
    void onSuccessFetchCollectionsFromRemoteDatabase(List <Collection> collections);
    void onFailureFetchCollectionsFromRemoteDatabase(String message);
    void onSuccessLogout();
    void onUsernameAvailable(String result);
    void onEmailAvailable(String result);
}