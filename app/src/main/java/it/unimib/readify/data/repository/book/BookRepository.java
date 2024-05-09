package it.unimib.readify.data.repository.book;

import static it.unimib.readify.util.Constants.COLLECTION;
import static it.unimib.readify.util.Constants.RECENT;
import static it.unimib.readify.util.Constants.SEARCH;
import static it.unimib.readify.util.Constants.SUGGESTED;
import static it.unimib.readify.util.Constants.TRENDING;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.data.database.BookRoomDatabase;
import it.unimib.readify.data.source.book.BaseBookLocalDataSource;
import it.unimib.readify.data.source.book.BookLocalDataSource;
import it.unimib.readify.data.source.book.BookRemoteDataSource;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.data.source.book.BaseBookRemoteDataSource;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.util.SharedPreferencesUtil;

public class BookRepository implements IBookRepository, BookResponseCallback {

    private final MutableLiveData<Result> workApiResponseLiveData;
    private final MutableLiveData<List<Result>> searchResultsLiveData;
    private final MutableLiveData<List<Result>> suggestedBooksLiveData;
    private final MutableLiveData<List<Result>> recentBooksLiveData;
    private final MutableLiveData<List<Result>> trendingBooksLiveData;
    private final BaseBookRemoteDataSource bookRemoteDataSource;
    private final BaseBookLocalDataSource bookLocalDataSource;

    private final MutableLiveData<List<Result>> fetchedCollectionsLiveData;
    private final MutableLiveData<List<Result>> singleCollectionLiveData;

    public static BookRepository getInstance(Application application, BookRoomDatabase bookRoomDatabase, SharedPreferencesUtil sharedPreferencesUtil, DataEncryptionUtil dataEncryptionUtil) {
        return new BookRepository(
                new BookRemoteDataSource(application),
                new BookLocalDataSource(bookRoomDatabase, sharedPreferencesUtil, dataEncryptionUtil)
        );
    }

    public BookRepository(BaseBookRemoteDataSource bookRemoteDataSource, BaseBookLocalDataSource bookLocalDataSource) {
        searchResultsLiveData = new MutableLiveData<>();
        workApiResponseLiveData = new MutableLiveData<>();
        suggestedBooksLiveData = new MutableLiveData<>();
        recentBooksLiveData = new MutableLiveData<>();
        trendingBooksLiveData = new MutableLiveData<>();
        fetchedCollectionsLiveData = new MutableLiveData<>();
        singleCollectionLiveData = new MutableLiveData<>();
        this.bookRemoteDataSource = bookRemoteDataSource;
        this.bookRemoteDataSource.setResponseCallback(this);
        this.bookLocalDataSource = bookLocalDataSource;
        this.bookLocalDataSource.setResponseCallback(this);
    }

    @Override
    public void searchBooks(String query, String sort, int limit, int offset, String genres) {
        bookRemoteDataSource.searchBooks(query, sort, limit, offset, genres);
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
    public void fetchWorksForCollections(List<Collection> collections) {
        bookRemoteDataSource.fetchWorksForCollections(collections);
    }

    @Override
    public MutableLiveData<List<Result>> getFetchedCollectionsLiveData() {
        return fetchedCollectionsLiveData;
    }

    @Override
    public void onSuccessSearchFromRemote(List<OLWorkApiResponse> searchApiResponse) {

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
            case SEARCH:
                searchResultsLiveData.postValue(resultList);
                break;
            case COLLECTION:
                singleCollectionLiveData.postValue(resultList);
                break;
        }
    }

    @Override
    public void onSuccessFetchCollectionsFromRemote(List<Collection> collectionList) {
        List<Result> resultList = new ArrayList<>();
        for(Collection collection : collectionList){
            resultList.add(new Result.CollectionSuccess(collection));
        }
        fetchedCollectionsLiveData.postValue(resultList);
    }

    @Override
    public void onSuccessFetchCollectionsFromLocal(List<Collection> collectionList) {
        Log.e("COLLEZIONI LOCALI FETCHATE", collectionList.toString());
    }

    @Override
    public void onSuccessInsertCollectionFromLocal(List<Collection> collectionList) {

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

    public MutableLiveData<List<Result>> getSingleCollectionLiveData() {
        return singleCollectionLiveData;
    }
}
