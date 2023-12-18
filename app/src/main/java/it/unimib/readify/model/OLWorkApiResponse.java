package it.unimib.readify.model;

import java.util.List;

public class OLWorkApiResponse {

    private OLDescription OLDescription;
    private String title;
    private List<Integer> covers;
    private String first_publish_date;
    private String key;
    private List<OLAuthor> OLAuthors;
    private List<String> subjects;

    public OLWorkApiResponse(OLDescription OLDescription, String title, List<Integer> covers,
                             String first_publish_date, String key, List<OLAuthor> OLAuthors,
                             List<String> subjects) {
        this.OLDescription = OLDescription;
        this.title = title;
        this.covers = covers;
        this.first_publish_date = first_publish_date;
        this.key = key;
        this.OLAuthors = OLAuthors;
        this.subjects = subjects;
    }

    public OLWorkApiResponse(){}

    public List<Book> getBooksApi(List<OLDocs> docs) {
        List<Book> listBooks = null;

        return listBooks;
    }
}
