package it.unimib.readify.service;

import static it.unimib.readify.util.Constants.OL_SEARCH_ENDPOINT;
import static it.unimib.readify.util.Constants.OL_SEARCH_LIMIT_PARAMETER;
import static it.unimib.readify.util.Constants.OL_SEARCH_OFFSET_PARAMETER;
import static it.unimib.readify.util.Constants.OL_SEARCH_Q_PARAMETER;
import static it.unimib.readify.util.Constants.OL_SEARCH_SORT_PARAMETER;

import it.unimib.readify.model.OLAuthorApiResponse;
import it.unimib.readify.model.OLSearchApiResponse;
import it.unimib.readify.model.OLWorkApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OLApiService {

    @GET(OL_SEARCH_ENDPOINT)
    Call<OLSearchApiResponse> searchBooks(
            @Query(OL_SEARCH_Q_PARAMETER) String q,
            @Query(OL_SEARCH_SORT_PARAMETER) String sort,
            @Query(OL_SEARCH_LIMIT_PARAMETER) int limit,
            @Query(OL_SEARCH_OFFSET_PARAMETER) int offset );


    @GET("{id}.json")
    Call<OLWorkApiResponse> fetchBook(@Path("id") String id );

    @GET("{id}.json")
    Call<OLAuthorApiResponse> fetchAuthor(@Path("id") String id );


}
