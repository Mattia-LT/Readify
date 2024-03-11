package it.unimib.readify.data.repository.book;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;

/**
 * Interface to send data from Repositories to Activity/Fragment.
 */
public interface BookResponseCallback {
    /*
        Object come parametro, così da gestire diversi output d'uscita
        (abbiamo più tipi di chiamate)
     */
    void onSuccessSearchFromRemote(List<OLWorkApiResponse> searchApiResponse);
    void onSuccessFetchBookFromRemote(OLWorkApiResponse workApiResponse);
    void onSuccessFetchBooksFromRemote(List<OLWorkApiResponse> workApiResponseList, String reference);
    void onFailureFromRemote(Exception exception);
}
