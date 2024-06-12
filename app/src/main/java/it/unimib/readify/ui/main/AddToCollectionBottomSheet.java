package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.COLLECTION_NAME_CHARACTERS_LIMIT;

import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.stream.Collectors;

import it.unimib.readify.R;
import it.unimib.readify.adapter.AddToCollectionAdapter;
import it.unimib.readify.databinding.BottomSheetAddToCollectionBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.SubjectsUtil;
import it.unimib.readify.viewmodel.CollectionViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;

public class AddToCollectionBottomSheet extends BottomSheetDialogFragment {

    private final String TAG = AddToCollectionBottomSheet.class.getSimpleName();

    private BottomSheetAddToCollectionBinding binding;
    private CollectionViewModel collectionViewModel;
    private UserViewModel userViewModel;
    private AddToCollectionAdapter addToCollectionAdapter;
    private OLWorkApiResponse book;
    private String idToken;
    private User loggedUser;
    private boolean isNameValid = false;
    private boolean isNameUnique = false;
    private Button confirmButton;
    private Observer<Boolean> addToCollectionResultObserver;
    private Observer<Boolean> removeFromCollectionResultObserver;
    private Observer<List<Result>> collectionsObserver;
    private Observer<Result> loggedUserObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater , @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAddToCollectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //This operation is needed when the device is in Horizontal mode
        int deviceOrientation = getResources().getConfiguration().orientation;
        if (deviceOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            if (dialog != null) {
                dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }

        initViewModels();
        initObservers();

        this.book = AddToCollectionBottomSheetArgs.fromBundle(getArguments()).getBook();
        this.idToken = AddToCollectionBottomSheetArgs.fromBundle(getArguments()).getIdToken();

        collectionViewModel.loadLoggedUserCollections();

        Button cancelButton = binding.buttonCancelCollectionInsertion;
        confirmButton = binding.buttonConfirmCollectionInsertion;
        Button showAddCollectionButton = binding.buttonCreateNewCollection;

        RecyclerView recyclerViewCollections = binding.recyclerviewSelectCollections;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());

        addToCollectionAdapter = new AddToCollectionAdapter(book.getKey(), new AddToCollectionAdapter.OnCheckboxStatusChanged() {
            @Override
            public void onCollectionSelected(Collection collection) {
                collectionViewModel.addBookToCollection(idToken, book, collection.getCollectionId());
                collectionViewModel.getAddToCollectionResult().observe(getViewLifecycleOwner(), addToCollectionResultObserver);
            }

            @Override
            public void onCollectionUnselected(Collection collection) {
                collectionViewModel.removeBookFromCollection(idToken, book.getKey(), collection.getCollectionId());
                collectionViewModel.getRemoveFromCollectionResult().observe(getViewLifecycleOwner(), removeFromCollectionResultObserver);
            }
        });

        recyclerViewCollections.setLayoutManager(layoutManager);
        recyclerViewCollections.setAdapter(addToCollectionAdapter);

