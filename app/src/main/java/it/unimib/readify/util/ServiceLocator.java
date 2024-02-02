package it.unimib.readify.util;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.data.repository.user.UserRepository;
import it.unimib.readify.data.source.user.BaseUserAuthenticationRemoteDataSource;
import it.unimib.readify.data.source.user.BaseUserDataRemoteDataSource;
import it.unimib.readify.data.source.user.UserAuthenticationRemoteDataSource;
import it.unimib.readify.data.source.user.UserDataRemoteDataSource;
import it.unimib.readify.model.OLDescription;
import it.unimib.readify.data.repository.book.BookRepository;
import it.unimib.readify.data.repository.book.IBookRepository;
import it.unimib.readify.data.service.OLApiService;
import it.unimib.readify.data.source.book.BaseBookRemoteDataSource;
import it.unimib.readify.data.source.book.BookRemoteDataSource;
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


    public IUserRepository getUserRepository(Application application) {
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);
        DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);

        BaseUserAuthenticationRemoteDataSource userRemoteAuthenticationDataSource =
                new UserAuthenticationRemoteDataSource();

        BaseUserDataRemoteDataSource userDataRemoteDataSource =
                new UserDataRemoteDataSource(sharedPreferencesUtil);

        return new UserRepository(userRemoteAuthenticationDataSource, userDataRemoteDataSource);
    }


}
