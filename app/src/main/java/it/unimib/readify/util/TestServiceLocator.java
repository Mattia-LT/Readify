package it.unimib.readify.util;

import android.app.Application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unimib.readify.data.repository.user.TestDatabaseRepository;
import it.unimib.readify.data.repository.user.TestIDatabaseRepository;

/*
    TestServiceLocator is a Singleton class which maps every Repository class
     with its "repositories" attribute, allowing only one instantiation per Repository class.
    This make every Repository class having the same behavior as a Singleton class,
     even though it doesn't implement its classic structure.
    This allows every Activity / Fragment to have access to the same data,
     decreasing the possibility of having problems regarding asynchronous calls and
     UI data visualization
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
                }
            }
        }
        return repositoryClass.cast(repositoryInstance);
    }
}
