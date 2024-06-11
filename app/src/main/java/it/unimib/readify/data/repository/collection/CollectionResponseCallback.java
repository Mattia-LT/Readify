package it.unimib.readify.data.repository.collection;

import java.util.List;

import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;

public interface CollectionResponseCallback {
    void onSuccessFetchCompleteCollectionsFromRemote(List<Collection> collectionList, String reference);
    void onFailureFetchCompleteCollectionsFromRemote(String message);
    void onSuccessFetchCollectionsFromLocal(List<Collection> collectionList);
    void onFailureFetchCollectionsFromLocal(String message);
    void onSuccessInsertCollectionFromLocal(List<Collection> collectionList);
    void onFailureInsertCollectionFromLocal(String message);
    void onSuccessDeleteCollectionFromLocal(Collection deletedCollection);
    void onFailureDeleteCollectionFromLocal(String errorMessage);
    void onSuccessDeleteCollectionFromRemote(Collection deletedCollection);
    void onFailureDeleteCollectionFromRemote(String errorMessage);
    void onSuccessCreateCollectionFromRemote(Collection collection);
    void onFailureCreateCollectionFromRemote(String errorMessage);
    void onSuccessAddBookToCollectionFromRemote(String collectionId, OLWorkApiResponse book);
    void onFailureAddBookToCollectionFromRemote(String message);
    void onSuccessAddBookToCollectionFromLocal();
    void onFailureAddBookToCollectionFromLocal(String message);
    void onSuccessRemoveBookFromCollectionFromLocal();
    void onFailureRemoveBookFromCollectionFromLocal(String message);
    void onSuccessRemoveBookFromCollectionFromRemote(String collectionId, String bookId);
    void onFailureRemoveBookFromCollectionFromRemote(String message);
    void onSuccessDeleteAllCollectionsFromLocal();
    void onFailureDeleteAllCollectionsFromLocal(String message);
    void onSuccessFetchLoggedUserCollectionsFromRemoteDatabase(List <Collection> collections);
    void onFailureFetchLoggedUserCollectionsFromRemoteDatabase(String message);
    void onSuccessFetchOtherUserCollectionsFromRemoteDatabase(List <Collection> collections);
    void onFailureFetchOtherUserCollectionsFromRemoteDatabase(String message);
    void onSuccessChangeCollectionVisibilityFromRemote(String collectionId, boolean isCollectionVisible);
    void onFailureChangeCollectionVisibilityFromRemote(String message);
    void onSuccessChangeCollectionVisibilityFromLocal();
    void onFailureChangeCollectionVisibilityFromLocal(String message);
    void onSuccessRenameCollectionFromRemote(String collectionId, String newCollectionName);
    void onFailureRenameCollectionFromRemote(String message);
    void onSuccessRenameCollectionFromLocal();
    void onFailureRenameCollectionFromLocal(String message);
    void onFailureFetchRating(String message);
    void onFailureFetchAuthors(String message);
    void onFailureFetchSingleWork(String message);
    void onFailureFetchSingleCollection(String message);
}
