package it.unimib.readify.data.repository.book;

import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Map;

import it.unimib.readify.model.Result;

public interface IBookRepository {
    void searchBooks(String query, String sort, int limit, int offset, String genres);
    void getBooksByIdList(List<String> idList, String reference);
    void loadRecommendedBooks(Map<String, Integer> recommendedGenres);
    void loadTrendingBooks();
    void loadRecentBooks();
    void resetCarousels();
    MutableLiveData<List<Result>> getSearchResultsLiveData();
    MutableLiveData<List<Result>> getRecommendedBooksLiveData();
    MutableLiveData<List<Result>> getTrendingBooksLiveData();
    MutableLiveData<List<Result>> getRecentBooksLiveData();
}
