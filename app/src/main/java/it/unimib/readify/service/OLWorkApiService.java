package it.unimib.readify.service;

import static it.unimib.readify.util.Constants.OL_WORKS_ENDPOINT;

import it.unimib.readify.model.OLWorkApiResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OLWorkApiService {

    //todo perche works viene duplicato?
    // OL_WORKS_ENDPOINT
    @GET("{id}.json")
    Call<OLWorkApiResponse> fetchBook(@Path("id") String id );
}
