package it.unimib.readify.data.source.book;

import static it.unimib.readify.util.Constants.OL_RECENT_BOOKS_QUERY;
import static it.unimib.readify.util.Constants.OL_SORT_RANDOM_DAILY;
import static it.unimib.readify.util.Constants.CAROUSEL_SIZE;
import static it.unimib.readify.util.Constants.RATING_SORT_SEARCH_MODE;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.unimib.readify.R;
import it.unimib.readify.model.OLAuthorApiResponse;
import it.unimib.readify.model.OLAuthorKeys;
import it.unimib.readify.model.OLDescription;
import it.unimib.readify.model.OLDocs;
import it.unimib.readify.model.OLRatingResponse;
import it.unimib.readify.model.OLSearchApiResponse;
import it.unimib.readify.model.OLTrendingApiResponse;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.data.service.OLApiService;
import it.unimib.readify.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRemoteDataSource extends BaseBookRemoteDataSource{

    private final OLApiService olApiService;
    private final Application application;

    public BookRemoteDataSource(Application application){
        this.olApiService = ServiceLocator.getInstance(application).getOLApiService();
        this.application = application;
    }

    @Override
    public void searchBooks(String query, String sort, int limit, int offset, String subjects) {
        olApiService.searchBooks(query, sort, limit, offset, subjects).enqueue(new Callback<OLSearchApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLSearchApiResponse> call, @NonNull Response<OLSearchApiResponse> response) {
                if(response.isSuccessful()){
                    OLSearchApiResponse searchApiResponse = response.body();
                    if(searchApiResponse != null){
                        if(searchApiResponse.getDocs() != null){
                            List<OLDocs> docsList = searchApiResponse.getDocs();
                            List<String> idList = new ArrayList<>();
                            for(OLDocs doc : docsList){
                                if(doc.getKey() != null){
                                    idList.add(doc.getKey());
                                }
                            }
                            bookResponseCallback.onSuccessLoadSearchResultList(idList, searchApiResponse.getNumFound());
                        } else {
                            bookResponseCallback.onFailureLoadSearchResultList("Error in BookRemoteDataSource: searchApiResponse.getDocs() is null");
                        }
                    } else {
                        bookResponseCallback.onFailureLoadSearchResultList("Error in BookRemoteDataSource: searchApiResponse is null");
                    }
                } else {
                    bookResponseCallback.onFailureLoadSearchResultList("Error in BookRemoteDataSource: OpenLibrary response wasn't successful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<OLSearchApiResponse> call, @NonNull Throwable t) {
                bookResponseCallback.onFailureLoadSearchResultList("Error in BookRemoteDataSource: OpenLibrary Search call failed.\n" + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void getBooks(List<String> idList, String reference) {
        List<OLWorkApiResponse> fetchedWorks = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch countDownLatchFetchedWorks = new CountDownLatch(idList.size());
        for (String id : idList) {
            olApiService.fetchBook(id).enqueue(new Callback<OLWorkApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<OLWorkApiResponse> call, @NonNull Response<OLWorkApiResponse> response) {
                    if (response.isSuccessful()) {
                        OLWorkApiResponse book = response.body();
                        if (book != null) {
                            checkBookData(book);
                            fetchedWorks.add(book);
                        } else {
                            bookResponseCallback.onFailureFetchBooksFromRemote("Error in BookRemoteDataSource: Fetched book with id " + id + " is null", reference);
                        }
                    } else {
                        bookResponseCallback.onFailureFetchBooksFromRemote("Error in BookRemoteDataSource: OpenLibrary response wasn't successful", reference);
                    }
                    countDownLatchFetchedWorks.countDown();
                }

                @Override
                public void onFailure(@NonNull Call<OLWorkApiResponse> call, @NonNull Throwable t) {
                    bookResponseCallback.onFailureFetchBooksFromRemote("Error in BookRemoteDataSource: OpenLibrary fetch call with id: " + id + " failed.\n" + t.getLocalizedMessage(), reference);
                    countDownLatchFetchedWorks.countDown();
                }
            });
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                countDownLatchFetchedWorks.await();

                CountDownLatch ratingsToFetchLatch = new CountDownLatch(fetchedWorks.size());
                CountDownLatch authorsToFetchLatch = new CountDownLatch(fetchedWorks.size());

                for(OLWorkApiResponse work : fetchedWorks){
                   fetchRatings(work, ratingsToFetchLatch);
                   fetchAuthors(work, authorsToFetchLatch);
                }

                ratingsToFetchLatch.await();
                authorsToFetchLatch.await();

                bookResponseCallback.onSuccessFetchBooksFromRemote(fetchedWorks, reference);
                executorService.shutdown();

            } catch (InterruptedException e) {
                bookResponseCallback.onFailureFetchBooksFromRemote("Error in BookRemoteDataSource: " +  e.getLocalizedMessage(), reference);
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void getRecommendedBooks(List<String> subjects) {
        CountDownLatch countDownLatchRecommended = new CountDownLatch(subjects.size());
        int fetchedSubjects = 0;
        List<String> recommendedIdList = Collections.synchronizedList(new ArrayList<>());
        while(fetchedSubjects < subjects.size()){
            int subjectFrequency = Collections.frequency(subjects, subjects.get(fetchedSubjects));
            olApiService.fetchBookFromSubject(subjects.get(fetchedSubjects), OL_SORT_RANDOM_DAILY, subjectFrequency).enqueue(new Callback<OLSearchApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<OLSearchApiResponse> call, @NonNull Response<OLSearchApiResponse> response) {
                    if(response.isSuccessful()){
                        OLSearchApiResponse searchApiResponse = response.body();
                        if(searchApiResponse != null) {
                            if(searchApiResponse.getDocs() != null){
                                List<OLDocs> docsList = searchApiResponse.getDocs();
                                List<String> idList = new ArrayList<>();
                                for (OLDocs doc : docsList) {
                                    if (doc.getKey() != null) {
                                        idList.add(doc.getKey());
                                    }
                                }
                                recommendedIdList.addAll(idList);
                                for (int i = 0; i < subjectFrequency; i++) {
                                    countDownLatchRecommended.countDown();
                                }
                            } else {
                                bookResponseCallback.onFailureLoadRecommendedList("Error in BookRemoteDataSource: searchApiResponse.getDocs() is null");
                            }
                        } else{
                            bookResponseCallback.onFailureLoadRecommendedList("Error in BookRemoteDataSource: searchApiResponse is null");
                        }
                    } else {
                        bookResponseCallback.onFailureLoadRecommendedList("Error in BookRemoteDataSource: OpenLibrary response wasn't successful");
                    }
                }
                @Override
                public void onFailure(@NonNull Call<OLSearchApiResponse> call, @NonNull Throwable t) {
                    bookResponseCallback.onFailureLoadRecommendedList("Error in BookRemoteDataSource: OpenLibrary Search call failed.\n" + t.getLocalizedMessage());
                    for(int i = 0; i < subjectFrequency; i++){
                        countDownLatchRecommended.countDown();
                    }
                }
            });
            fetchedSubjects += subjectFrequency;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                countDownLatchRecommended.await();
                bookResponseCallback.onSuccessLoadRecommendedList(recommendedIdList);
            } catch (InterruptedException e) {
                bookResponseCallback.onFailureLoadRecommendedList("Error in BookRemoteDataSource: " +  e.getLocalizedMessage());
                Thread.currentThread().interrupt();
            }
        });
    }

    @Override
    public void getTrendingBooks() {
        olApiService.fetchTrendingBooks(CAROUSEL_SIZE).enqueue(new Callback<OLTrendingApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLTrendingApiResponse> call, @NonNull Response<OLTrendingApiResponse> response) {
                if(response.isSuccessful()) {
                    OLTrendingApiResponse trendingApiResponse = response.body();
                    if (trendingApiResponse != null){
                        if(trendingApiResponse.getWorks() != null){
                            List<OLDocs> workList = trendingApiResponse.getWorks();
                            List<String> idList = new ArrayList<>();
                            for (OLDocs work : workList) {
                                if (work.getKey() != null) {
                                    idList.add(work.getKey());
                                }
                            }
                            bookResponseCallback.onSuccessLoadTrendingList(idList);
                        } else {
                            bookResponseCallback.onFailureLoadTrendingList("Error in BookRemoteDataSource: trendingApiResponse.getWorks() is null");
                        }
                    } else {
                        bookResponseCallback.onFailureLoadTrendingList("Error in BookRemoteDataSource: trendingApiResponse is null");
                    }
                } else {
                    bookResponseCallback.onFailureLoadTrendingList("Error in BookRemoteDataSource: OpenLibrary response wasn't successful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<OLTrendingApiResponse> call, @NonNull Throwable t) {
                bookResponseCallback.onFailureLoadTrendingList("Error in BookRemoteDataSource: OpenLibrary Search call failed.\n" + t.getLocalizedMessage());
            }
        });
    }

    @Override
    public void getRecentBooks() {
        olApiService.fetchRecentBooks(OL_RECENT_BOOKS_QUERY,RATING_SORT_SEARCH_MODE, CAROUSEL_SIZE).enqueue(new Callback<OLSearchApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLSearchApiResponse> call, @NonNull Response<OLSearchApiResponse> response) {
                if(response.isSuccessful()) {
                    OLSearchApiResponse searchApiResponse = response.body();
                    if (searchApiResponse != null){
                        if(searchApiResponse.getDocs() != null){
                            List<OLDocs> docsList = searchApiResponse.getDocs();
                            List<String> idList = new ArrayList<>();
                            for (OLDocs doc : docsList) {
                                if (doc.getKey() != null) {
                                    idList.add(doc.getKey());
                                }
                            }
                            bookResponseCallback.onSuccessLoadRecentList(idList);
                        } else {
                            bookResponseCallback.onFailureLoadRecentList("Error in BookRemoteDataSource: searchApiResponse.getDocs() is null");
                        }
                    } else {
                        bookResponseCallback.onFailureLoadRecentList("Error in BookRemoteDataSource: searchApiResponse is null");
                    }
                } else {
                    bookResponseCallback.onFailureLoadRecentList("Error in BookRemoteDataSource: OpenLibrary response wasn't successful");
                }
            }

            @Override
            public void onFailure(@NonNull Call<OLSearchApiResponse> call, @NonNull Throwable t) {
                bookResponseCallback.onFailureLoadRecentList("Error in BookRemoteDataSource: OpenLibrary Search call failed.\n" + t.getLocalizedMessage());
            }
        });
    }

    public void fetchRatings(OLWorkApiResponse book, CountDownLatch ratingsToFetchLatch) {
        String bookId = book.getKey();
        olApiService.fetchRating(bookId).enqueue(new Callback<OLRatingResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLRatingResponse> call, @NonNull Response<OLRatingResponse> response) {
                if(response.isSuccessful()){
                    OLRatingResponse rating = response.body();
                    if(rating != null){
                        book.setRating(rating);
                    } else {
                        bookResponseCallback.onFailureFetchRating("Error in BookRemoteDataSource: the rating of the book " + book.getTitle() + " with id: " + bookId + " is null");
                    }
                } else {
                    bookResponseCallback.onFailureFetchRating("Error in BookRemoteDataSource: OpenLibrary fetch rating for book with id " + bookId + " wasn't successful");
                }
                ratingsToFetchLatch.countDown();
            }

            @Override
            public void onFailure(@NonNull Call<OLRatingResponse> call, @NonNull Throwable t) {
                bookResponseCallback.onFailureFetchRating("Error in BookRemoteDataSource: OpenLibrary fetch rating for book with id " + bookId + " failed.\n" + t.getLocalizedMessage());
                ratingsToFetchLatch.countDown();
            }
        });
    }

    private void fetchAuthors(OLWorkApiResponse book, CountDownLatch authorsToFetchLatch){
        List<OLAuthorApiResponse> authors = new ArrayList<>();
        List<OLAuthorKeys> authorsKeys = book.getAuthors();
        if(authorsKeys != null){
            if(!authorsKeys.isEmpty()){
                CountDownLatch tempAuthorsLatch = new CountDownLatch(authorsKeys.size());
                for(OLAuthorKeys authorKey : authorsKeys) {
                    String key = null;
                    if(authorKey.getAuthor() != null){
                        key = authorKey.getAuthor().getKey();
                    } else if (authorKey.getKey() != null){
                        key = authorKey.getKey();
                        authorKey.setAuthor(new OLDocs(key));
                    }
                    if(key != null){
                        String finalKey = key;
                        olApiService.fetchAuthor(key).enqueue(new Callback<OLAuthorApiResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<OLAuthorApiResponse> call, @NonNull Response<OLAuthorApiResponse> response) {
                                if(response.isSuccessful()){
                                    OLAuthorApiResponse author = response.body();
                                    if(author != null){
                                        authors.add(author);
                                    } else {
                                        bookResponseCallback.onFailureFetchAuthors("Error in BookRemoteDataSource: the author with id: " + finalKey + " of the book " + book.getTitle() + " with id: " + book.getKey() + " is null");
                                    }
                                } else {
                                    bookResponseCallback.onFailureFetchAuthors("Error in BookRemoteDataSource: OpenLibrary fetch author with id: " + finalKey + " for the book with id: " + book.getKey() + " wasn't successful");
                                }
                                tempAuthorsLatch.countDown();
                            }

                            @Override
                            public void onFailure(@NonNull Call<OLAuthorApiResponse> call, @NonNull Throwable t) {
                                bookResponseCallback.onFailureFetchAuthors("Error in BookRemoteDataSource: OpenLibrary fetch author with id: " + finalKey + " for the book with id: " + book.getKey() + " failed.\n" + t.getLocalizedMessage());
                                tempAuthorsLatch.countDown();
                            }
                        });
                    } else {
                        bookResponseCallback.onFailureFetchAuthors("Error in BookRemoteDataSource: OpenLibrary fetch author failed because the key was null");
                        tempAuthorsLatch.countDown();
                    }
                }

                ExecutorService authorExecutorService = Executors.newSingleThreadExecutor();
                authorExecutorService.submit(() -> {
                    try {
                        tempAuthorsLatch.await();
                        book.setAuthorList(authors);
                        authorsToFetchLatch.countDown();
                        authorExecutorService.shutdown();
                    } catch (InterruptedException e) {
                        bookResponseCallback.onFailureFetchAuthors("Error in BookRemoteDataSource: " +  e.getLocalizedMessage());
                        authorsToFetchLatch.countDown();
                    }
                });
            } else {
                bookResponseCallback.onFailureFetchAuthors("Error in BookRemoteDataSource: The book with the id: " + book.getKey() + "has no authors in authorKeys");
            }
        } else {
            bookResponseCallback.onFailureFetchAuthors("Error in BookRemoteDataSource: The book with the id: " + book.getKey() + " has the authorKeys field with a null value");
            authorsToFetchLatch.countDown();
        }
    }

    private void checkBookData(OLWorkApiResponse book){
        if(book.getFirstPublishDate() == null){
            book.setFirstPublishDate("N/A");
        }
        if(book.getDescription() == null){
            String type = "/type/text";
            String value = application.getApplicationContext().getString(R.string.description_not_available);
            book.setDescription(new OLDescription(type, value));
        }
    }
}
