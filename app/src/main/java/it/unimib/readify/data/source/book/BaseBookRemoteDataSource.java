package it.unimib.readify.data.source.book;

import java.util.List;

import it.unimib.readify.data.repository.book.BookResponseCallback;
import it.unimib.readify.model.Collection;

public abstract class BaseBookRemoteDataSource {
    protected BookResponseCallback responseCallback;
    public void setResponseCallback(BookResponseCallback bookResponseCallback) {
        this.responseCallback = bookResponseCallback;
    }
    public abstract void searchBooks(String query, String sort, int limit, int offset, String subjects);
    public abstract void getBooks(List<String> idList, String reference);
    public abstract void fetchWorksForCollections(List<Collection> collections);
}
