package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.BUNDLE_BOOK;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookItemCollectionAdapter;
import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.databinding.FragmentCollectionBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.UserViewModelFactory;
import it.unimib.readify.util.ServiceLocator;

public class CollectionFragment extends Fragment {

    private FragmentCollectionBinding collectionProfileBinding;
    private BookItemCollectionAdapter bookItemCollectionAdapter;
    private UserViewModel userViewModel;
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




        bookItemCollectionAdapter = new BookItemCollectionAdapter(
                new BookItemCollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onBookItemClick(OLWorkApiResponse book) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(BUNDLE_BOOK, book);
                        Navigation.findNavController(view).navigate(R.id.action_collectionFragment_to_bookDetailsFragment, bundle);
                    }
                }, requireActivity().getApplication());

        //managing recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        collectionProfileBinding.collectionFragmentBooksRecyclerView.setLayoutManager(layoutManager);
        collectionProfileBinding.collectionFragmentBooksRecyclerView.setAdapter(bookItemCollectionAdapter);

        //managing viewModel
        userViewModel.getUserMutableLiveData("prova@gmail.com", "password", true)
                .observe(getViewLifecycleOwner(), result -> {
                    collection = ((Result.UserSuccess) result).getData().getCollections().get(0);
                    Log.d("collection name", collection.getName());
                });
        //servirà in caso di modifiche (?)
        //userViewModel.updateCollectionListLiveData(collectionsList);




        //managing data from Profile Fragment
        Bundle bundle = getArguments();
        if(bundle != null) {
            collection = bundle.getParcelable("collectionData");

            //set layout data
            if(collection != null) {
                if (collection.getName() != null) {
                    //set collection name
                    collectionProfileBinding.collectionFragmentCollectionName.setText(collection.getName());
                    //set collection visibility icon
                    if(collection.isVisible())
                        collectionProfileBinding.collectionFragmentCollectionVisibility.setImageResource(R.drawable.baseline_lock_open_24);
                    else
                        collectionProfileBinding.collectionFragmentCollectionVisibility.setImageResource(R.drawable.baseline_lock_outline_24);
                    //set number of books in the collection
                    if(collection.getBooks() != null) {
                        String booksNumber = Integer.toString(collection.getBooks().size()) + " ";
                        if(collection.getBooks().size() > 1)
                            booksNumber += getResources().getString(R.string.books);
                        else
                            booksNumber += getResources().getString(R.string.book);
                        collectionProfileBinding.collectionFragmentBooksNumber.setText(booksNumber);
                    }
                    else {
                        collectionProfileBinding.collectionFragmentBooksNumber.setText(getResources()
                                .getString(R.string.empty_collection));
                    }
                }
            }
        }
    }

    private void loadMenu(){
        // Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();

                // TODO: cambiare nome
                String title = requireContext().getString(R.string.app_name)
                        .concat(" - ")
                        .concat("TODO nome collezione");
                        //.concat(collection.getName());
                toolbar.setTitle(title);
                menuInflater.inflate(R.menu.collection_appbar_menu, menu);

                //Set the settings icon to white

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
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                });



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
        //repository
        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());

        //initializing viewModel
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository))
                .get(UserViewModel.class);
    }

}
