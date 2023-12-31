package it.unimib.readify.util;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.unimib.readify.database.BookRoomDatabase;
import it.unimib.readify.model.OLDescription;
import it.unimib.readify.repository.BookRepository;
import it.unimib.readify.repository.IBookRepository;
import it.unimib.readify.service.OLApiService;
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
    public OLApiService getOLApiService() {
        Gson gsonDescriptionManager = new GsonBuilder()
                .registerTypeAdapter(OLDescription.class, new OLDescriptionDeserializer())
                .create();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.OL_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gsonDescriptionManager))
                .build();
        return retrofit.create(OLApiService.class);
    }

    public IBookRepository getBookRepository(Application application) {
        BaseBookRemoteDataSource bookRemoteDataSource = new BookRemoteDataSource(application);
        return new BookRepository(bookRemoteDataSource);
    }

    public BookRoomDatabase getBookDao(Application application) {
        return BookRoomDatabase.getDatabase(application);
    }
}
