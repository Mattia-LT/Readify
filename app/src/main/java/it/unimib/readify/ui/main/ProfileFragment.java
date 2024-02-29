package it.unimib.readify.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.data.repository.book.IBookRepository;
import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.data.repository.user.TestIDatabaseRepository;
import it.unimib.readify.databinding.FragmentProfileBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.util.TestServiceLocator;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.DataViewModelFactory;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.UserViewModelFactory;
import it.unimib.readify.util.ServiceLocator;

public class ProfileFragment extends Fragment implements CollectionCreationBottomSheet.OnInputListener {
    private FragmentProfileBinding fragmentProfileBinding;
    private TestDatabaseViewModel testDatabaseViewModel;
    private BookViewModel bookViewModel;
    private CollectionAdapter collectionAdapter;
    private User user;
    /*
        Do we want @copiedUser?
        In database, do we want to save Works (generated from every keyBook, contained in Collections)?
            No)     we need @copiedUser
            Yes)    we don't need @copiedUser
     */
    private User copiedUser;
    private Observer<Result> observer;

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
        TestIDatabaseRepository testDatabaseRepository = TestServiceLocator
                .getInstance(requireActivity().getApplication())
                .getRepository(TestIDatabaseRepository.class);
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(testDatabaseRepository)
                .create(TestDatabaseViewModel.class);

        //initializing repository and viewModel Book
        IBookRepository bookRepository = ServiceLocator.getInstance()
                .getBookRepository(requireActivity().getApplication());
        bookViewModel = new ViewModelProvider(
                requireActivity(),
                new DataViewModelFactory(bookRepository)
        ).get(BookViewModel.class);

        //initializing Observer
        observer = result -> {
            Log.d("profile fragment", "data changed");
            if(result.isSuccess()) {
                user = ((Result.UserSuccess)result).getData();
                copiedUser = new User(user);
                updateUI();

                //get books from api
                /*
                int counter = 0;
                for (int i = 0; i < copiedUser.getCollections().size(); i++) {
                    int finalCounter = counter;
                    //todo update constant "normal"
                    bookViewModel.fetchBooks(copiedUser.getCollections().get(i).getBooks(), "normal")
                            .observe(getViewLifecycleOwner(), resultsList -> {
                                for(int j = 0; j < resultsList.size(); j++) {
                                    if(resultsList.get(j).isSuccess()) {
                                        OLWorkApiResponse book = ((Result.WorkSuccess) resultsList.get(j)).getData();
                                        copiedUser.getCollections().get(finalCounter).getWorks().add(j, book);
                                    }
                                }
                                //UI collections view
                                //runCollectionsView(view, user);
                                //UI collections creation
                                //runCollectionCreationProcess();
                            });
                    counter++;
                }
                 */
            } else {
                //todo navigate to login(?)
                Snackbar.make(view, ((Result.Error)result).getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        };
        //get user data from database
        testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), observer);
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
                            if(itemId == R.id.nav_logout){
                                /*
                                testDatabaseViewModel.logout();
                                //FirebaseAuth.getInstance().signOut();
                                Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_loginFragment);
                                 */
                             }
                            if (itemId == R.id.nav_dark_mode) {
                                Snackbar.make(view, "Funzione non ancora implementata", Snackbar.LENGTH_SHORT).show();;
                            }
                            if (itemId == R.id.nav_visibility) {
                                ;/* Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_visibilityFragment);*/
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

    public void updateUI() {
        fragmentProfileBinding.textviewUsername.setText(copiedUser.getUsername());
        fragmentProfileBinding.textViewFollowers.setText(String.format(Locale.ENGLISH, "%d",
                copiedUser.getFollowers().getCounter()));
        fragmentProfileBinding.textViewFollowing.setText(String.format(Locale.ENGLISH, "%d",
                copiedUser.getFollowing().getCounter()));

        fragmentProfileBinding.textViewFacebook.setVisibility(View.GONE);
        fragmentProfileBinding.textViewTwitter.setVisibility(View.GONE);
        fragmentProfileBinding.textViewInstagram.setVisibility(View.GONE);
        for (int i = 0; i < copiedUser.getSocialLinks().size(); i++) {
            switch (copiedUser.getSocialLinks().get(i).getSocialPlatform()) {
                case "facebook":
                    fragmentProfileBinding.textViewFacebook.setText(copiedUser.getSocialLinks().get(i).getLink());
                    fragmentProfileBinding.textViewFacebook.setVisibility(View.VISIBLE);
                    break;
                case "twitter":
                    fragmentProfileBinding.textViewTwitter.setText(copiedUser.getSocialLinks().get(i).getLink());
                    fragmentProfileBinding.textViewTwitter.setVisibility(View.VISIBLE);
                    break;
                case "instagram":
                    fragmentProfileBinding.textViewInstagram.setText(copiedUser.getSocialLinks().get(i).getLink());
                    fragmentProfileBinding.textViewInstagram.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}