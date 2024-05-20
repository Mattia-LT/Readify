package it.unimib.readify.data.repository.book;

import java.util.List;

import it.unimib.readify.model.OLWorkApiResponse;

/**
 * Interface to send data from Repositories to Activity/Fragment.
 */
public interface BookResponseCallback {
    void onSuccessSearchFromRemote(List<OLWorkApiResponse> workApiResponseList, int numFound);
    void onSuccessFetchBookFromRemote(OLWorkApiResponse workApiResponse);
    void onSuccessFetchBooksFromRemote(List<OLWorkApiResponse> workApiResponseList, String reference);
    void onFailureFromRemote(Exception exception);
}
