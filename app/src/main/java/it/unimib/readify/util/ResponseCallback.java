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
    void onSuccessFromRemote(Object response);
    void onFailureFromRemote(Exception exception);
}
