package it.unimib.readify.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.readify.repository.IBookRepository;

public class BookViewModelFactory implements ViewModelProvider.Factory {

    private final IBookRepository iBookRepository;

    public BookViewModelFactory(IBookRepository iBookRepository) {
        this.iBookRepository = iBookRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BookViewModel(iBookRepository);
    }
}
