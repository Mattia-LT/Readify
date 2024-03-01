package it.unimib.readify.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unimib.readify.data.repository.user.TestIDatabaseRepository;

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
 */
public class TestDatabaseViewModelFactory implements ViewModelProvider.Factory {

    private static volatile TestDatabaseViewModelFactory INSTANCE = null;
    private final Map<Class<? extends ViewModel>, ViewModel> viewModels = new ConcurrentHashMap<>();
    private final TestIDatabaseRepository testDatabaseRepository;

    private TestDatabaseViewModelFactory(TestIDatabaseRepository testDatabaseRepository) {
        this.testDatabaseRepository = testDatabaseRepository;
    }

    public static TestDatabaseViewModelFactory getInstance(TestIDatabaseRepository testDatabaseRepository) {
        if (INSTANCE == null) {
            synchronized (TestDatabaseViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TestDatabaseViewModelFactory(testDatabaseRepository);
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
            return (T) new TestDatabaseViewModel(testDatabaseRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}

