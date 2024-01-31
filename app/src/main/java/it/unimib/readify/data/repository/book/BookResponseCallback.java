package it.unimib.readify.data.repository.book;

import java.util.List;

import it.unimib.readify.model.OLWorkApiResponse;

/**
 * Interface to send data from DataSource to Repositories
 * that implement INewsRepositoryWithLiveData interface.
 */
public interface BookResponseCallback {

    void onSuccessFetchBooksFromRemote(List<OLWorkApiResponse> workApiResponseList, String reference);
    void onSuccessFromGettingUserPreferences();
}
