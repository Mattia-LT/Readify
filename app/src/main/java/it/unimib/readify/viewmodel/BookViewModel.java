package it.unimib.readify.viewmodel;

import static it.unimib.readify.util.Constants.COLLECTION;
import static it.unimib.readify.util.Constants.RECENT;
import static it.unimib.readify.util.Constants.SUGGESTED;
import static it.unimib.readify.util.Constants.TRENDING;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import it.unimib.readify.model.Result;
import it.unimib.readify.data.repository.book.IBookRepository;

public class BookViewModel extends ViewModel {

    private final IBookRepository bookRepository;
    private final int offset;
    private final int limit;
    private LiveData<Result> workLiveData;
    private MutableLiveData<List<Result>> suggestedCarouselLiveData;
    private MutableLiveData<List<Result>> trendingCarouselLiveData;
    private MutableLiveData<List<Result>> recentCarouselLiveData;
    private MutableLiveData<List<Result>> searchResultsLiveData;
    private MutableLiveData<List<Result>> collectionResultsLiveData;

    public BookViewModel(IBookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.offset = 0;
        this.limit = 10;
    }

    public MutableLiveData<List<Result>> searchBooks(String query, String sort, String subjects) {
        searchResultsLiveData = bookRepository.searchBooks(query, sort, limit, offset, subjects);
        return searchResultsLiveData;
    }

    public LiveData<Result> fetchBook(String id){
        if(workLiveData == null){
            workLiveData = bookRepository.fetchBook(id);
        }
        return workLiveData;
    }

    public MutableLiveData<List<Result>> fetchBooks(List<String> idList, String reference) {
        switch (reference){
            case SUGGESTED:
                if(suggestedCarouselLiveData == null){
                    suggestedCarouselLiveData = bookRepository.getBooksByIdList(idList, reference);
                }
                return suggestedCarouselLiveData;
            case TRENDING:
                if(trendingCarouselLiveData == null){
                    trendingCarouselLiveData = bookRepository.getBooksByIdList(idList, reference);
                }
                return trendingCarouselLiveData;
            case RECENT:
                if(recentCarouselLiveData == null){
                    recentCarouselLiveData = bookRepository.getBooksByIdList(idList, reference);
                }
                return recentCarouselLiveData;
            case COLLECTION:
                if(collectionResultsLiveData == null)
                    collectionResultsLiveData = bookRepository.getBooksByIdList(idList, reference);
                return collectionResultsLiveData;
            default:
                return null;
        }

    }
}
