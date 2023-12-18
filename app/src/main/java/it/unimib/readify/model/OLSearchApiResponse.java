package it.unimib.readify.model;

import java.util.List;

import it.unimib.readify.util.OLApiUtil;

/*
usata per registrare la risposta dell'api alla nostra richiesta
 */
public class OLSearchApiResponse extends OLResponse {

    private int numFound;
    private int start;
    private boolean numFoundExact;
    private List<OLDocs> docs;
    private String q;
    private int offset;

    public OLSearchApiResponse() {
        super();
    }

    public OLSearchApiResponse(List<OLDocs> docs, int numFound, int start, boolean numFoundExact, String q, int offset) {
        super(new OLWorkApiResponse().getBooksApi(docs));
        this.numFound = numFound;
        this.start = start;
        this.numFoundExact = numFoundExact;
        this.q = q;
        this.offset = offset;
    }
}
