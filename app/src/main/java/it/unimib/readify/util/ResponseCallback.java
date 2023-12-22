package it.unimib.readify.util;

import java.util.List;

import it.unimib.readify.model.OLWorkApiResponse;

/**
 * Interface to send data from Repositories to Activity/Fragment.
 */
public interface ResponseCallback {
    void onSuccess(List <OLWorkApiResponse> bookList);

    void onWorkSuccess(OLWorkApiResponse work);

    void onFailure(String errorMessage);

}
