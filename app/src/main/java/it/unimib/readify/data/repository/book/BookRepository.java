package it.unimib.readify.data.repository.book;

import static it.unimib.readify.util.Constants.RECENT;
import static it.unimib.readify.util.Constants.RECOMMENDED;
import static it.unimib.readify.util.Constants.SEARCH;
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

public class BookRepository implements IBookRepository, BookResponseCallback {

    private final String TAG = BookRepository.class.getSimpleName();
    private final MutableLiveData<List<Result>> searchResultsLiveData;
    private final MutableLiveData<List<Result>> recommendedBooksLiveData;
    private final MutableLiveData<List<Result>> recentBooksLiveData;
    private final MutableLiveData<List<Result>> trendingBooksLiveData;
    private final BaseBookRemoteDataSource bookRemoteDataSource;
    private boolean firstSearch = true;
    private boolean searchLimitReached = false;
    private int currentSearchLimit = 0;

    public static BookRepository getInstance(Application application) {
        return new BookRepository(
                new BookRemoteDataSource(application));
    }

    public BookRepository(BaseBookRemoteDataSource bookRemoteDataSource) {
        searchResultsLiveData = new MutableLiveData<>();
        recommendedBooksLiveData = new MutableLiveData<>();
        recentBooksLiveData = new MutableLiveData<>();
        trendingBooksLiveData = new MutableLiveData<>();
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

    @Override
    public void loadRecommendedBooks(Map<String, Integer> recommendedGenres) {
        int size = 6;
        int minimumThreshold = 3;
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
            if(Objects.requireNonNull(recommendedGenres.get(key)) < minimumThreshold){
                finalRecommendedKeys.add(recommendedKeys.get(counter));
                counter++;
            } else {
                finalRecommendedKeys.add(key);
            }
        }
        Collections.sort(finalRecommendedKeys);
        bookRemoteDataSource.getRecommendedBooks(finalRecommendedKeys);

    }

    @Override
    public void loadTrendingBooks() {
        bookRemoteDataSource.getTrendingBooks();
    }

    @Override
    public void loadRecentBooks() {
        bookRemoteDataSource.getRecentBooks();
    }

    @Override
    public void resetCarousels() {
        trendingBooksLiveData.postValue(null);
        recentBooksLiveData.postValue(null);
        recommendedBooksLiveData.postValue(null);
    }

    public MutableLiveData<List<Result>> getSearchResultsLiveData(){
        return searchResultsLiveData;
    }

    @Override
    public void getBooksByIdList(List<String> idList, String reference) {
        bookRemoteDataSource.getBooks(idList, reference);
    }

    @Override
    public MutableLiveData<List<Result>> getRecommendedBooksLiveData() {
        return recommendedBooksLiveData;
    }

    @Override
    public MutableLiveData<List<Result>> getTrendingBooksLiveData() {
        return trendingBooksLiveData;
    }

    @Override
    public MutableLiveData<List<Result>> getRecentBooksLiveData() {
        return recentBooksLiveData;
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
            case RECOMMENDED:
                recommendedBooksLiveData.postValue(resultList);
                break;
            case SEARCH:
                List<Result> currentSearchResultList = new ArrayList<>();
                if(firstSearch){
                    currentSearchResultList = resultList;
                } else {
                    if (searchResultsLiveData.getValue() != null) {
                        currentSearchResultList.addAll(searchResultsLiveData.getValue());
                        currentSearchResultList.addAll(resultList);
                    }
                }
                searchResultsLiveData.postValue(currentSearchResultList);
                break;
        }
    }

    @Override
    public void onFailureFetchBooksFromRemote(String message, String reference) {
        switch (reference){
            case TRENDING:
            case RECENT:
            case RECOMMENDED:
            case SEARCH:
                Log.e(TAG + reference + "Section", message);
                break;
        }
    }

    @Override
    public void onSuccessLoadRecommendedList(List<String> recommendedIdList) {
        getBooksByIdList(recommendedIdList, RECOMMENDED);
    }

    @Override
    public void onFailureLoadRecommendedList(String message) {
        Log.e(TAG + " - " + RECOMMENDED, message);
    }

    @Override
    public void onSuccessLoadTrendingList(List<String> trendingIdList) {
        getBooksByIdList(trendingIdList, TRENDING);
    }

    @Override
    public void onFailureLoadTrendingList(String message) {
        Log.e(TAG + " - " + TRENDING, message);
    }

    @Override
    public void onSuccessLoadRecentList(List<String> recentIdList) {
        getBooksByIdList(recentIdList, RECENT);
    }

    @Override
    public void onFailureLoadRecentList(String message) {
        Log.e(TAG + " - " + RECENT, message);
    }

    @Override
    public void onSuccessLoadSearchResultList(List<String> searchResultList, int numFound) {
        if(firstSearch){
            this.currentSearchLimit = numFound;
        }
        getBooksByIdList(searchResultList, SEARCH);
    }

    @Override
    public void onFailureLoadSearchResultList(String message) {
        Log.e(TAG + " - " + SEARCH, message);
    }

    @Override
    public void onFailureFetchRating(String message) {
        //Only a warning, sometimes api doesn't have those information
        Log.w(TAG, message);
    }

    @Override
    public void onFailureFetchAuthors(String message) {
        //Only a warning, sometimes api doesn't have those information
        Log.w(TAG, message);
    }
}
