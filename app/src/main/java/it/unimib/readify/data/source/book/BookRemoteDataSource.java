package it.unimib.readify.data.source.book;

import static it.unimib.readify.util.Constants.SEARCH;

import android.app.Application;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

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
                        getBooks(idList, SEARCH);
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
                                responseCallback.onSuccessFetchBooksFromRemote(books, reference);
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
                }
            });
        }
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
                    }
                });
            }
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