        binding.characterCounter.setText("0");
        binding.characterLimit.setText(String.valueOf(COLLECTION_NAME_CHARACTERS_LIMIT));
        binding.editTextCreateCollection.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(COLLECTION_NAME_CHARACTERS_LIMIT)
        });
        binding.editTextCreateCollection.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed for this implementation
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Update character count
                collectionViewModel.setNewCollectionName(s.toString());
                collectionViewModel.validateCollectionName();
                int currentLength = s.length();
                binding.characterCounter.setText(String.valueOf(currentLength));
            }
        });

        showAddCollectionButton.setOnClickListener(v -> {
            showAddCollectionButton.setVisibility(View.GONE);
            binding.createSection.setVisibility(View.VISIBLE);
        });

        cancelButton.setOnClickListener(v -> clearAddSection());

        confirmButton.setOnClickListener(v -> {
            TextInputLayout textInputLayout = binding.textInputLayoutAddCollection;
            EditText editText = textInputLayout.getEditText();
            String collectionName = (editText != null) ? editText.getText().toString() : "";
            collectionName = collectionName.trim();
            if(collectionName.isEmpty()){
                Snackbar.make(requireView(), getString(R.string.snack_bar_empty_collection_name), Snackbar.LENGTH_SHORT).show();
            } else {
                boolean visible = binding.switchCollectionVisibility.isChecked();
                collectionViewModel.createCollection(idToken, collectionName, visible);
                clearAddSection();
                collectionViewModel.loadLoggedUserCollections();
            }
        });
    }

    private void initViewModels(){
        collectionViewModel = CustomViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(CollectionViewModel.class);

        userViewModel = CustomViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(UserViewModel.class);
    }
    private void clearAddSection(){
        binding.buttonCreateNewCollection.setVisibility(View.VISIBLE);
        binding.createSection.setVisibility(View.GONE);
        binding.characterCounter.setText("0");
        binding.switchCollectionVisibility.setChecked(false);
        binding.editTextCreateCollection.setText("");
    }
    private void initObservers(){
        collectionsObserver = results -> {
            List<Collection> collectionsResultList = results.stream()
                    .filter(result -> result instanceof Result.CollectionSuccess)
                    .map(result -> ((Result.CollectionSuccess) result).getData())
                    .collect(Collectors.toList());
            addToCollectionAdapter.submitList(collectionsResultList);
        };
        collectionViewModel.getLoggedUserCollectionListLiveData().observe(getViewLifecycleOwner(),collectionsObserver);

        addToCollectionResultObserver = result -> {
            if(result != null){
                if(result){
                    SubjectsUtil subjectsUtil = SubjectsUtil.getSubjectsUtil(requireContext());
                    for(String subject : book.getSubjects()){
                        subject = subject.toLowerCase();
                        if(subjectsUtil.containSubject(subject)){
                            Integer currentValue = loggedUser.getRecommended().get(subject);
                            if(currentValue != null){
                                Integer newValue = currentValue + 1;
                                loggedUser.getRecommended().put(subject, newValue);
                            } else {
                                Log.e(TAG, "Unknown subject : " + subject);
                                loggedUser.getRecommended().put(subject, 0);
                            }
                        }
                    }
                    userViewModel.setUserRecommended(loggedUser);

                    int totalNumberOfBooks = loggedUser.getTotalNumberOfBooks();
                    totalNumberOfBooks += 1;
                    loggedUser.setTotalNumberOfBooks(totalNumberOfBooks);
                    userViewModel.setUserTotalNumberOfBooks(loggedUser);

                } else {
                    Toast.makeText(requireContext(), R.string.error_add_book_to_collection, Toast.LENGTH_SHORT).show();
                }
                //remove observer for single use action
                collectionViewModel.getAddToCollectionResult().removeObserver(addToCollectionResultObserver);
            }
        };

        removeFromCollectionResultObserver = result -> {
            if(result != null){
                if(result){
                    SubjectsUtil subjectsUtil = SubjectsUtil.getSubjectsUtil(requireContext());
                    for(String subject : book.getSubjects()){
                        subject = subject.toLowerCase();
                        if(subjectsUtil.containSubject(subject)){
                            Integer currentValue = loggedUser.getRecommended().get(subject);
                            if(currentValue != null){
                                Integer newValue = currentValue - 1;
                                loggedUser.getRecommended().put(subject, newValue);
                            } else {
                                Log.e(TAG, "Unknown subject : " + subject);
                                loggedUser.getRecommended().put(subject, 0);
                            }
                        }
                    }
                    userViewModel.setUserRecommended(loggedUser);

                    int totalNumberOfBooks = loggedUser.getTotalNumberOfBooks();
                    totalNumberOfBooks -= 1;
                    loggedUser.setTotalNumberOfBooks(totalNumberOfBooks);
                    userViewModel.setUserTotalNumberOfBooks(loggedUser);
                } else {
                    Toast.makeText(requireContext(), R.string.error_remove_book_from_collection, Toast.LENGTH_SHORT).show();
                }
                //remove observer for single use action
                collectionViewModel.getRemoveFromCollectionResult().removeObserver(removeFromCollectionResultObserver);
            }
        };

        loggedUserObserver = loggedUserResult -> {
            if(loggedUserResult.isSuccess()) {
                loggedUser = ((Result.UserSuccess) loggedUserResult).getData();
            } else {
                String errorMessage = ((Result.Error) loggedUserResult).getMessage();
                Log.e(TAG, "Error: Logged user fetch wasn't successful -> " + errorMessage);
            }
        };

        //isValid collection name -> length OK and contain only permitted characters
        collectionViewModel.isNameValid().observe(getViewLifecycleOwner(), isValid -> {
            if(isValid != null){
                this.isNameValid = isValid;
                updateErrorMessage();
            }
        });

        //isUnique collection name -> the user doesn't have another collection with the same name
        collectionViewModel.isNameUnique().observe(getViewLifecycleOwner(), isUnique -> {
            if (isUnique != null) {
                this.isNameUnique = isUnique;
                updateErrorMessage();
            }
        });

        userViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);
    }

    private void updateErrorMessage() {
        if(!isNameValid){
            binding.editTextCreateCollection.setError(getString(R.string.error_collection_name_not_valid));
            confirmButton.setEnabled(false);
            confirmButton.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
            confirmButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.grey_disabled_item));
        } else if(!isNameUnique){
            binding.editTextCreateCollection.setError(getString(R.string.error_collection_name_not_unique));
            confirmButton.setEnabled(false);confirmButton.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
            confirmButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.grey_disabled_item));
        } else {
            binding.editTextCreateCollection.setError(null);
            confirmButton.setEnabled(true);
            confirmButton.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
            confirmButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.login_blue));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        collectionViewModel.getAddToCollectionResult().removeObserver(addToCollectionResultObserver);
        collectionViewModel.getRemoveFromCollectionResult().removeObserver(removeFromCollectionResultObserver);
        collectionViewModel.getLoggedUserCollectionListLiveData().removeObserver(collectionsObserver);
        userViewModel.getUserMediatorLiveData().removeObserver(loggedUserObserver);
    }
}