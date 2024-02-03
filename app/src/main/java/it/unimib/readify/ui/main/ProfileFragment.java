package it.unimib.readify.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.data.repository.book.IBookRepository;
import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.databinding.FragmentProfileBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.DataViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.UserViewModelFactory;
import it.unimib.readify.util.ServiceLocator;

public class ProfileFragment extends Fragment implements CollectionCreationBottomSheet.OnInputListener {

    private FragmentProfileBinding fragmentProfileBinding;
    private UserViewModel userViewModel;
    private BookViewModel bookViewModel;
    private CollectionAdapter collectionAdapter;
    private User user;

    public ProfileFragment() {}

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater,container,false);
        return fragmentProfileBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMenu(view);
        //initializing repository and viewModel User
        IUserRepository userRepository = ServiceLocator.getInstance()
                .getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        //initializing repository and viewModel Book
        IBookRepository bookRepository = ServiceLocator.getInstance()
                .getBookRepository(requireActivity().getApplication());
        bookViewModel = new ViewModelProvider(
                requireActivity(),
                new DataViewModelFactory(bookRepository)
        ).get(BookViewModel.class);

        //get user data from database
        userViewModel.getLoggedUser().observe(getViewLifecycleOwner(), result -> {
            if(result.isSuccess()) {
                user = ((Result.UserSuccess) result).getData();
                Log.d("user collections", user.getCollections().toString());

                //get books from api
                int counter = 0;
                for (int i = 0; i < user.getCollections().size(); i++) {
                    int finalCounter = counter;
                    bookViewModel.fetchBooks(user.getCollections().get(i).getBooks(), "normal")
                            .observe(getViewLifecycleOwner(), resultsList -> {
                                for(int j = 0; j < resultsList.size(); j++) {
                                    if(resultsList.get(j).isSuccess()) {
                                        OLWorkApiResponse book = ((Result.WorkSuccess) resultsList.get(j)).getData();
                                        user.getCollections().get(finalCounter).getWorks().add(j, book);
                                    }
                                }
                            });
                    counter++;
                }

                //UI collections view
                runCollectionsView(view, user);
                //UI collections creation
                runCollectionCreationProcess();
            } else {
                Log.d("profile fragment error", "getLoggedUser");
            }
        });
    }


    //managing collections existence
    public void runCollectionsView(View view, User user) {

        //managing collectionAdapter and recycler view
        collectionAdapter = new CollectionAdapter(
                new CollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onCollectionItemClick(Collection collection) {
                        Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_collectionFragment);
                    }
                }, requireActivity().getApplication());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        fragmentProfileBinding.recyclerviewCollections.setLayoutManager(layoutManager);
        fragmentProfileBinding.recyclerviewCollections.setAdapter(collectionAdapter);
        collectionAdapter.setCollectionsList(user.getCollections());
    }

    public void runCollectionCreationProcess() {
        CollectionCreationBottomSheet collectionCreationBottomSheet = new CollectionCreationBottomSheet();
        collectionCreationBottomSheet.onInputListener(this);
        fragmentProfileBinding.createCollection.setOnClickListener( v -> {
            collectionCreationBottomSheet.show(getChildFragmentManager(), collectionCreationBottomSheet.getTag());
        });
    }

    //CollectionCreationBottomSheet.OnInputListener method
    //todo da fare
    @Override
    public void sendInput(Collection newCollection) {
        //collectionsList.add(newCollection);
        //userViewModel.updateCollectionListLiveData(collectionsList);
    }

    public void loadMenu(View view){
        // Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
                String title = requireContext().getString(R.string.app_name)
                        .concat(" - ")
                        .concat(requireContext().getString(R.string.book_details));
                toolbar.setTitle(title);
                menuInflater.inflate(R.menu.profile_appbar_menu, menu);
            }
            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_notifications) {
                    // TODO: Show notifications menu
                } else if (menuItem.getItemId() == R.id.action_settings) {
                    DrawerLayout drawerLayout = fragmentProfileBinding.drawLayout;
                    NavigationView navigationView = fragmentProfileBinding.navView;
                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                        drawerLayout.closeDrawer(GravityCompat.END);
                    } else {
                        drawerLayout.openDrawer(GravityCompat.END);
                    }
                    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                            int itemId = menuItem.getItemId();
                            if (itemId == R.id.nav_settings) {
                                Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_settingsFragment);
                            }
                            drawerLayout.closeDrawer(GravityCompat.END);
                            return true;
                        }

                    });
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
}