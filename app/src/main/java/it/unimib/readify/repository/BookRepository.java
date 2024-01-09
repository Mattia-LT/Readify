package it.unimib.readify.repository;

import static it.unimib.readify.util.Constants.RECENT;
import static it.unimib.readify.util.Constants.SEARCH;
import static it.unimib.readify.util.Constants.SUGGESTED;
import static it.unimib.readify.util.Constants.TRENDING;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.OLSearchApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.source.BaseBookRemoteDataSource;
import it.unimib.readify.util.ResponseCallback;

public class BookRepository implements IBookRepository, ResponseCallback{

    /*
    private final Application application;
    private final OLSearchApiService olSearchApiService;
    //private final BookDao bookDao;
    private final ResponseCallback responseCallback;
    */

    private MutableLiveData<Result> workApiResponseLiveData;
    private MutableLiveData<List<Result>> searchResultsLiveData;
    private MutableLiveData<List<Result>> suggestedBooksLiveData;
    private MutableLiveData<List<Result>> recentBooksLiveData;
    private MutableLiveData<List<Result>> trendingBooksLiveData;

    private final BaseBookRemoteDataSource bookRemoteDataSource;

    public BookRepository(BaseBookRemoteDataSource bookRemoteDataSource) {
        searchResultsLiveData = new MutableLiveData<>();
        workApiResponseLiveData = new MutableLiveData<>();
        suggestedBooksLiveData = new MutableLiveData<>();
        recentBooksLiveData = new MutableLiveData<>();
        trendingBooksLiveData = new MutableLiveData<>();
        this.bookRemoteDataSource = bookRemoteDataSource;
        this.bookRemoteDataSource.setResponseCallback(this);
    }

    @Override
    public MutableLiveData<List<Result>> searchBooks(String query, String sort, int limit, int offset) {
        bookRemoteDataSource.searchBooks(query, sort, limit, offset);
        return searchResultsLiveData;
    }

    @Override
    public LiveData<Result> fetchBook(String id) {
        bookRemoteDataSource.fetchBook(id);
        return workApiResponseLiveData;
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
    public void onSuccessSearchFromRemote(List<OLWorkApiResponse> searchApiResponse) {

    }

    @Override
    public void onSuccessFetchBookFromRemote(OLWorkApiResponse workApiResponse) {
        workApiResponseLiveData.postValue(new Result.WorkSuccess((OLWorkApiResponse) workApiResponse));
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
            case SEARCH:
                searchResultsLiveData.postValue(resultList);
                break;
        }
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        List<Result> errorList = new ArrayList<Result>();
        errorList.add(new Result.Error(exception.getMessage()));
        searchResultsLiveData.postValue(errorList);
        workApiResponseLiveData.postValue(new Result.Error(exception.getMessage()));
        suggestedBooksLiveData.postValue(errorList);
        recentBooksLiveData.postValue(errorList);
        trendingBooksLiveData.postValue(errorList);

    }

}
