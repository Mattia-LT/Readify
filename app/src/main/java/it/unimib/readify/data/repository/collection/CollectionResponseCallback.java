package it.unimib.readify.data.repository.collection;

import java.util.List;

import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;

public interface CollectionResponseCallback {
    void onSuccessFetchCompleteCollectionsFromRemote(List<Collection> collectionList, String reference);

    void onSuccessFetchCollectionsFromLocal(List<Collection> collectionList);
    void onSuccessInsertCollectionFromLocal(List<Collection> collectionList);
    void onSuccessDeleteCollectionFromLocal(Collection deletedCollection);
    void onFailureDeleteCollectionFromLocal(String errorMessage);
    void onSuccessDeleteCollectionFromRemote(Collection deletedCollection);
    void onFailureDeleteCollectionFromRemote(String errorMessage);
    void onCreateCollectionResult(Collection collection);
    void onSuccessAddBookToCollectionFromRemote(String collectionId, OLWorkApiResponse book);
    void onSuccessUpdateCollectionFromLocal();
    void onFailureUpdateCollectionFromLocal(String message);
    void onSuccessAddBookToCollectionFromLocal();
    void onSuccessRemoveBookFromCollectionFromLocal();
    void onFailureAddBookToCollectionFromLocal(String message);
    void onFailureRemoveBookFromCollectionFromLocal(String message);
    void onSuccessRemoveBookFromCollectionFromRemote(String collectionId, String bookId);
    void onFailureRemoveBookFromCollectionFromRemote(String message);
    void onFailureAddBookToCollectionFromRemote(String message);
    void onSuccessDeleteAllCollectionsFromLocal();
    void onFailureDeleteAllCollectionsFromLocal(String message);


    void onSuccessFetchLoggedUserCollectionsFromRemoteDatabase(List <Collection> collections);
    void onFailureFetchLoggedUserCollectionsFromRemoteDatabase(String message);
    void onSuccessFetchOtherUserCollectionsFromRemoteDatabase(List <Collection> collections);
    void onFailureFetchOtherUserCollectionsFromRemoteDatabase(String message);
}
