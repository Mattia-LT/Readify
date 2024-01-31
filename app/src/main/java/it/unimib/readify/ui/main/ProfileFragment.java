package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.FIREBASE_REALTIME_DATABASE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.databinding.FragmentProfileBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.ui.startup.UserViewModel;
import it.unimib.readify.ui.startup.UserViewModelFactory;
import it.unimib.readify.util.ServiceLocator;

public class ProfileFragment extends Fragment implements CollectionCreationBottomSheet.OnInputListener {

    private ArrayList<it.unimib.readify.model.Collection> collectionsList;
    private FragmentProfileBinding fragmentProfileBinding;
    private UserViewModel userViewModel;
    private CollectionAdapter collectionAdapter;

    public ProfileFragment() { /*Required empty public constructor*/ }

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

        //test data
        /*
        collectionsList = new ArrayList<>();
        collectionsList.add(0, new Collection("horror", false,
                new ArrayList<>(Collections.singletonList(new OLWorkApiResponse(
                        new ArrayList<>(Arrays.asList(-1, 6498519, 8904777))
                )))));
        collectionsList.add(1, new Collection("fantasy", true,
                new ArrayList<>(Collections.singletonList(null))));
        collectionsList.add(2, new Collection("favourites", false, null));
        collectionsList.add(3, new Collection("to recommend", true,
                new ArrayList<>(Collections.singletonList(new OLWorkApiResponse(
                        new ArrayList<>(Arrays.asList(-1, 108274, 233884))
                )))));
        collectionsList.add(4, new Collection("romance", true, null));
         */

        //initializing repository
        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());

        //initializing viewModel
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository))
                .get(UserViewModel.class);

        //viewModel actions

        userViewModel.getUserDataFromToken("utente1").observe(getViewLifecycleOwner(), result -> {
            if(result.isSuccess()) {
                User userData = ((Result.UserSuccess) result).getData();
                Log.d("data", userData.toString());
            } else {
                Log.d("getUserDatafrom token", "errore");
            }
        });

        /*
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_REALTIME_DATABASE);
        //Log.d("firebase", database.getApp().toString());
        DatabaseReference myRef = firebaseDatabase.getReference("users/utente1/biografia");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("read from database", "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d("error reading from database", "Failed to read value.", error.toException());
            }
        });
         */



        //runCollectionsView(view);
        //runCollectionCreationProcess(view);
        runDrawer();
    }


    //managing collections existence
    public void runCollectionsView(View view) {

        //managing collectionAdapter and recycler view
        collectionAdapter = new CollectionAdapter(
                new CollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onCollectionItemClick(Collection collection) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("collectionData", collection);
                        Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_collectionFragment, bundle);
                    }
                }, requireActivity().getApplication());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        fragmentProfileBinding.recyclerviewCollections.setLayoutManager(layoutManager);
        fragmentProfileBinding.recyclerviewCollections.setAdapter(collectionAdapter);
    }

    public void runCollectionCreationProcess(View view) {
        CollectionCreationBottomSheet collectionCreationBottomSheet = new CollectionCreationBottomSheet();
        collectionCreationBottomSheet.onInputListener(this);

        fragmentProfileBinding.createCollection.setOnClickListener( v -> {
            collectionCreationBottomSheet.show(getChildFragmentManager(), collectionCreationBottomSheet.getTag());
        });
    }

    //CollectionCreationBottomSheet.OnInputListener method

    @Override
    public void sendInput(Collection newCollection) {
        //collectionsList.add(newCollection);
        //userViewModel.updateCollectionListLiveData(collectionsList);
    }

    public void runDrawer() {
        DrawerLayout drawerLayout = fragmentProfileBinding.drawLayout;
        ImageView imageView = fragmentProfileBinding.hamburger;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });
    }

}