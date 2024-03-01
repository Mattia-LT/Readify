package it.unimib.readify.data.repository.book;

import static it.unimib.readify.util.Constants.COLLECTION;
import static it.unimib.readify.util.Constants.RECENT;
import static it.unimib.readify.util.Constants.SEARCH;
import static it.unimib.readify.util.Constants.SUGGESTED;
import static it.unimib.readify.util.Constants.TRENDING;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.data.source.book.BookRemoteDataSource;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.data.source.book.BaseBookRemoteDataSource;

public class BookRepository implements IBookRepository, BookResponseCallback {

    private MutableLiveData<Result> workApiResponseLiveData;
    private MutableLiveData<List<Result>> searchResultsLiveData;
    private MutableLiveData<List<Result>> suggestedBooksLiveData;
    private MutableLiveData<List<Result>> recentBooksLiveData;
    private MutableLiveData<List<Result>> trendingBooksLiveData;
    private MutableLiveData<List<Result>> collectionBooksLiveData;
    private final BaseBookRemoteDataSource bookRemoteDataSource;

    public static BookRepository getInstance(Application application) {
        return new BookRepository(new BookRemoteDataSource(application));
    }

    public BookRepository(BaseBookRemoteDataSource bookRemoteDataSource) {
        searchResultsLiveData = new MutableLiveData<>();
        workApiResponseLiveData = new MutableLiveData<>();
        suggestedBooksLiveData = new MutableLiveData<>();
        recentBooksLiveData = new MutableLiveData<>();
        trendingBooksLiveData = new MutableLiveData<>();
        collectionBooksLiveData = new MutableLiveData<>();
        this.bookRemoteDataSource = bookRemoteDataSource;
        this.bookRemoteDataSource.setResponseCallback(this);
    }

    @Override
    public MutableLiveData<List<Result>> searchBooks(String query, String sort, int limit, int offset, String subjects) {
        bookRemoteDataSource.searchBooks(query, sort, limit, offset, subjects);
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
            case COLLECTION:
                return collectionBooksLiveData;
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
            default:
                collectionBooksLiveData.postValue(resultList);
                break;
        }
    }

    @Override
    public void onFailureFromRemote(Exception exception) {
        List<Result> errorList = new ArrayList<>();
        errorList.add(new Result.Error(exception.getMessage()));
        searchResultsLiveData.postValue(errorList);
        workApiResponseLiveData.postValue(new Result.Error(exception.getMessage()));
        suggestedBooksLiveData.postValue(errorList);
        recentBooksLiveData.postValue(errorList);
        trendingBooksLiveData.postValue(errorList);
    }
}
