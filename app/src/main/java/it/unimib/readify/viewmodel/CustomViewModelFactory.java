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


public class CustomViewModelFactory implements ViewModelProvider.Factory {

    /**
     * Custom ViewModelProvider to be able to have a custom constructor
     * for all viewmodel class.
     */

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