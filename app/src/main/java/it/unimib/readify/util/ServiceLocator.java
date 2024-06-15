package it.unimib.readify.util;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unimib.readify.data.database.CollectionRoomDatabase;
import it.unimib.readify.data.repository.book.BookRepository;
import it.unimib.readify.data.repository.book.IBookRepository;
import it.unimib.readify.data.repository.collection.CollectionRepository;
import it.unimib.readify.data.repository.collection.ICollectionRepository;
import it.unimib.readify.data.repository.user.UserRepository;
import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.data.service.OLApiService;
import it.unimib.readify.model.OLDescription;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 *  Registry to provide the dependencies for the classes
 *  used in the application.
 */
public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;
    private final Map<Class<?>, Object> repositories = new ConcurrentHashMap<>();
    private final Application application;

    private ServiceLocator(Application application) {
        this.application = application;
    }

    /**
     * Returns an instance of ServiceLocator class.
     * @param application Param for accessing the global application state.
     * @return An instance of ServiceLocator.
     */
    public static ServiceLocator getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator(application);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Returns an instance of CollectionRoomDatabase class to manage collections local Room database.
     * @param application Param for accessing the global application state.
     * @return An instance of CollectionRoomDatabase.
     */
    public CollectionRoomDatabase getBookDao(Application application) {
        return CollectionRoomDatabase.getDatabase(application);
    }


    /**
     * Returns an instance of the IRepository needed.
     * @param repositoryClass Param for accessing the singleton repository needed.
     * @return An instance of IRepository needed.
     */
    public <T> T getRepository(Class<T> repositoryClass) {
        Object repositoryInstance = repositories.get(repositoryClass);
        if(repositoryInstance == null) {
            synchronized (repositories) {
                repositoryInstance = repositories.get(repositoryClass);
                SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(application);
                DataEncryptionUtil dataEncryptionUtil = new DataEncryptionUtil(application);
                if(repositoryInstance == null) {
                    //creating UserRepository instance
                    if(repositoryClass == IUserRepository.class) {
                        repositoryInstance = UserRepository.getInstance();
                        repositories.put(repositoryClass, repositoryInstance);
                    }
                    //creating BookRepository instance
                    if(repositoryClass == IBookRepository.class) {
                        repositoryInstance = BookRepository.getInstance(application);
                        repositories.put(repositoryClass, repositoryInstance);
                    }
                    //creating CollectionRepository instance
                    if(repositoryClass == ICollectionRepository.class) {
                        repositoryInstance = CollectionRepository.getInstance(application, getBookDao(application), sharedPreferencesUtil, dataEncryptionUtil);
                        repositories.put(repositoryClass, repositoryInstance);
                    }
                }
            }
        }
        return repositoryClass.cast(repositoryInstance);
    }

    /**
     * Returns an instance of OLApiService (OL stands for OpenLibrary) class using Retrofit.
     * @return an instance of OLApiService.
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
}
