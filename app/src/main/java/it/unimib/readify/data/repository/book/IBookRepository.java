package it.unimib.readify.data.repository.book;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.readify.model.Collection;
import it.unimib.readify.model.Result;

public interface IBookRepository {

    MutableLiveData<List<Result>> searchBooks(String query, String sort, int limit, int offset, String genres);
    LiveData<Result> fetchBook(String id);
    MutableLiveData<List<Result>> getBooksByIdList(List<String> idList, String reference);
    void fetchCollections(List<Collection> collections, LifecycleOwner lifecycleOwner);
    MutableLiveData<List<Collection>> getFetchedCollections();
}
