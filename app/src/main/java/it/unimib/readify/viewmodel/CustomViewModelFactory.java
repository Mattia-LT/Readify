package it.unimib.readify.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.unimib.readify.data.repository.book.IBookRepository;
import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.data.repository.collection.ICollectionRepository;

import it.unimib.readify.util.ServiceLocator;

/*
    CustomViewModelFactory is a Singleton class which maps every ViewModel class
     with its "viewModels" attribute, allowing only one instantiation per ViewModel class.
    This make every ViewModel class having the same behavior as a Singleton class,
     even though it doesn't implement its classic structure.
    This allows every Activity / Fragment to have access to the same data,
     decreasing the possibility of having problems regarding UI data visualization.
    It is a good practice when it is needed 1) a general UI update of every Activity / Fragment
     (each time the data the UI based its appearance changes)
     and 2) an automatic check of data changing during some particular actions (asynchronous actions)

     EDITED: CustomViewModelFactory now manages the creation of every ViewModel Class
 */
public class CustomViewModelFactory implements ViewModelProvider.Factory {

    private static volatile CustomViewModelFactory INSTANCE = null;
    private final Map<Class<? extends ViewModel>, ViewModel> viewModels = new ConcurrentHashMap<>();
    private final IUserRepository IUserRepository;
    private final IBookRepository iBookRepository;
    private final ICollectionRepository iCollectionRepository;

    private CustomViewModelFactory(Application application) {
        this.IUserRepository = ServiceLocator.getInstance(application)
                .getRepository(IUserRepository.class);
        this.iBookRepository = ServiceLocator.getInstance(application)
                .getRepository(IBookRepository.class);
        this.iCollectionRepository = ServiceLocator.getInstance(application)
                .getRepository(ICollectionRepository.class);
    }

    public static CustomViewModelFactory getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (CustomViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CustomViewModelFactory(application);
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
        //creating UserViewModel instance
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(IUserRepository);
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