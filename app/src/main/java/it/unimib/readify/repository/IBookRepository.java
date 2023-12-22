package it.unimib.readify.repository;

public interface IBookRepository {

    void searchBooks(String query, String sort, int limit, int offset);

    void fetchBook(String id);

}
