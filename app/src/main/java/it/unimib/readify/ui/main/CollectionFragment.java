package it.unimib.readify.ui.main;

import static android.graphics.Typeface.BOLD_ITALIC;
import static it.unimib.readify.util.Constants.COLLECTION_NAME_CHARACTERS_LIMIT;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookItemCollectionAdapter;
import it.unimib.readify.databinding.FragmentCollectionBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.Result;
import it.unimib.readify.viewmodel.CollectionViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class CollectionFragment extends Fragment {

    private FragmentCollectionBinding collectionProfileBinding;
    private TestDatabaseViewModel testDatabaseViewModel;
    private CollectionViewModel collectionViewModel;
    private Collection currentCollection;
    private String loggedUserIdToken;
    private String collectionOwnerIdToken;
    BookItemCollectionAdapter bookItemCollectionAdapter;
    private AlertDialog renameDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        collectionProfileBinding = FragmentCollectionBinding.inflate(inflater,container,false);
        return collectionProfileBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModels();
        initObservers();
        loadMenu();

        collectionOwnerIdToken = CollectionFragmentArgs.fromBundle(getArguments()).getCollectionOwnerIdToken();
        currentCollection = CollectionFragmentArgs.fromBundle(getArguments()).getCollectionData();
        String collectionName = CollectionFragmentArgs.fromBundle(getArguments()).getCollectionName();

        initRecyclerView();

        //Managing data from Profile Fragment
        requireActivity().setTitle(collectionName);

        showCollectionData();

    }

    private void initViewModels(){
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);

        collectionViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(CollectionViewModel.class);
    }

    private void initObservers() {
        final Observer<Result> loggedUserObserver = result -> {
            if(result.isSuccess()) {
                this.loggedUserIdToken = ((Result.UserSuccess) result).getData().getIdToken();
                Log.e("USER OBSERVER","TRIGGERED");
            }
        };
        testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), loggedUserObserver);

        final Observer<List<Result>> collectionObserver = results -> {
            //orElse is the defaultValue, it should never be called but this is a safety check
            this.currentCollection = results.stream()
                    .filter(result -> result instanceof Result.CollectionSuccess)
                    .map(result -> (Result.CollectionSuccess) result)
                    .map(Result.CollectionSuccess::getData)
                    .filter(collection -> collection.getCollectionId().equalsIgnoreCase(currentCollection.getCollectionId()))
                    .findFirst()
                    .orElse(null);

            if(currentCollection != null){
                bookItemCollectionAdapter.submitList(currentCollection.getWorks());
                showCollectionData();
            }
        };

        collectionViewModel.getLoggedUserCollectionListLiveData().observe(getViewLifecycleOwner(), collectionObserver);
    }

    private void loadMenu(){
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                if(loggedUserIdToken.equals(collectionOwnerIdToken)){
                    menuInflater.inflate(R.menu.collection_appbar_menu, menu);
                    //todo caricare il menu solo se le collezioni sono dell'utente loggato
                }
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_collection_settings) {
                    SubMenu collectionSettingsSubMenu = menuItem.getSubMenu();
                    if (collectionSettingsSubMenu != null) {
                        MenuItem renameCollectionItem = collectionSettingsSubMenu.findItem(R.id.action_rename_collection);
                        renameCollectionItem.setOnMenuItemClickListener(item -> {
                            loadRenameCollectionDialog();
                            return false;
                        });
                    }
                } else if(menuItem.getItemId() == R.id.action_delete_collection){
                    loadDeleteCollectionDialog();
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void loadRenameCollectionDialog() {
        View renameDialogLayout = getLayoutInflater().inflate(R.layout.dialog_rename_collection, null);

        TextInputLayout textInputLayout = renameDialogLayout.findViewById(R.id.rename_textinputlayout_dialog);
        EditText renameEditText = textInputLayout.getEditText();

        //isValid -> name's length OK and contain only permitted characters
        collectionViewModel.isNameValid().observe(getViewLifecycleOwner(), isValid -> {
            if (!isValid){
                if (renameEditText != null) {
                    //TODO metti un file R.string
                    Log.e("isNameValid","false");
                    renameEditText.setError("Invalid collection name");
                    renameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });

        collectionViewModel.isNameUnique().observe(getViewLifecycleOwner(), isUnique -> {
            if (isUnique) {
                Log.e("isNameUnique","true");
                if (renameEditText != null) {
                    renameEditText.setError(null);
                }
                renameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
            } else {
                if (renameEditText != null) {
                    //TODO metti un file R.string
                    Log.e("isNameUnique","false");
                    renameEditText.setError("Collection name already exists");
                    renameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }
        });

        String originalMessage = requireContext().getString(R.string.rename_collection_confirm_message);

        TextView characterCounterRename = renameDialogLayout.findViewById(R.id.character_counter_rename);
        TextView characterLimitRename = renameDialogLayout.findViewById(R.id.character_limit_rename);
        characterCounterRename.setText("0");
        characterLimitRename.setText(String.valueOf(COLLECTION_NAME_CHARACTERS_LIMIT));
        if (renameEditText != null) {
            renameEditText.setFilters(new InputFilter[]{
                    new InputFilter.LengthFilter(COLLECTION_NAME_CHARACTERS_LIMIT)
            });
            renameEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Not needed for this implementation
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    collectionViewModel.setNewCollectionName(s.toString());
                    collectionViewModel.validateCollectionName();
                    int currentLength = s.length();
                    characterCounterRename.setText(String.valueOf(currentLength));
                }
            });
        }

        AlertDialog.Builder renameDialogBuilder = new AlertDialog.Builder(requireContext());
        renameDialogBuilder
                .setTitle(R.string.rename_collection_action)
                .setMessage(originalMessage)
                .setView(renameDialogLayout)
                .setPositiveButton(R.string.confirm_action, (dialog, which) -> {
                    if (renameEditText != null) {
                        collectionViewModel.getCollectionName().observe(getViewLifecycleOwner(), new Observer<String>(){
                            @Override
                            public void onChanged(String newCollectionName) {
                                collectionProfileBinding.collectionFragmentCollectionName.setText(newCollectionName);
                                AppCompatActivity activity = (AppCompatActivity) requireActivity();
                                //update top appbar title
                                if (activity.getSupportActionBar() != null) {
                                    activity.getSupportActionBar().setDisplayShowTitleEnabled(true);
                                    activity.getSupportActionBar().setTitle(newCollectionName);
                                }
                                collectionViewModel.getCollectionName().removeObserver(this);
                            }
                        });
                        collectionViewModel.renameCollection(loggedUserIdToken, currentCollection.getCollectionId());
                    }
                })
                .setNegativeButton(R.string.cancel_action, (dialog, which) -> dialog.dismiss());
        renameDialog = renameDialogBuilder.create();
        renameDialog.show();
        renameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private void initRecyclerView() {
        bookItemCollectionAdapter = new BookItemCollectionAdapter(
                book -> {
                    NavDirections action = CollectionFragmentDirections.actionCollectionFragmentToBookDetailsFragment(book);
                    Navigation.findNavController(requireView()).navigate(action);
                });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        collectionProfileBinding.collectionFragmentBooksRecyclerView.setLayoutManager(layoutManager);
        collectionProfileBinding.collectionFragmentBooksRecyclerView.setAdapter(bookItemCollectionAdapter);
        bookItemCollectionAdapter.submitList(currentCollection.getWorks());
    }

    private void showCollectionData() {
        //Set collection name
        collectionProfileBinding.collectionFragmentCollectionName.setText(currentCollection.getName());
        //Set collection visibility icon
        if (currentCollection.isVisible()){
            collectionProfileBinding.collectionFragmentCollectionVisibility.setImageResource(R.drawable.baseline_visibility_24);
        } else{
            collectionProfileBinding.collectionFragmentCollectionVisibility.setImageResource(R.drawable.baseline_lock_outline_24);
        }
        //Set number of books in the collection
        String booksNumber;
        if (currentCollection.getBooks() != null) {
            booksNumber = getResources().getQuantityString(R.plurals.books, currentCollection.getNumberOfBooks(), currentCollection.getNumberOfBooks());
        } else {
            booksNumber = getResources().getString(R.string.empty_collection);
        }
        collectionProfileBinding.collectionFragmentNumberOfBooks.setText(booksNumber);
    }

    private void loadDeleteCollectionDialog(){
        String originalMessage = requireContext().getString(R.string.delete_collection_confirm_message)
                .concat(": ")
                .concat(currentCollection.getName())
                .concat(" ?");

        SpannableStringBuilder formattedMessage = getFormattedMessage(originalMessage);

        MaterialAlertDialogBuilder deleteDialogBuilder = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_collection_action)
                .setMessage(formattedMessage)
                .setPositiveButton(R.string.confirm_action, (dialog, which) -> {
                    collectionViewModel.deleteCollection(loggedUserIdToken, currentCollection);
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton(R.string.cancel_action, (dialog, which) -> dialog.dismiss());

        deleteDialogBuilder.show();
    }

    @NonNull
    private SpannableStringBuilder getFormattedMessage(String originalMessage) {
        SpannableStringBuilder formattedMessage = new SpannableStringBuilder(originalMessage);
        StyleSpan boldStyleSpan = new StyleSpan(BOLD_ITALIC);
        ForegroundColorSpan redForegroundColorSpan = new ForegroundColorSpan(Color.RED);
        formattedMessage.setSpan(boldStyleSpan, originalMessage.indexOf(":") + 1, originalMessage.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        formattedMessage.setSpan(redForegroundColorSpan, originalMessage.indexOf(":") + 1, originalMessage.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return formattedMessage;
    }

}