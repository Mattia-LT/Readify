package it.unimib.readify.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.readify.repository.IBookRepository;

public class BooksViewModelFactory implements ViewModelProvider.Factory {

    private final IBookRepository iBookRepository;

    public BooksViewModelFactory(IBookRepository iBookRepository) {
        this.iBookRepository = iBookRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BooksViewModel(iBookRepository);
    }
}
