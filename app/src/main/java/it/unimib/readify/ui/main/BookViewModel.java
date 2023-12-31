package it.unimib.readify.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.repository.IBookRepository;

public class BookViewModel extends ViewModel {

    private final IBookRepository bookRepository;
    private final int offset;
    private final int limit;
    private MutableLiveData<Result> searchLiveData;
    private MutableLiveData<Result> workLiveData;

    public BookViewModel(IBookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.offset = 0;
        this.limit = 10;
    }

    public MutableLiveData<Result> searchBooks(String query, String sort) {
       if(searchLiveData == null){
           searchLiveData = bookRepository.searchBooks(query, sort, limit, offset);
       }
       return searchLiveData;
    }

    public MutableLiveData<Result> fetchBook(String id){
        if(workLiveData == null){
            workLiveData = bookRepository.fetchBook(id);
        }
        return workLiveData;
    }

    public MutableLiveData<List<Result>> fetchBooks(List<String> idList) {
        MutableLiveData<List<Result>> resultLiveData = new MutableLiveData<>();
        List<Result> resultList = new ArrayList<>();

        for (String bookId : idList) {
            LiveData<Result> bookLiveData = bookRepository.fetchBook(bookId);
            bookLiveData.observeForever(new Observer<Result>() {
                @Override
                public void onChanged(Result book) {
                    // Add the book to the result list
                    resultList.add(book);

                    // If all books have been fetched, update the result LiveData
                    if (resultList.size() == idList.size()) {
                        resultLiveData.setValue(resultList);
                    }
                }
            });
        }

        return resultLiveData;
    }
}
