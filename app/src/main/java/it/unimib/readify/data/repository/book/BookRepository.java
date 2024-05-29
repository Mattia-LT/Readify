package it.unimib.readify.data.repository.book;

import static it.unimib.readify.util.Constants.COLLECTION;
import static it.unimib.readify.util.Constants.RECENT;
import static it.unimib.readify.util.Constants.TRENDING;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import it.unimib.readify.data.source.book.BaseBookRemoteDataSource;
import it.unimib.readify.data.source.book.BookRemoteDataSource;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.util.DataEncryptionUtil;
import it.unimib.readify.util.SharedPreferencesUtil;

public class BookRepository implements IBookRepository, BookResponseCallback {

    private final MutableLiveData<Result> workApiResponseLiveData;
    private final MutableLiveData<List<Result>> searchResultsLiveData;
    private final MutableLiveData<List<Result>> recommendedBooksLiveData;
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
        recommendedBooksLiveData = new MutableLiveData<>();
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
        Log.d("generi", String.valueOf(genres));
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

    @Override
    public void loadRecommendedBooks(Map<String, Integer> recommendedGenres) {
        int size = 6;
        int threshold = 3;
        List<String> recommendedKeys = recommendedGenres
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(size)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        int counter = 0;
        List<String> finalRecommendedKeys = new ArrayList<>();
        for(String key : recommendedKeys){
            if(Objects.requireNonNull(recommendedGenres.get(key)) < threshold){
                finalRecommendedKeys.add(recommendedKeys.get(counter));
                counter++;
            } else {
                finalRecommendedKeys.add(key);
            }
        }

        Collections.sort(finalRecommendedKeys);

        Log.d("REPOSITORY", "recommendedKeys" + recommendedKeys);
        Log.d("REPOSITORY", "finalRecommendedKeys" + finalRecommendedKeys);

        bookRemoteDataSource.getSuggestedBooks(finalRecommendedKeys);

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
        }
        return null;
    }

    @Override
    public MutableLiveData<List<Result>> getRecommendedBooksLiveData() {
        return recommendedBooksLiveData;
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
        recentBooksLiveData.postValue(errorList);
        trendingBooksLiveData.postValue(errorList);
    }

    @Override
    public void onSuccessLoadRecommended(List<OLWorkApiResponse> recommendedBookList) {
        List<Result> resultList = new ArrayList<>();
        for(OLWorkApiResponse work : recommendedBookList){
            resultList.add(new Result.WorkSuccess(work));
        }
        recommendedBooksLiveData.postValue(resultList);
    }
}
