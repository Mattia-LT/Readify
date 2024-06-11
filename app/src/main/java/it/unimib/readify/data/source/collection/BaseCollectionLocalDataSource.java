package it.unimib.readify.data.source.collection;

import java.util.List;

import it.unimib.readify.data.repository.collection.CollectionResponseCallback;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;

public abstract class BaseCollectionLocalDataSource {
    protected CollectionResponseCallback collectionResponseCallback;
    public void setResponseCallback(CollectionResponseCallback collectionResponseCallback) {
        this.collectionResponseCallback = collectionResponseCallback;
    }
    public abstract void initLocalCollections(List<Collection> collectionList);
    public abstract void getAllCollections();
    public abstract void insertCollectionList(List<Collection> collectionList);
    public abstract void updateCollection(Collection collection, String operation);
    public abstract void deleteCollection(Collection collection);
    public abstract void deleteAllCollections();
    public abstract void addBookToCollection(String collectionId, OLWorkApiResponse book);
    public abstract void removeBookFromCollection(String collectionId, String bookId);
    public abstract void renameCollection(String collectionId, String newCollectionName);
    public abstract void changeCollectionVisibility(String collectionId, boolean isCollectionVisible);
}
