package it.unimib.readify.viewmodel;

import static it.unimib.readify.util.Constants.COLLECTION;
import static it.unimib.readify.util.Constants.RECENT;
import static it.unimib.readify.util.Constants.SUGGESTED;
import static it.unimib.readify.util.Constants.TRENDING;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.data.repository.book.IBookRepository;

public class BookViewModel extends ViewModel {

    /*
        todo implement correct MutableLiveData initializations and managing
         @trendingCarouselLiveData and @recentCarouselLiveData shall remain similar as of now
         (changed a little bit)
        the others have to be changed:
            @suggestedCarouselLiveData has to be dynamic while user is online
            @searchResultsLiveData CAN be "dynamic" IF we want to memorize previous searches
            @collectionResultsLiveData has to be dynamic
     */
    private final IBookRepository bookRepository;
    private final int offset;
    private final int limit;
    private MutableLiveData<List<Result>> suggestedCarouselLiveData;
    private MutableLiveData<List<Result>> trendingCarouselLiveData;
    private MutableLiveData<List<Result>> recentCarouselLiveData;
    private MutableLiveData<List<Result>> searchResultsLiveData;

    /*
        todo do we need a copy of userCollections to manage updates of data in database?
         (as user in databaseViewModel)
     */
    private MutableLiveData<List<Collection>> userCollections;

    public BookViewModel(IBookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.offset = 0;
        this.limit = 10;
        userCollections = bookRepository.getFetchedCollections();
    }

    public MutableLiveData<List<Result>> getSearchResultsLiveData(){
        if(searchResultsLiveData == null){
            searchResultsLiveData = bookRepository.getSearchResultsLiveData();
        }
        return searchResultsLiveData;
    }

    public void searchBooks(String query, String sort, String subjects) {
        bookRepository.searchBooks(query, sort, limit, offset, subjects);
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

    public void fetchCollections(List<Collection> collections, LifecycleOwner lifecycleOwner) {
        bookRepository.fetchCollections(collections, lifecycleOwner);
    }

    public MutableLiveData<List<Collection>> getFetchedCollections()  {
        return userCollections;
    }
}
