package it.unimib.readify.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import it.unimib.readify.data.repository.user.IUserRepository;

public class UserViewModelFactory implements ViewModelProvider.Factory {
    private final IUserRepository userRepository;

    public UserViewModelFactory(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.equals(UserViewModel.class)) {
            return (T) new UserViewModel(userRepository);
        }
        else {
            // Gestisci altri tipi di ViewModel o lancia un'eccezione se necessario
            throw new IllegalArgumentException("Unsupported model class: " + modelClass);
        }
    }

}
