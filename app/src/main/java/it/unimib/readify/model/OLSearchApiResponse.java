package it.unimib.readify.model;

import java.util.List;

/*
usata per registrare la risposta dell'api alla nostra richiesta
 */
public class OLSearchApiResponse extends BookResponse{

    private int numFound;
    private int start;
    private boolean numFoundExact;
    private List<String> docsKeys;
    private String q;
    private int offset;

    public OLSearchApiResponse() {
        super();
    }

    public OLSearchApiResponse(int numFound, int start, boolean numFoundExact, List<String> docsKeys, String q, int offset) {
        super(articles);
        this.status = status;
        this.totalResults = totalResults;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

}
