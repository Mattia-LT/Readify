package it.unimib.readify.data.source.collection;

import java.util.List;

import it.unimib.readify.data.repository.collection.CollectionResponseCallback;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;

public abstract class BaseCollectionRemoteDataSource {

    protected CollectionResponseCallback collectionResponseCallback;

    public void setCollectionResponseCallback(CollectionResponseCallback collectionResponseCallback) {
        this.collectionResponseCallback = collectionResponseCallback;
    }

    public abstract void fetchWorksForCollections(List<Collection> collections, String reference);
    public abstract void createCollection(String idToken, String name, boolean visible);
    public abstract void deleteCollection(String idToken, Collection collectionToDelete);
    public abstract void addBookToCollection(String idToken, OLWorkApiResponse book, String collectionId);
    public abstract void removeBookFromCollection(String idToken, String collectionId, String bookId);
    public abstract void fetchLoggedUserCollections(String idToken);
    public abstract void fetchOtherUserCollections(String otherUserIdToken);
    public abstract void renameCollection(String loggedUserIdToken, String collectionId, String newCollectionName);


}
