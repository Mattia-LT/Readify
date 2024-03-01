package it.unimib.readify.util;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unimib.readify.data.repository.book.BookRepository;
import it.unimib.readify.data.repository.book.IBookRepository;
import it.unimib.readify.data.repository.user.TestDatabaseRepository;
import it.unimib.readify.data.repository.user.TestIDatabaseRepository;
import it.unimib.readify.data.service.OLApiService;
import it.unimib.readify.model.OLDescription;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
    TestServiceLocator is a Singleton class which maps every Repository class
     with its "repositories" attribute, allowing only one instantiation per Repository class.
    This make every Repository class having the same behavior as a Singleton class,
     even though it doesn't implement its classic structure.
    This allows every Activity / Fragment to have access to the same data,
     decreasing the possibility of having problems regarding UI data visualization.
    It is a good practice when it is needed 1) a general UI update of every Activity / Fragment
     (each time the data the UI based its appearance changes)
     and 2) an automatic check of data changing during some particular actions (asynchronous actions)
 */
public class TestServiceLocator {

    private static volatile TestServiceLocator INSTANCE = null;
    private final Map<Class<?>, Object> repositories = new ConcurrentHashMap<>();
    private final Application application;

    private TestServiceLocator(Application application) {
        this.application = application;
    }

    public static TestServiceLocator getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized(TestServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TestServiceLocator(application);
                }
            }
        }
        return INSTANCE;
    }

    public <T> T getRepository(Class<T> repositoryClass) {
        Object repositoryInstance = repositories.get(repositoryClass);
        if(repositoryInstance == null) {
            synchronized (repositories) {
                repositoryInstance = repositories.get(repositoryClass);
                if(repositoryInstance == null) {
                    //creating TestDatabaseRepository instance
                    if(repositoryClass == TestIDatabaseRepository.class) {
                        repositoryInstance = TestDatabaseRepository.getInstance(application);
                        repositories.put(repositoryClass, repositoryInstance);
                    }
                    //creating BookRepository instance
                    if(repositoryClass == IBookRepository.class) {
                        repositoryInstance = BookRepository.getInstance(application);
                        repositories.put(repositoryClass, repositoryInstance);
                    }
                }
            }
        }
        return repositoryClass.cast(repositoryInstance);
    }

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
