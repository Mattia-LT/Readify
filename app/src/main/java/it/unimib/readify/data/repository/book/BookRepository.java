package it.unimib.readify.data.repository.book;

import static it.unimib.readify.util.Constants.COLLECTION;
import static it.unimib.readify.util.Constants.RECENT;
import static it.unimib.readify.util.Constants.SEARCH;
import static it.unimib.readify.util.Constants.SUGGESTED;
import static it.unimib.readify.util.Constants.TRENDING;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.data.source.book.BookRemoteDataSource;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.data.source.book.BaseBookRemoteDataSource;

public class BookRepository implements IBookRepository, BookResponseCallback {

    private final MutableLiveData<Result> workApiResponseLiveData;
    private final MutableLiveData<List<Result>> searchResultsLiveData;
    private final MutableLiveData<List<Result>> suggestedBooksLiveData;
    private final MutableLiveData<List<Result>> recentBooksLiveData;
    private final MutableLiveData<List<Result>> trendingBooksLiveData;
    private final BaseBookRemoteDataSource bookRemoteDataSource;

    private final MutableLiveData<List<Collection>> fetchedCollections;
    private final List<MutableLiveData<List<Result>>> collectionsResultsList;

    public static BookRepository getInstance(Application application) {
        return new BookRepository(new BookRemoteDataSource(application));
    }

    public BookRepository(BaseBookRemoteDataSource bookRemoteDataSource) {
        searchResultsLiveData = new MutableLiveData<>();
        workApiResponseLiveData = new MutableLiveData<>();
        suggestedBooksLiveData = new MutableLiveData<>();
        recentBooksLiveData = new MutableLiveData<>();
        trendingBooksLiveData = new MutableLiveData<>();

        fetchedCollections = new MutableLiveData<>();
        collectionsResultsList = new ArrayList<>();
        this.bookRemoteDataSource = bookRemoteDataSource;
        this.bookRemoteDataSource.setResponseCallback(this);
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

    //todo undo separation of methods, most surely getBooksByIdList() can be used
    public void fetchCollections(List<Collection> collections, LifecycleOwner lifecycleOwner) {
        List<Collection> tempList = new ArrayList<>();
        final int[] counter = {0};
        Observer<List<Result>> observer = fetchedWorks -> {
            for (int i = 0; i < fetchedWorks.size(); i++) {
                if(fetchedWorks.get(i).isSuccess()) {
                    OLWorkApiResponse work = ((Result.WorkSuccess)fetchedWorks.get(i)).getData();
                    collections.get(counter[0]).getWorks().add(work);
                }
            }
            tempList.add(collections.get(counter[0]));
            if(tempList.size() == collections.size()) {
                fetchedCollections.postValue(tempList);
            }
            counter[0]++;
            //todo managing observer deletion (where?)
        };
        //TODO, IF AGGIUNTO PER NON FAR CRASHARE L'APP --> RIVEDERE LOGICA DELLE API DI OPENLIBRARY
        // non si dovrebbe usare il viewlifecycle owner in classi che non siano fragment
        for (int i = 0; i < collections.size(); i++) {
            collectionsResultsList.add(new MutableLiveData<>());
            if(collections.get(i).getBooks() != null){
                bookRemoteDataSource.getBooks(collections.get(i).getBooks(), COLLECTION);
            }
            collectionsResultsList.get(i).observe(lifecycleOwner, observer);
        }
    }

    public MutableLiveData<List<Collection>> getFetchedCollections() {
        return fetchedCollections;
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
                for (MutableLiveData<List<Result>> list: collectionsResultsList) {
                    //todo it unexpectedly works, will it always work?
                    if(list.getValue() == null) {
                        list.postValue(resultList);
                        break;
                    }
                }
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
