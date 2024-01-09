package it.unimib.readify.util;

import java.util.List;

import it.unimib.readify.model.OLSearchApiResponse;
import it.unimib.readify.model.OLWorkApiResponse;

/**
 * Interface to send data from Repositories to Activity/Fragment.
 */
public interface ResponseCallback {
    /*
    Object come parametro, così da gestire diverse output d'uscita
    (abbiamo più tipi di chiamate)
     */
    void onSuccessSearchFromRemote(List<OLWorkApiResponse> searchApiResponse);

    void onSuccessFetchBookFromRemote(OLWorkApiResponse workApiResponse);
    void onSuccessFetchBooksFromRemote(List<OLWorkApiResponse> workApiResponseList, String reference);

    void onFailureFromRemote(Exception exception);
}
