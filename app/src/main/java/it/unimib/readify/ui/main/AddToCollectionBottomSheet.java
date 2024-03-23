package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.COLLECTION_NAME_CHARACTERS_LIMIT;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.stream.Collectors;

import it.unimib.readify.adapter.AddToCollectionAdapter;
import it.unimib.readify.data.repository.book.IBookRepository;
import it.unimib.readify.data.repository.user.TestIDatabaseRepository;
import it.unimib.readify.databinding.BottomSheetAddToCollectionBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.TestServiceLocator;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.DataViewModelFactory;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class AddToCollectionBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetAddToCollectionBinding binding;
    private BookViewModel bookViewModel;
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
        initViewModels();
        initObservers();
        this.bookId = AddToCollectionBottomSheetArgs.fromBundle(getArguments()).getBookId();
        this.idToken = AddToCollectionBottomSheetArgs.fromBundle(getArguments()).getIdToken();
        // Get the BottomSheetBehavior
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) view.getParent());
        // Set the state to EXPANDED
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        Button cancelButton = binding.buttonCancelCollectionInsertion;
        Button confirmButton = binding.buttonConfirmCollectionInsertion;
        Button showAddCollectionButton = binding.buttonCreateNewCollection;

        RecyclerView recyclerViewCollections = binding.recyclerviewSelectCollections;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext());

        addToCollectionAdapter = new AddToCollectionAdapter(bookId, collection -> {

        });

        recyclerViewCollections.setLayoutManager(layoutManager);
        recyclerViewCollections.setAdapter(addToCollectionAdapter);


        testDatabaseViewModel.fetchCollections(idToken);

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

        cancelButton.setOnClickListener(v -> {
            binding.createSection.setVisibility(View.GONE);
            clearAddSection();
            showAddCollectionButton.setVisibility(View.VISIBLE);
        });



    }







    private void initViewModels(){
        IBookRepository bookRepository = TestServiceLocator
                .getInstance(requireActivity().getApplication())
                .getRepository(IBookRepository.class);

        bookViewModel = new ViewModelProvider(
                requireActivity(),
                new DataViewModelFactory(bookRepository)
        )
                .get(BookViewModel.class);

        TestIDatabaseRepository testDatabaseRepository = TestServiceLocator
                .getInstance(requireActivity().getApplication())
                .getRepository(TestIDatabaseRepository.class);

        testDatabaseViewModel = TestDatabaseViewModelFactory
                .getInstance(testDatabaseRepository)
                .create(TestDatabaseViewModel.class);
    }
    private void clearAddSection(){
        binding.characterCounter.setText("0");
        binding.switchCollectionVisibility.setChecked(false);
        binding.editTextCreateCollection.setText("");
    }
    private void initObservers(){
        Observer<Result> loggedUserObserver = result -> {
            Log.d("BookDetails fragment", "user changed");
            if(result.isSuccess()) {
                User user = ((Result.UserSuccess) result).getData();
                this.idToken = user.getIdToken();
            }
        };
        //get user data from database
        //testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);

        Observer<List<Result>> collectionsObserver = results -> {
            List<Collection> collectionsResultList = results.stream()
                    .filter(result -> result instanceof Result.CollectionSuccess)
                    .map(result -> ((Result.CollectionSuccess) result).getData())
                    .collect(Collectors.toList());
            addToCollectionAdapter.submitList(collectionsResultList);
        };

        testDatabaseViewModel.getCollectionListLiveData().observe(getViewLifecycleOwner(),collectionsObserver);







    }




}
