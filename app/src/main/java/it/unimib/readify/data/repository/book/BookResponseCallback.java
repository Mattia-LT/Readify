package it.unimib.readify.data.repository.book;

import java.util.List;

import it.unimib.readify.model.OLWorkApiResponse;

public interface BookResponseCallback {
    void onSuccessFetchBooksFromRemote(List<OLWorkApiResponse> workApiResponseList, String reference);
    void onFailureFetchBooksFromRemote(String message, String reference);
    void onSuccessLoadRecommendedList(List<String> recommendedIdList);
    void onFailureLoadRecommendedList(String message);
    void onSuccessLoadTrendingList(List<String> trendingIdList);
    void onFailureLoadTrendingList(String message);
    void onSuccessLoadRecentList(List<String> recentIdList);
    void onFailureLoadRecentList(String message);
    void onSuccessLoadSearchResultList(List<String> searchResultList, int numFound);
    void onFailureLoadSearchResultList(String message);
    void onFailureFetchRating(String message);
    void onFailureFetchAuthors(String message);

}
