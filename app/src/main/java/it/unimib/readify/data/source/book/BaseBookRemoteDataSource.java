package it.unimib.readify.data.source.book;

import java.util.List;

import it.unimib.readify.data.repository.book.BookResponseCallback;

public abstract class BaseBookRemoteDataSource {
    protected BookResponseCallback bookResponseCallback;
    public void setBookResponseCallback(BookResponseCallback bookResponseCallback) {
        this.bookResponseCallback = bookResponseCallback;
    }
    public abstract void searchBooks(String query, String sort, int limit, int offset, String subjects);
    public abstract void getBooks(List<String> idList, String reference);
    public abstract void getSuggestedBooks(List<String> subjects);
}
