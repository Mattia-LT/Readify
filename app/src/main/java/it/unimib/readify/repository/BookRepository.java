package it.unimib.readify.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.database.BookDao;
import it.unimib.readify.database.BookRoomDatabase;
import it.unimib.readify.model.OLDocs;
import it.unimib.readify.model.OLSearchApiResponse;
import it.unimib.readify.service.OLSearchApiService;
import it.unimib.readify.service.OLWorkApiService;
import it.unimib.readify.util.ResponseCallback;
import it.unimib.readify.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRepository implements IBookRepository{

    private final Application application;
    private final OLSearchApiService olSearchApiService;
    private final OLWorkApiService olWorkApiService;
    //private final BookDao bookDao;
    private final ResponseCallback responseCallback;


    public BookRepository(Application application, ResponseCallback responseCallback) {
        this.application = application;
        this.olWorkApiService = ServiceLocator.getInstance().getOLWorkApiService();
        this.olSearchApiService = ServiceLocator.getInstance().getOLSearchApiService();
        //BookRoomDatabase bookRoomDatabase = ServiceLocator.getInstance().getBookDao(application);
        //this.bookDao = bookRoomDatabase.bookDao();
        this.responseCallback = responseCallback;
    }

    @Override
    public void searchBooks(String query, String sort, int limit, int offset) {

        //todo forse sort potrebbe essere un enum
        //chiamata alla search
        Call <OLSearchApiResponse> bookListResponseCall = olSearchApiService.searchBooks(query, sort, limit, offset);
        bookListResponseCall.enqueue(new Callback<OLSearchApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLSearchApiResponse> call,
                                   @NonNull Response<OLSearchApiResponse> response) {
                Log.e("URL", response.toString());
                Log.e("isSuccessful", ""+response.isSuccessful());
                Log.e("body null", ""+ (response.body() != null));
                //todo capire perche non funziona
                Log.e("nunFoundEsatto", ""+!response.body().isNumFoundExact());
                //&& !response.body().isNumFoundExact()
                if (response.body() != null && response.isSuccessful() ) {
                    List<OLDocs> docsList = response.body().getDocs();

                    List<OLWorkApiResponse> outputList = new ArrayList<OLWorkApiResponse>();

                    for(OLDocs docs : docsList){
                        //per ogni key di docs facciamo la chiamata al relativo work
                        String key = docs.getKey();
                        fetchBook(key);
                        //outputList.add(fetchBook(key));


                    }
                    //todo mostrali o salvali o convertili in qualcosa
                    responseCallback.onSuccess(outputList);
                } else {
                    responseCallback.onFailure(application.getString(R.string.book_search_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<OLSearchApiResponse> call, @NonNull Throwable t) {
                responseCallback.onFailure(t.getMessage());
            }
        });
    }



    @Override
    public void fetchBook(String id) {
        Call <OLWorkApiResponse> bookResponseCall = olWorkApiService.fetchBook(id);
        bookResponseCall.enqueue(new Callback<OLWorkApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLWorkApiResponse> call, @NonNull Response<OLWorkApiResponse> response) {
                Log.e("URL-Work", response.toString());

                if (response.body() != null && response.isSuccessful()){
                    //todo
                    Log.e("fetch", "entrato");
                    Log.e("bodyfetch",response.body().toString());
                    responseCallback.onWorkSuccess(response.body());
                } else {
                    responseCallback.onFailure(application.getString(R.string.book_search_error));
                }
            }

            @Override
            public void onFailure(@NonNull Call<OLWorkApiResponse> call, @NonNull Throwable t) {
                Log.e("errore failure", "errore singolo work");
            }
        });
    }

}
