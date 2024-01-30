package it.unimib.readify.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.readify.data.repository.book.IBookRepository;

public class DataViewModelFactory implements ViewModelProvider.Factory {

    private IBookRepository iBookRepository;

    public DataViewModelFactory(IBookRepository iBookRepository) {
        this.iBookRepository = iBookRepository;
    }

    public DataViewModelFactory() {}

    /*
    @SuppressWarnings("unchecked") is used to suppress an annoying warning,
    which is managed for sure by the various class checking
    */
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.equals(BookViewModel.class)) {
            return (T) new BookViewModel(iBookRepository);
        }
        if (modelClass.equals(CollectionViewModel.class)) {
            return (T) new CollectionViewModel();
        } else {
            // Gestisci altri tipi di ViewModel o lancia un'eccezione se necessario
            throw new IllegalArgumentException("Unsupported model class: " + modelClass);
        }
    }
}
