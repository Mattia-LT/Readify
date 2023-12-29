package it.unimib.readify.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import it.unimib.readify.model.Result;
import it.unimib.readify.repository.IBookRepository;

public class BooksViewModel extends ViewModel {

    private final IBookRepository booksRepository;
    private final int offset;
    private final int limit;
    private MutableLiveData<Result> booksList;

    public BooksViewModel(IBookRepository booksRepository) {
        this.booksRepository = booksRepository;
        this.offset = 0;
        this.limit = 10;
    }

    public MutableLiveData<Result> searchBooks(String query, String sort) {
        booksRepository.searchBooks(query, sort, limit, offset);
        return booksList;
    }

    public MutableLiveData<Result> fetchBook(String id) {
        booksRepository.fetchBook(id);
        return booksList;
    }
}
