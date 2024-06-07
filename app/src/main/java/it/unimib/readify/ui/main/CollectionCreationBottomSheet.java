package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.COLLECTION_NAME_CHARACTERS_LIMIT;

import android.content.res.Configuration;
import android.graphics.PorterDuff;
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
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import it.unimib.readify.R;
import it.unimib.readify.databinding.BottomSheetCollectionCreationBinding;
import it.unimib.readify.viewmodel.CollectionViewModel;
import it.unimib.readify.viewmodel.CustomViewModelFactory;

public class CollectionCreationBottomSheet extends BottomSheetDialogFragment {
    private BottomSheetCollectionCreationBinding binding;
    private CollectionViewModel collectionViewModel;
    private boolean isNameValid;
    private boolean isNameUnique;
    private Button confirmButton;
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
                collectionViewModel.setNewCollectionName(s.toString());
                collectionViewModel.validateCollectionName();
                int currentLength = s.length();
                binding.characterCounter.setText(String.valueOf(currentLength));
            }
        });

        confirmButton = binding.buttonCollectionCreationConfirm;
        confirmButton.setOnClickListener(v -> {
            TextInputLayout textInputLayout = binding.createTextInputLayoutAddCollection;
            EditText editText = textInputLayout.getEditText();
            String collectionName = (editText != null) ? editText.getText().toString() : "";
            collectionName = collectionName.trim();
            if(collectionName.isEmpty()){
                Snackbar.make(requireView(), getString(R.string.snack_bar_empty_collection_name), Snackbar.LENGTH_SHORT).show();
            } else {
                boolean visible = binding.switchCollectionVisibility.isChecked();
                collectionViewModel.createCollection(idToken, collectionName, visible);
                collectionViewModel.loadLoggedUserCollections();
                requireDialog().dismiss();
            }
        });
    }

    private void initViewModels() {
        collectionViewModel = CustomViewModelFactory
                .getInstance(requireActivity().getApplication())
                .create(CollectionViewModel.class);
    }

    private void initObservers() {
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
    }

    private void updateErrorMessage() {
        if(!isNameValid){
            binding.editTextCollectionCreationName.setError(getString(R.string.error_collection_name_not_valid));
            confirmButton.setEnabled(false);
            confirmButton.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
            confirmButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.grey_disabled_item));
        } else if(!isNameUnique){
            binding.editTextCollectionCreationName.setError(getString(R.string.error_collection_name_not_unique));
            confirmButton.setEnabled(false);
            confirmButton.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
            confirmButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.grey_disabled_item));
        } else {
            binding.editTextCollectionCreationName.setError(null);
            confirmButton.setEnabled(true);
            confirmButton.setBackgroundTintMode(PorterDuff.Mode.SRC_IN);
            confirmButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.login_blue));
        }
    }

}