package it.unimib.readify.repository;

import androidx.lifecycle.MutableLiveData;

import it.unimib.readify.model.Result;

public interface IBookRepository {

    MutableLiveData<Result> searchBooks(String query, String sort, int limit, int offset);

    MutableLiveData<Result> fetchBook(String id);

}
