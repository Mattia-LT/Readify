package it.unimib.readify.util;

import android.app.Application;

import it.unimib.readify.database.BookRoomDatabase;
import it.unimib.readify.repository.BookRepository;
import it.unimib.readify.repository.IBookRepository;
import it.unimib.readify.service.OLSearchApiService;
import it.unimib.readify.service.OLWorkApiService;
import it.unimib.readify.source.BaseBookRemoteDataSource;
import it.unimib.readify.source.BookRemoteDataSource;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * It creates an instance of OLSearchApiService using Retrofit.
     * @return an instance of OLSearchApiService.
     */
    public OLSearchApiService getOLSearchApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.OL_API_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(OLSearchApiService.class);
    }

    public OLWorkApiService getOLWorkApiService() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.OL_API_BASE_URL).
                addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(OLWorkApiService.class);
    }

    public IBookRepository getBookRepository() {
        BaseBookRemoteDataSource bookRemoteDataSource = new BookRemoteDataSource();
        return new BookRepository(bookRemoteDataSource);
    }

    public BookRoomDatabase getBookDao(Application application) {
        return BookRoomDatabase.getDatabase(application);
    }
}
