package it.unimib.readify.util;

import java.util.ArrayList;
import java.util.Collection;

/*
usata per verificare che la risposta dell'Api sia programmata correttamente,
ed in caso affermativo, registrare i dati attraverso la classe ApiResponse
 */
public class JSONParserUtil {
    //lista di tutti i parametri che si incontreranno nel JSON
    private String status;
    private int totalResults;
    private ArrayList<Collection> collections;
}
