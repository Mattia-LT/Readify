package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.COLLECTION_NAME_CHARACTERS_LIMIT;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import it.unimib.readify.model.Result;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class AddToCollectionBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetAddToCollectionBinding binding;
    private TestDatabaseViewModel testDatabaseViewModel;
    private AddToCollectionAdapter addToCollectionAdapter;
    private String bookId;
    private String idToken;
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
        this.bookId = AddToCollectionBottomSheetArgs.fromBundle(getArguments()).getBookId();
        this.idToken = AddToCollectionBottomSheetArgs.fromBundle(getArguments()).getIdToken();
        testDatabaseViewModel.fetchLoggedUserCollections(idToken);

        Button cancelButton = binding.buttonCancelCollectionInsertion;
        Button confirmButton = binding.buttonConfirmCollectionInsertion;
        Button showAddCollectionButton = binding.buttonCreateNewCollection;

        RecyclerView recyclerViewCollections = binding.recyclerviewSelectCollections;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());

        addToCollectionAdapter = new AddToCollectionAdapter(bookId, new AddToCollectionAdapter.OnCheckboxStatusChanged() {
            @Override
            public void onCollectionSelected(Collection collection) {
                testDatabaseViewModel.addBookToCollection(idToken, bookId, collection.getCollectionId());
            }

            @Override
            public void onCollectionUnselected(Collection collection) {
                testDatabaseViewModel.removeBookFromCollection(idToken, bookId, collection.getCollectionId());
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
                testDatabaseViewModel.createCollection(idToken, collectionName, visible);
                clearAddSection();
                testDatabaseViewModel.fetchLoggedUserCollections(idToken);
            }
        });
    }

    private void initViewModels(){
        testDatabaseViewModel = TestDatabaseViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);
    }
    private void clearAddSection(){
        binding.buttonCreateNewCollection.setVisibility(View.VISIBLE);
        binding.createSection.setVisibility(View.GONE);
        binding.characterCounter.setText("0");
        binding.switchCollectionVisibility.setChecked(false);
        binding.editTextCreateCollection.setText("");
    }
    private void initObservers(){
        Observer<List<Result>> collectionsObserver = results -> {
            List<Collection> collectionsResultList = results.stream()
                    .filter(result -> result instanceof Result.CollectionSuccess)
                    .map(result -> ((Result.CollectionSuccess) result).getData())
                    .collect(Collectors.toList());
            addToCollectionAdapter.submitList(collectionsResultList);
        };
        testDatabaseViewModel.getLoggedUserCollectionListLiveData().observe(getViewLifecycleOwner(),collectionsObserver);
    }
}