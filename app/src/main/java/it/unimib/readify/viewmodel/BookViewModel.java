package it.unimib.readify.viewmodel;

import static it.unimib.readify.util.Constants.RECENT;
import static it.unimib.readify.util.Constants.TRENDING;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.Map;

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
    private final int ITEMS_PER_PAGE = 10;
    private int currentPage = 0;
    private String query;
    private String sort;
    private String subjects;


    private MutableLiveData<List<String>> subjectListLiveData;
    private MutableLiveData<String> sortModeLiveData;
    private MutableLiveData<List<Result>> recommendedCarouselLiveData;
    private MutableLiveData<List<Result>> trendingCarouselLiveData;
    private MutableLiveData<List<Result>> recentCarouselLiveData;
    private MutableLiveData<List<Result>> searchResultsLiveData;

    public BookViewModel(IBookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public MutableLiveData<List<Result>> getSearchResultsLiveData(){
        if(searchResultsLiveData == null){
            searchResultsLiveData = bookRepository.getSearchResultsLiveData();
        }
        return searchResultsLiveData;
    }

    public void searchBooks(String query, String sort, String subjects) {
        currentPage = 0;
        this.query = query;
        this.sort = sort;
        this.subjects = subjects;
        bookRepository.searchBooks(query, sort, ITEMS_PER_PAGE, 0, subjects);
    }

    // Method to load more items for pagination
    public void loadMoreSearchResults() {
        currentPage++; // Increment page number to load the next page
        bookRepository.searchBooks(query, sort, ITEMS_PER_PAGE, currentPage * ITEMS_PER_PAGE, subjects);
    }


    public MutableLiveData<List<Result>> fetchBooks(List<String> idList, String reference) {
        switch (reference){
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

    public MutableLiveData<List<String>> getSubjectListLiveData(){
        if(subjectListLiveData == null){
            subjectListLiveData = new MutableLiveData<>();
        }
        return subjectListLiveData;
    }

    public MutableLiveData<String> getSortModeLiveData(){
        if(sortModeLiveData == null){
            sortModeLiveData = new MutableLiveData<>();
        }
        return sortModeLiveData;
    }

    public void setSortMode(String sortMode){
        sortModeLiveData.postValue(sortMode);
    }

    public void setSubjectList(List<String> subjectList){
        subjectListLiveData.postValue(subjectList);
    }

    public MutableLiveData<List<Result>> getRecommendedCarouselLiveData(){
        if(recommendedCarouselLiveData == null){
            recommendedCarouselLiveData = bookRepository.getRecommendedBooksLiveData();
        }
        return recommendedCarouselLiveData;
    }

    public void loadRecommendedBooks(Map<String, Integer> recommendedGenres){
        bookRepository.loadRecommendedBooks(recommendedGenres);
    }


}
