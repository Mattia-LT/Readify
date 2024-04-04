package it.unimib.readify.ui.main;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookItemCollectionAdapter;
import it.unimib.readify.databinding.FragmentCollectionBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class CollectionFragment extends Fragment {

    private FragmentCollectionBinding collectionProfileBinding;
    private BookItemCollectionAdapter bookItemCollectionAdapter;
    private TestDatabaseViewModel testDatabaseViewModel;
    private Collection collection;

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
        loadMenu();
        initRepositories();

        collection = CollectionFragmentArgs.fromBundle(getArguments()).getCollectionData();

        bookItemCollectionAdapter = new BookItemCollectionAdapter(
                new BookItemCollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onBookItemClick(OLWorkApiResponse book) {
                        NavDirections action = CollectionFragmentDirections.actionCollectionFragmentToBookDetailsFragment(book);
                        Navigation.findNavController(requireView()).navigate(action);
                    }
                }, requireActivity().getApplication());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        collectionProfileBinding.collectionFragmentBooksRecyclerView.setLayoutManager(layoutManager);
        collectionProfileBinding.collectionFragmentBooksRecyclerView.setAdapter(bookItemCollectionAdapter);
        bookItemCollectionAdapter.setBooks(collection.getWorks());

        //Managing data from Profile Fragment
        String collectionName = CollectionFragmentArgs.fromBundle(getArguments()).getCollectionName();
        requireActivity().setTitle(collectionName);
        //TODO DA IMPLEMENTARE ANCORA in USER DETAILS
        //Set collection name
        collectionProfileBinding.collectionFragmentCollectionName.setText(collection.getName());
        //Set collection visibility icon
        if (collection.isVisible())
            collectionProfileBinding.collectionFragmentCollectionVisibility.setImageResource(R.drawable.baseline_visibility_24);
        else
            collectionProfileBinding.collectionFragmentCollectionVisibility.setImageResource(R.drawable.baseline_lock_outline_24);
        //Set number of books in the collection
        if (collection.getBooks() != null) {
            String booksNumber = collection.getNumberOfBooks() + " ";
            if (collection.getNumberOfBooks() == 1){
                booksNumber += getResources().getString(R.string.book);
            } else {
                booksNumber += getResources().getString(R.string.books);
            }
            collectionProfileBinding.collectionFragmentBooksNumber.setText(booksNumber);
        } else {
            collectionProfileBinding.collectionFragmentBooksNumber.setText(getResources()
                    .getString(R.string.empty_collection));
        }
    }

    private void loadMenu(){
        // Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                menuInflater.inflate(R.menu.collection_appbar_menu, menu);
                //Set the settings icon to white
                //todo va cambiato qui? va fatto da xml? da chiedere a michi
                int colorWhite = getResources().getColor(R.color.white, null);
                MenuItem settingsMenuItem = menu.findItem(R.id.action_collection_settings);
                if (settingsMenuItem != null) {
                    Drawable settingsIcon = settingsMenuItem.getIcon();
                    if (settingsIcon != null) {
                        settingsIcon.setColorFilter(colorWhite, PorterDuff.Mode.SRC_IN);
                        settingsMenuItem.setIcon(settingsIcon);
                    }
                }

                // Enable the back button
                Drawable backButton = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_24);
                if (backButton != null) {
                    backButton.setColorFilter(colorWhite, PorterDuff.Mode.SRC_IN);
                }
                toolbar.setNavigationIcon(backButton);
                toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_collection_settings) {
                    // TODO: Apri impostazioni collection o un menu
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void initRepositories(){
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);
    }
}