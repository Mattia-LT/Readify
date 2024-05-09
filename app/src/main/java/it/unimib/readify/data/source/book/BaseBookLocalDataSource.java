package it.unimib.readify.data.source.book;

import java.util.List;

import it.unimib.readify.data.repository.book.BookResponseCallback;
import it.unimib.readify.model.Collection;

public abstract class BaseBookLocalDataSource {

    protected BookResponseCallback bookResponseCallback;

    public void setResponseCallback(BookResponseCallback bookResponseCallback) {
        this.bookResponseCallback = bookResponseCallback;
    }

    public abstract void getAllCollections();
    public abstract void insertCollectionList(List<Collection> collectionList);
    public abstract void updateCollection(Collection collection);
    public abstract void deleteCollection(Collection collection);
    public abstract void deleteAllCollections();


}
