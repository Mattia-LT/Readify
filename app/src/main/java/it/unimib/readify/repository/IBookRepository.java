package it.unimib.readify.repository;

import androidx.lifecycle.MutableLiveData;

import it.unimib.readify.model.Book;
import it.unimib.readify.model.Result;

public interface IBookRepository {

    MutableLiveData<Result> fetchBooks(String title, int page, int limit);

    MutableLiveData<Result> getFavoriteBooks();

    void updateBooks(Book book);

    void deleteFavoriteBook();
}
