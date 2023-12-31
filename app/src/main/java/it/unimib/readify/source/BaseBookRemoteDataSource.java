package it.unimib.readify.source;

import java.util.List;

import it.unimib.readify.util.ResponseCallback;

public abstract class BaseBookRemoteDataSource {
    protected ResponseCallback responseCallback;
    public void setResponseCallback(ResponseCallback responseCallback) {
        this.responseCallback = responseCallback;
    }
    public abstract void searchBooks(String query, String sort, int limit, int offset);
    public abstract void fetchBook(String id);
}
