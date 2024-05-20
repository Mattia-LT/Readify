package it.unimib.readify.data.repository.book;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.readify.model.Result;

public interface IBookRepository {

    void searchBooks(String query, String sort, int limit, int offset, String genres);
    MutableLiveData<List<Result>> getSearchResultsLiveData();
    MutableLiveData<List<Result>> getBooksByIdList(List<String> idList, String reference);

}
