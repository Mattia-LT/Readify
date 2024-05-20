package it.unimib.readify.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unimib.readify.data.repository.book.IBookRepository;
import it.unimib.readify.data.repository.user.TestIDatabaseRepository;
import it.unimib.readify.data.repository.collection.ICollectionRepository;

import it.unimib.readify.util.TestServiceLocator;

/*
    TestDatabaseViewModelFactory is a Singleton class which maps every ViewModel class
     with its "viewModels" attribute, allowing only one instantiation per ViewModel class.
    This make every ViewModel class having the same behavior as a Singleton class,
     even though it doesn't implement its classic structure.
    This allows every Activity / Fragment to have access to the same data,
     decreasing the possibility of having problems regarding UI data visualization.
    It is a good practice when it is needed 1) a general UI update of every Activity / Fragment
     (each time the data the UI based its appearance changes)
     and 2) an automatic check of data changing during some particular actions (asynchronous actions)

     EDITED: TestDatabaseViewModelFactory now manages the creation of every ViewModel Class
 */
public class TestDatabaseViewModelFactory implements ViewModelProvider.Factory {

    private static volatile TestDatabaseViewModelFactory INSTANCE = null;
    private final Map<Class<? extends ViewModel>, ViewModel> viewModels = new ConcurrentHashMap<>();
    private final TestIDatabaseRepository testIDatabaseRepository;
    private final IBookRepository iBookRepository;
    private final ICollectionRepository iCollectionRepository;

    private TestDatabaseViewModelFactory(Application application) {
        this.testIDatabaseRepository = TestServiceLocator.getInstance(application)
                .getRepository(TestIDatabaseRepository.class);
        this.iBookRepository = TestServiceLocator.getInstance(application)
                .getRepository(IBookRepository.class);
        this.iCollectionRepository = TestServiceLocator.getInstance(application)
                .getRepository(ICollectionRepository.class);
    }

    public static TestDatabaseViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (TestDatabaseViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TestDatabaseViewModelFactory(application);
                }
            }
        }
        return INSTANCE;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        ViewModel viewModel = viewModels.get(modelClass);
        if (viewModel == null) {
            synchronized (viewModels) {
                viewModel = viewModels.get(modelClass);
                if (viewModel == null) {
                    viewModel = createViewModel(modelClass);
                    viewModels.put(modelClass, viewModel);
                }
            }
        }
        return (T) viewModel;
    }

    @SuppressWarnings("unchecked")
    private <T extends ViewModel> T createViewModel(Class<T> modelClass) {
        //creating TestDatabaseViewModel instance
        if (modelClass.isAssignableFrom(TestDatabaseViewModel.class)) {
            return (T) new TestDatabaseViewModel(testIDatabaseRepository);
        }
        //creating BookViewModel instance
        if (modelClass.isAssignableFrom(BookViewModel.class)) {
            return (T) new BookViewModel(iBookRepository);
        }
        //creating CollectionViewModel instance
        if(modelClass.isAssignableFrom(CollectionViewModel.class)){
            return (T) new CollectionViewModel(iCollectionRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}