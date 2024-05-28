package it.unimib.readify.data.source.book;

import static it.unimib.readify.util.Constants.OL_SORT_RANDOM_DAILY;

import android.app.Application;
import android.util.Log;

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
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.data.service.OLApiService;
import it.unimib.readify.util.TestServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRemoteDataSource extends BaseBookRemoteDataSource{

    private final OLApiService olApiService;
    private final Application application;

    public BookRemoteDataSource(Application application){
        this.olApiService = TestServiceLocator.getInstance(application).getOLApiService();
        this.application = application;
    }

    @Override
    public void searchBooks(String query, String sort, int limit, int offset, String subjects) {
        olApiService.searchBooks(query, sort, limit, offset, subjects).enqueue(new Callback<OLSearchApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLSearchApiResponse> call, @NonNull Response<OLSearchApiResponse> response) {
                if(response.isSuccessful()){
                    OLSearchApiResponse searchApiResponse = response.body();
                    if(searchApiResponse != null && searchApiResponse.getDocs() != null){
                        List<OLDocs> docsList = searchApiResponse.getDocs();
                        List<String> idList = new ArrayList<>();
                        for(OLDocs doc : docsList){
                            if(doc.getKey() != null){
                                idList.add(doc.getKey());
                            }
                        }

                        List<OLWorkApiResponse> books = new ArrayList<>();
                        int totalRequests = idList.size();
                        final int[] completedRequests = {0};
                        for(String id: idList){
                            olApiService.fetchBook(id).enqueue(new Callback<OLWorkApiResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<OLWorkApiResponse> call,
                                                       @NonNull Response<OLWorkApiResponse> response) {
                                    if(response.isSuccessful()){
                                        OLWorkApiResponse book = response.body();
                                        if(book != null){
                                            checkBookData(book);
                                            fetchAuthors(book);
                                            fetchRatingForWork(book);
                                            books.add(book);
                                            completedRequests[0]++;
                                            if(books.size() == idList.size() && completedRequests[0] == totalRequests){
                                                bookResponseCallback.onSuccessSearchFromRemote(books, searchApiResponse.getNumFound());
                                            }
                                        } else {
                                            //todo gestire errore
                                        }
                                    } else {
                                        //todo gestire errore
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<OLWorkApiResponse> call, @NonNull Throwable t) {
                                    //todo gestire errori
                                    completedRequests[0]++;
                                    if(books.size() == idList.size() && completedRequests[0] == totalRequests){
                                        bookResponseCallback.onSuccessSearchFromRemote(books, searchApiResponse.getNumFound());
                                    }
                                }
                            });
                        }
                        if(idList.isEmpty()){
                            bookResponseCallback.onSuccessSearchFromRemote(books, searchApiResponse.getNumFound());
                        }
                    } else{
                        //todo errore
                    }
                } else {
                    //todo altro errore
                }
            }

            @Override
            public void onFailure(@NonNull Call<OLSearchApiResponse> call, @NonNull Throwable t) {
                //todo altro altro errore
            }
        });
    }



    @Override
    public void getBooks(List<String> idList, String reference) {
        List<OLWorkApiResponse> books = new ArrayList<>();
        int totalRequests = idList.size();
        final int[] completedRequests = {0};
        for(String id: idList){
            olApiService.fetchBook(id).enqueue(new Callback<OLWorkApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<OLWorkApiResponse> call,
                                       @NonNull Response<OLWorkApiResponse> response) {
                    if(response.isSuccessful()){
                        OLWorkApiResponse book = response.body();
                        if(book != null){
                            checkBookData(book);
                            fetchAuthors(book);
                            fetchRatingForWork(book);
                            books.add(book);
                            completedRequests[0]++;
                            if(books.size() == idList.size() && completedRequests[0] == totalRequests){
                                bookResponseCallback.onSuccessFetchBooksFromRemote(books, reference);
                            }
                        } else {
                            //todo gestire errore
                        }
                    } else {
                        //todo gestire errore
                    }
                }

                @Override
                public void onFailure(@NonNull Call<OLWorkApiResponse> call, @NonNull Throwable t) {
                    //todo gestire errori
                    completedRequests[0]++;
                    if(books.size() == idList.size() && completedRequests[0] == totalRequests){
                        bookResponseCallback.onSuccessFetchBooksFromRemote(books, reference);
                    }
                }
            });
        }
    }

    @Override
    public void getSuggestedBooks(List<String> subjects) {
        CountDownLatch countDownLatchRecommended = new CountDownLatch(subjects.size());
        int counter = 0;
        List<OLWorkApiResponse> recommendedWorks = Collections.synchronizedList(new ArrayList<>());
        while(counter < subjects.size()){
            int subjectFrequency = Collections.frequency(subjects, subjects.get(counter));

            olApiService.fetchBookFromSubject(subjects.get(counter), OL_SORT_RANDOM_DAILY, subjectFrequency).enqueue(new Callback<OLSearchApiResponse>() {
                @Override
                public void onResponse(Call<OLSearchApiResponse> call, Response<OLSearchApiResponse> response) {
                    Log.e("URL",call.request().url().toString());
                    if(response.isSuccessful()){
                        OLSearchApiResponse searchApiResponse = response.body();
                        if(searchApiResponse != null && searchApiResponse.getDocs() != null){
                            List<OLDocs> docsList = searchApiResponse.getDocs();
                            List<String> idList = new ArrayList<>();
                            for(OLDocs doc : docsList){
                                if(doc.getKey() != null){
                                    idList.add(doc.getKey());
                                }
                            }
                            for(String bookId : idList){
                                olApiService.fetchBook(bookId).enqueue(new Callback<OLWorkApiResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<OLWorkApiResponse> call, @NonNull Response<OLWorkApiResponse> response) {
                                        if(response.isSuccessful()){
                                            OLWorkApiResponse book = response.body();
                                            if(book != null){
                                                checkBookData(book);
                                                recommendedWorks.add(book);
                                            } else {
                                                //todo gestire errore
                                            }
                                        } else {
                                            //todo errore
                                        }
                                        countDownLatchRecommended.countDown();
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<OLWorkApiResponse> call, @NonNull Throwable t) {
                                        //todo gestire eventuali errori
                                        countDownLatchRecommended.countDown();
                                    }
                                });
                            }
                        } else{
                            //todo errore
                        }
                    } else {
                        //todo altro errore
                    }
                }

                @Override
                public void onFailure(@NonNull Call<OLSearchApiResponse> call, @NonNull Throwable t) {
                    //todo altro altro errore fetch iniziale
                    for(int i = 0; i<subjectFrequency; i++){
                        countDownLatchRecommended.countDown();
                    }
                }
            });

            counter += subjectFrequency;
        }

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try{
                Log.d("fetchRecommended", "Waiting for all books...");
                countDownLatchRecommended.await();
                Log.d("fetchRecommended", "All books fetched, returning book list");

                Log.d("recommended", recommendedWorks.toString());

                CountDownLatch ratingsToFetchLatch = new CountDownLatch(recommendedWorks.size());

                for(OLWorkApiResponse book : recommendedWorks){
                    olApiService.fetchRating(book.getKey()).enqueue(new Callback<OLRatingResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<OLRatingResponse> call, @NonNull Response<OLRatingResponse> response) {
                            if(response.isSuccessful()){
                                OLRatingResponse rating = response.body();
                                if(rating != null){
                                    book.setRating(rating);
                                } else {
                                    //todo errore
                                }
                            } else {
                                //todo errore
                            }

                            ratingsToFetchLatch.countDown();
                        }

                        @Override
                        public void onFailure(@NonNull Call<OLRatingResponse> call, @NonNull Throwable t) {
                            ratingsToFetchLatch.countDown();
                        }
                    });
                }
                ratingsToFetchLatch.await();

                CountDownLatch authorsToFetchLatch = new CountDownLatch(recommendedWorks.size());
                for(OLWorkApiResponse book : recommendedWorks){

                    List<OLAuthorApiResponse> authors = new ArrayList<>();
                    List<OLAuthorKeys> authorsKeys = book.getAuthors();
                    if(authorsKeys != null && !authorsKeys.isEmpty()){
                        CountDownLatch tempAuthorsLatch = new CountDownLatch(authorsKeys.size());
                        for(OLAuthorKeys authorKey : authorsKeys) {
                            String key = null;
                            if (authorKey.getAuthor() != null) {
                                key = authorKey.getAuthor().getKey();
                            } else if (authorKey.getKey() != null) {
                                authorKey.setAuthor(new OLDocs(authorKey.getKey()));
                            }
                            if(key != null){
                                olApiService.fetchAuthor(key).enqueue(new Callback<OLAuthorApiResponse>() {
                                    @Override
                                    public void onResponse(@NonNull Call<OLAuthorApiResponse> call, @NonNull Response<OLAuthorApiResponse> response) {
                                        if(response.isSuccessful()){
                                            OLAuthorApiResponse author = response.body();
                                            if(author != null){
                                                authors.add(author);
                                            } else {
                                                //todo errore
                                            }
                                        } else {
                                            //todo errore
                                        }
                                        tempAuthorsLatch.countDown();
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<OLAuthorApiResponse> call, @NonNull Throwable t) {
                                        //todo errore
                                        tempAuthorsLatch.countDown();
                                    }
                                });
                            } else {
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
                                authorsToFetchLatch.countDown();
                            }
                        });

                    } else {
                        //todo errore
                        authorsToFetchLatch.countDown();
                    }
                }
                authorsToFetchLatch.await();

                bookResponseCallback.onSuccessLoadRecommended(recommendedWorks);
                executorService.shutdown();
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                //todo error
            }
        });

    }

    public void fetchRatingForWork(OLWorkApiResponse workApiResponse) {
        String id = workApiResponse.getKey();
        olApiService.fetchRating(id).enqueue(new Callback<OLRatingResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLRatingResponse> call, @NonNull Response<OLRatingResponse> response) {
                if(response.isSuccessful()){
                    OLRatingResponse rating = response.body();
                    if(rating != null){
                        workApiResponse.setRating(rating);
                    } else {
                        //todo errore
                    }
                } else {
                    //todo errore
                }
            }

            @Override
            public void onFailure(@NonNull Call<OLRatingResponse> call, @NonNull Throwable t) {
                //todo errore
            }
        });


    }

    private void fetchAuthors(OLWorkApiResponse book){
        List<OLAuthorApiResponse> authors = new ArrayList<>();
        List<OLAuthorKeys> authorsKeys = book.getAuthors();
        if(authorsKeys != null && !authorsKeys.isEmpty()){
            int totalRequests = authorsKeys.size();
            final int[] completedRequests = {0};
            for(OLAuthorKeys authorKey : authorsKeys) {
                String key = null;
                if(authorKey.getAuthor() != null){
                    key = authorKey.getAuthor().getKey();
                } else if (authorKey.getKey() != null){
                    key = authorKey.getKey();
                    authorKey.setAuthor(new OLDocs(key));
                }
                olApiService.fetchAuthor(key).enqueue(new Callback<OLAuthorApiResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OLAuthorApiResponse> call, @NonNull Response<OLAuthorApiResponse> response) {
                        if(response.isSuccessful()){
                            OLAuthorApiResponse author = response.body();
                            if(author != null){
                                authors.add(author);
                                completedRequests[0]++;
                                if(authors.size() == authorsKeys.size() && completedRequests[0] == totalRequests){
                                    book.setAuthorList(authors);
                                }
                            } else {
                                //todo errore
                            }
                        } else {
                            //todo errore
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OLAuthorApiResponse> call, @NonNull Throwable t) {
                        //todo errore
                        completedRequests[0]++;
                        if(authors.size() == authorsKeys.size() && completedRequests[0] == totalRequests){
                            book.setAuthorList(authors);
                        }
                    }
                });
            }
        }

    }


    private void checkBookData(OLWorkApiResponse book){
        //TODO andrebe spostato negli adapter forse in modo da poter rimuovere la application dal data source, non dovrebbe essere qui
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
