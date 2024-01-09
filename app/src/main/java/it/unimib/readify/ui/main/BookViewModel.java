package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.RECENT;
import static it.unimib.readify.util.Constants.SUGGESTED;
import static it.unimib.readify.util.Constants.TRENDING;

import android.util.Log;

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
    private LiveData<Result> workLiveData;
    private MutableLiveData<List<Result>> suggestedCarouselLiveData;
    private MutableLiveData<List<Result>> trendingCarouselLiveData;
    private MutableLiveData<List<Result>> recentCarouselLiveData;
    private MutableLiveData<List<Result>> searchResultsLiveData;
    public BookViewModel(IBookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.offset = 0;
        this.limit = 10;
    }

    public MutableLiveData<List<Result>> searchBooks(String query, String sort) {
        searchResultsLiveData = bookRepository.searchBooks(query, sort, limit, offset);
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
            default:
                return null;
        }

    }
}