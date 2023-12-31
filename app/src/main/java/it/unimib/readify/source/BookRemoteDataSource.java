package it.unimib.readify.source;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import it.unimib.readify.R;
import it.unimib.readify.model.OLAuthorApiResponse;
import it.unimib.readify.model.OLAuthorKeys;
import it.unimib.readify.model.OLDescription;
import it.unimib.readify.model.OLDocs;
import it.unimib.readify.model.OLSearchApiResponse;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.service.OLApiService;
import it.unimib.readify.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRemoteDataSource extends BaseBookRemoteDataSource{

    private final OLApiService olApiService;
    private final Application application;
    public BookRemoteDataSource(Application application){
        this.olApiService = ServiceLocator.getInstance().getOLApiService();
        this.application = application;
    }

    @Override
    public void searchBooks(String query, String sort, int limit, int offset) {

        //todo forse sort potrebbe essere un enum
        Call<OLSearchApiResponse> bookListResponseCall = olApiService.searchBooks(query, sort, limit, offset);
        bookListResponseCall.enqueue(new Callback<OLSearchApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLSearchApiResponse> call,
                                   @NonNull Response<OLSearchApiResponse> searchResponse) {
                //todo capire perche non funziona
                //Log.e("numFoundEsatto", ""+!searchResponse.body().isNumFoundExact());
                //&& !response.body().isNumFoundExact()
                if (searchResponse.body() != null && searchResponse.isSuccessful() ) {
                    List<OLDocs> docsList = searchResponse.body().getDocs();
                    if (!docsList.isEmpty()) {
                        fetchWorkFromDocs(docsList, searchResponse);
                    } else {
                        responseCallback.onFailureFromRemote(new Exception(application.getApplicationContext().getString(R.string.book_search_error)));
                    }
                } else {
                    responseCallback.onFailureFromRemote(new Exception(application.getApplicationContext().getString(R.string.book_search_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<OLSearchApiResponse> call, @NonNull Throwable t) {
                responseCallback.onFailureFromRemote(new Exception(application.getApplicationContext().getString(R.string.book_search_error)));
            }
        });
    }

    private void fetchWorkFromDocs(List<OLDocs> docsList, Response<OLSearchApiResponse> searchResponse){
        List<OLWorkApiResponse> workList = new ArrayList<>();

        AtomicInteger worksFetched = new AtomicInteger(0);
        for(OLDocs docs : docsList){
            String key = docs.getKey();
            Log.d("work-key docs",key);
            Call <OLWorkApiResponse> bookResponseCall = olApiService.fetchBook(key);
            Log.d("work-CALL-URL", bookResponseCall.request().url().toString());
            bookResponseCall.enqueue(new Callback<OLWorkApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<OLWorkApiResponse> call, @NonNull Response<OLWorkApiResponse> workResponse) {
                    Log.i("work-OK, on response" + worksFetched.get(), workResponse.body().getTitle().toString());
                    Log.i("work-body ", searchResponse.body().toString());
                    Log.i("work-key", key);

                    if (workResponse.body() != null && workResponse.isSuccessful()){

                        if(workResponse.body().getFirstPublishDate() == null){
                            workResponse.body().setFirstPublishDate("N/A");
                        }


                        if(workResponse.body().getDescription() == null){
                            String type = "/type/text";
                            String value = application.getApplicationContext().getString(R.string.description_not_available);
                            workResponse.body().setDescription(new OLDescription(type, value));
                        }

                        fetchAuthorsFromDocs(workResponse.body().getAuthors(), workResponse);


                        workList.add(workResponse.body());
                    } else {
                        responseCallback.onFailureFromRemote(new Exception(application.getApplicationContext().getString(R.string.book_search_error)));
                    }

                    // controllo che tutti i work siano stati recuperati
                    if(worksFetched.incrementAndGet() == docsList.size()){
                        if(searchResponse.body() != null){
                            searchResponse.body().setWorkList(workList);
                            responseCallback.onSuccessFromRemote(searchResponse.body());
                        } else {
                            responseCallback.onFailureFromRemote(new Exception(application.getApplicationContext().getString(R.string.book_search_error)));
                        }
                    }
                }
                @Override
                public void onFailure(@NonNull Call<OLWorkApiResponse> call, @NonNull Throwable t) {
                    //Log.e("errore on failure " + worksFetched.get(), searchResponse.body().toString());
                    Log.e("work-errore on failure " + worksFetched.get(), searchResponse.body().getDocs().get(worksFetched.get()).getKey());
                    Log.e("work-body ", searchResponse.body().toString());
                    Log.e("work-key", key);

                    responseCallback.onFailureFromRemote(new Exception(application.getApplicationContext().getString(R.string.book_search_error)));
                }
            });

        }
    }



    private void fetchAuthorsFromDocs(List<OLAuthorKeys> authorKeysList, Response<OLWorkApiResponse> workResponse){

        List<OLAuthorApiResponse> authorList = new ArrayList<>();
        AtomicInteger authorsFetched = new AtomicInteger(0);

        for(OLAuthorKeys authorKey : authorKeysList){
            String key = authorKey.getAuthor().getKey();
            Call <OLAuthorApiResponse> authorResponseCall = olApiService.fetchAuthor(key);
            Log.d("author-key", key);
            Log.d("author-CALL-URL", authorResponseCall.request().url().toString());
            authorResponseCall.enqueue(new Callback<OLAuthorApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<OLAuthorApiResponse> call, @NonNull Response<OLAuthorApiResponse> authorResponse) {

                    Log.i("author-OK, on response" + authorsFetched.get(), authorResponse.body().getName());
                    Log.i("author-body ", authorResponse.body().toString());
                    Log.i("author-key", key);

                    if (authorResponse.body() != null && authorResponse.isSuccessful()){
                        authorList.add(authorResponse.body());
                    } else {
                        responseCallback.onFailureFromRemote(new Exception(application.getApplicationContext().getString(R.string.book_search_error)));
                    }

                    // controllo che tutti i work siano stati recuperati
                    if(authorsFetched.incrementAndGet() == authorKeysList.size()){
                        if(workResponse.body() != null){
                            workResponse.body().setAuthorList(authorList);
                            //todo dubbio
                            responseCallback.onSuccessFromRemote(workResponse);
                        } else {
                            responseCallback.onFailureFromRemote(new Exception(application.getApplicationContext().getString(R.string.book_search_error)));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<OLAuthorApiResponse> call, @NonNull Throwable t) {
                    responseCallback.onFailureFromRemote(new Exception(application.getApplicationContext().getString(R.string.book_search_error)));

                }
            });

        }
    }
    @Override
    public void fetchBook(String id) {
        Call <OLWorkApiResponse> bookResponseCall = olApiService.fetchBook(id);
        bookResponseCall.enqueue(new Callback<OLWorkApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLWorkApiResponse> call, @NonNull Response<OLWorkApiResponse> response) {
                if (response.body() != null && response.isSuccessful()){

                    if(response.body().getFirstPublishDate() == null){
                        response.body().setFirstPublishDate("N/A");
                    }


                    if(response.body().getDescription() == null){
                        String type = "/type/text";
                        String value = application.getApplicationContext().getString(R.string.description_not_available);
                        response.body().setDescription(new OLDescription(type, value));
                    }

                    fetchAuthorsFromDocs(response.body().getAuthors(), response);

                    responseCallback.onSuccessFromRemote(response.body());
                } else {
                    responseCallback.onFailureFromRemote(new Exception(application.getApplicationContext().getString(R.string.book_search_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<OLWorkApiResponse> call, @NonNull Throwable t) {
                responseCallback.onFailureFromRemote(new Exception(application.getApplicationContext().getString(R.string.book_search_error)));
            }
        });
    }
}
