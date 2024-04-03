package it.unimib.readify.ui.main;

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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.databinding.FragmentProfileBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class ProfileFragment extends Fragment implements CollectionCreationBottomSheet.OnInputListener {
    private FragmentProfileBinding fragmentProfileBinding;
    private TestDatabaseViewModel testDatabaseViewModel;
    private BookViewModel bookViewModel;
    private CollectionAdapter collectionAdapter;
    private User user;
    /*
        todo Do we need a copy of user data?
         @copiedUser
     */
    private User copiedUser;
    private Observer<Result> observer;
    private Observer<List<Collection>> collectionsObserver;

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
        Log.d("profile lifecycle", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
        MenuItem switchMenuItem = navigationView.getMenu().findItem(R.id.nav_switch);

        // Ottieni la vista dell'azione associata all'elemento di menu
        View actionLayout = switchMenuItem.getActionView();

        // Trova lo SwitchCompat all'interno della vista dell'azione
        SwitchCompat switchButton = actionLayout.findViewById(R.id.switch_compat);

        // Imposta il listener per il cambio di stato dello switch
        switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d("SwitchCompat", "Stato dello switch cambiato: " + isChecked);
            if (isChecked) {
                Log.d("SwitchCompat", "Tema scuro attivato");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                Log.d("SwitchCompat", "Tema scuro disattivato");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
        loadMenu(view);

        //initializing viewModel Database
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);

        //initializing viewModel Book
        bookViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(BookViewModel.class);

        //initializing Observers
        collectionsObserver = collections -> {
            Log.d("profile fragment", "collections changed");
            if(collections.size() == copiedUser.getFetchedCollections().size()) {
                runCollectionsView(view);
            }
            //todo managing observer deletion (where?)
        };

        observer = result -> {
            Log.d("profile fragment", "user changed");
            if(result.isSuccess()) {
                user = ((Result.UserSuccess)result).getData();
                copiedUser = new User(user);
                Log.d("profile test", copiedUser.getFetchedCollections().toString());

                /*
                TEST
                se collections == null, il campo fetchedCollections viene cancellato nel databse
                copiedUser.setFetchedCollections(null);
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
                DatabaseReference databaseReference = firebaseDatabase.getReference();
                databaseReference.child(FIREBASE_USERS_COLLECTION).child(copiedUser.getIdToken()).setValue(copiedUser)
                        .addOnSuccessListener(aVoid -> Log.d("profile", "success"))
                        .addOnFailureListener(e -> Log.d("profile", "error"));
                 */

                updateUI();

                //TODO fix using new collections logic (?) idk
                /*
                    todo optimize conditions and / or logic of @isCollectionsChanged
                     maybe we can use user == copiedUser
                    this resolves the double calling of @collectionsObserver
                    right now, this is straightforward; it is working but can be optimized most probably
                 */
                if(testDatabaseViewModel.isCollectionsChanged()) {
                    bookViewModel.fetchCollections(copiedUser.getFetchedCollections(), getViewLifecycleOwner());
                    testDatabaseViewModel.setCollectionsChanged(false);
                }
            } else {
                //todo navigate to login(?)
                Snackbar.make(view, ((Result.Error)result).getMessage(), Snackbar.LENGTH_SHORT).show();
            }
            //todo managing observer deletion (where?)
        };
        //get user data from database
        testDatabaseViewModel.getUserMediatorLiveData().observe(getViewLifecycleOwner(), observer);
        bookViewModel.getFetchedCollections().observe(getViewLifecycleOwner(), collectionsObserver);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("profile lifecycle", "onStart");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("profile lifecycle", "onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("profile lifecycle", "onDetach");
    }

    //managing collections existence
    public void runCollectionsView(View view) {
        //managing collectionAdapter and recycler view
        collectionAdapter = new CollectionAdapter(
                new CollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onCollectionItemClick(Collection collection) {
                        NavDirections action = ProfileFragmentDirections.actionProfileFragmentToCollectionFragment(collection, collection.getName());
                        Navigation.findNavController(view).navigate(action);
                    }
                }, requireActivity().getApplication());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        fragmentProfileBinding.recyclerviewCollections.setLayoutManager(layoutManager);
        fragmentProfileBinding.recyclerviewCollections.setAdapter(collectionAdapter);
        collectionAdapter.setCollectionsList(copiedUser.getFetchedCollections());
    }

    public void runCollectionCreationProcess() {
        CollectionCreationBottomSheet collectionCreationBottomSheet = new CollectionCreationBottomSheet();
        collectionCreationBottomSheet.onInputListener(this);
        fragmentProfileBinding.createCollection.setOnClickListener( v -> {
            collectionCreationBottomSheet.show(getChildFragmentManager(), collectionCreationBottomSheet.getTag());
        });
    }

    //CollectionCreationBottomSheet.OnInputListener method
    //todo implement
    @Override
    public void sendInput(Collection newCollection) {
        //collectionsList.add(newCollection);
        //userViewModel.updateCollectionListLiveData(collectionsList);
    }

    public void loadMenu(View view){
        // Set up the toolbar and remove all icons
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.clear();
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

                            if (itemId == R.id.nav_visibility) {
                                // Esegui le azioni per la voce del menu 'Visibility'
                                // ...
                            }

                            if (itemId == R.id.nav_switch) {

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