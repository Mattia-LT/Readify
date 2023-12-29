package it.unimib.readify.source;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.model.OLDocs;
import it.unimib.readify.model.OLSearchApiResponse;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.service.OLSearchApiService;
import it.unimib.readify.service.OLWorkApiService;
import it.unimib.readify.util.ServiceLocator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookRemoteDataSource extends BaseBookRemoteDataSource{

    private final OLSearchApiService olSearchApiService;
    private final OLWorkApiService olWorkApiService;
    private final List<OLWorkApiResponse> list;

    public BookRemoteDataSource(){
        this.olSearchApiService = ServiceLocator.getInstance().getOLSearchApiService();
        this.olWorkApiService = ServiceLocator.getInstance().getOLWorkApiService();
        list = new ArrayList<>();
    }

    @Override
    public void searchBooks(String query, String sort, int limit, int offset) {

        //todo forse sort potrebbe essere un enum
        Call<OLSearchApiResponse> bookListResponseCall = olSearchApiService.searchBooks(query, sort, limit, offset);
        bookListResponseCall.enqueue(new Callback<OLSearchApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLSearchApiResponse> call,
                                   @NonNull Response<OLSearchApiResponse> response) {
                //todo capire perche non funziona
                Log.e("numFoundEsatto", ""+!response.body().isNumFoundExact());
                //&& !response.body().isNumFoundExact()
                if (response.body() != null && response.isSuccessful() ) {
                    List<OLDocs> docsList = response.body().getDocs();

                    for(OLDocs docs : docsList){
                        String key = docs.getKey();
                        fetchBook(key);
                    }
                } else {
                    responseCallback.onFailureFromRemote(new Exception(Resources.getSystem().getString(R.string.book_search_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<OLSearchApiResponse> call, @NonNull Throwable t) {
                responseCallback.onFailureFromRemote(new Exception(Resources.getSystem().getString(R.string.book_search_error)));
            }
        });
    }

    @Override
    public void fetchBook(String id) {
        Call <OLWorkApiResponse> bookResponseCall = olWorkApiService.fetchBook(id);
        bookResponseCall.enqueue(new Callback<OLWorkApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<OLWorkApiResponse> call, @NonNull Response<OLWorkApiResponse> response) {
                if (response.body() != null && response.isSuccessful()){
                    responseCallback.onSuccessFromRemote(response.body());
                } else {
                    responseCallback.onFailureFromRemote(new Exception(Resources.getSystem().getString(R.string.book_search_error)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<OLWorkApiResponse> call, @NonNull Throwable t) {
                responseCallback.onFailureFromRemote(new Exception(Resources.getSystem().getString(R.string.book_search_error)));
            }
        });
    }
}
