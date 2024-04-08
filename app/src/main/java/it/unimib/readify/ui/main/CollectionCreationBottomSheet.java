package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.COLLECTION_NAME_CHARACTERS_LIMIT;

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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import it.unimib.readify.R;
import it.unimib.readify.databinding.BottomSheetCollectionCreationBinding;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class CollectionCreationBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetCollectionCreationBinding binding;
    private TestDatabaseViewModel testDatabaseViewModel;
    private String idToken;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetCollectionCreationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        this.idToken = CollectionCreationBottomSheetArgs.fromBundle(getArguments()).getIdToken();
        binding.characterCounter.setText("0");
        binding.characterLimit.setText(String.valueOf(COLLECTION_NAME_CHARACTERS_LIMIT));
        binding.editTextCollectionCreationName.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(COLLECTION_NAME_CHARACTERS_LIMIT)
        });
        binding.editTextCollectionCreationName.addTextChangedListener(new TextWatcher() {
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

        Button confirmButton = binding.buttonCollectionCreationConfirm;
        confirmButton.setOnClickListener(v -> {
            TextInputLayout textInputLayout = binding.createTextInputLayoutAddCollection;
            EditText editText = textInputLayout.getEditText();
            String collectionName = (editText != null) ? editText.getText().toString() : "";
            collectionName = collectionName.trim();
            if(collectionName.isEmpty()){
                Snackbar.make(requireView(), getString(R.string.snack_bar_empty_collection_name), Snackbar.LENGTH_SHORT).show();
            } else {
                boolean visible = binding.switchCollectionVisibility.isChecked();
                testDatabaseViewModel.createCollection(idToken, collectionName, visible);
                testDatabaseViewModel.fetchCollections(idToken);
                requireDialog().dismiss();
            }
        });
    }

    private void initViewModels() {
        testDatabaseViewModel = TestDatabaseViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);
    }
}