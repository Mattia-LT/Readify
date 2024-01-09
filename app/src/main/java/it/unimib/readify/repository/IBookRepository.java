package it.unimib.readify.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.readify.model.Result;

public interface IBookRepository {

    MutableLiveData<List<Result>> searchBooks(String query, String sort, int limit, int offset);

    LiveData<Result> fetchBook(String id);

    MutableLiveData<List<Result>> getBooksByIdList(List<String> idList, String reference);
}
