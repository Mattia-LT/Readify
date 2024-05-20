package it.unimib.readify.data.repository.book;

import static it.unimib.readify.util.Constants.COLLECTION;
import static it.unimib.readify.util.Constants.RECENT;
import static it.unimib.readify.util.Constants.SUGGESTED;
import static it.unimib.readify.util.Constants.TRENDING;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.data.source.book.BaseBookRemoteDataSource;
import it.unimib.readify.data.source.book.BookRemoteDataSource;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.util.SharedPreferencesUtil;

public class BookRepository implements IBookRepository, BookResponseCallback {

    private final MutableLiveData<Result> workApiResponseLiveData;
    private final MutableLiveData<List<Result>> searchResultsLiveData;
    private final MutableLiveData<List<Result>> suggestedBooksLiveData;
    private final MutableLiveData<List<Result>> recentBooksLiveData;
    private final MutableLiveData<List<Result>> trendingBooksLiveData;
    private final BaseBookRemoteDataSource bookRemoteDataSource;
    private final MutableLiveData<List<Result>> fetchedCollectionsLiveData;
    private final MutableLiveData<List<Result>> singleCollectionLiveData;

    private boolean firstSearch = true;
    private boolean searchLimitReached = false;
    private int currentSearchLimit = 0;

    public static BookRepository getInstance(Application application, SharedPreferencesUtil sharedPreferencesUtil, DataEncryptionUtil dataEncryptionUtil) {
        return new BookRepository(
                new BookRemoteDataSource(application));
    }

    public BookRepository(BaseBookRemoteDataSource bookRemoteDataSource) {
        searchResultsLiveData = new MutableLiveData<>();
        workApiResponseLiveData = new MutableLiveData<>();
        suggestedBooksLiveData = new MutableLiveData<>();
        recentBooksLiveData = new MutableLiveData<>();
        trendingBooksLiveData = new MutableLiveData<>();
        fetchedCollectionsLiveData = new MutableLiveData<>();
        singleCollectionLiveData = new MutableLiveData<>();
        this.bookRemoteDataSource = bookRemoteDataSource;
        this.bookRemoteDataSource.setBookResponseCallback(this);
    }

    @Override
    public void searchBooks(String query, String sort, int limit, int offset, String genres) {
        this.firstSearch = offset == 0;
        if(firstSearch){
            searchLimitReached = false;
            bookRemoteDataSource.searchBooks(query, sort, limit, offset, genres);
        } else if(!searchLimitReached){
            if(offset + limit >= currentSearchLimit){
                searchLimitReached = true;
            }
            bookRemoteDataSource.searchBooks(query, sort, limit, offset, genres);
        } else {
            searchResultsLiveData.postValue(searchResultsLiveData.getValue());
        }
    }

    public MutableLiveData<List<Result>> getSearchResultsLiveData(){
        return searchResultsLiveData;
    }


    @Override
    public MutableLiveData<List<Result>> getBooksByIdList(List<String> idList, String reference) {
        bookRemoteDataSource.getBooks(idList, reference);
        switch (reference){
            case TRENDING:
                return trendingBooksLiveData;
            case RECENT:
                return recentBooksLiveData;
            case SUGGESTED:
                return suggestedBooksLiveData;
        }
        return null;
    }

    @Override
    public void onSuccessSearchFromRemote(List<OLWorkApiResponse> workApiResponseList, int totalNumber) {
        List<Result> resultList = new ArrayList<>();
        for(OLWorkApiResponse work : workApiResponseList) {
            resultList.add(new Result.WorkSuccess(work));
        }
        List<Result> currentSearchResultList = new ArrayList<>();
        if(firstSearch){
            this.currentSearchLimit = totalNumber;
            currentSearchResultList = resultList;
        } else {
            if (searchResultsLiveData.getValue() != null) {
                currentSearchResultList.addAll(searchResultsLiveData.getValue());
                currentSearchResultList.addAll(resultList);
            }
        }
        searchResultsLiveData.postValue(currentSearchResultList);
    }

    @Override
    public void onSuccessFetchBookFromRemote(OLWorkApiResponse workApiResponse) {
        workApiResponseLiveData.postValue(new Result.WorkSuccess(workApiResponse));
    }

    @Override
    public void onSuccessFetchBooksFromRemote(List<OLWorkApiResponse> workApiResponseList, String reference) {
        List<Result> resultList = new ArrayList<>();
        for(OLWorkApiResponse work : workApiResponseList){
            resultList.add(new Result.WorkSuccess(work));
        }
        switch (reference){
            case TRENDING:
                trendingBooksLiveData.postValue(resultList);
                break;
            case RECENT:
                recentBooksLiveData.postValue(resultList);
                break;
            case SUGGESTED:
                suggestedBooksLiveData.postValue(resultList);
                break;
            case COLLECTION:
                singleCollectionLiveData.postValue(resultList);
                break;
        }
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        List<Result> errorList = new ArrayList<>();
        //todo dividi vari errori in metodi diversi nel callback
        errorList.add(new Result.Error(exception.getMessage()));
        searchResultsLiveData.postValue(errorList);
        workApiResponseLiveData.postValue(new Result.Error(exception.getMessage()));
        suggestedBooksLiveData.postValue(errorList);
        recentBooksLiveData.postValue(errorList);
        trendingBooksLiveData.postValue(errorList);
    }
}
