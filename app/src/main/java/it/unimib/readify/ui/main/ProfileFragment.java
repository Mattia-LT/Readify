package it.unimib.readify.ui.main;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.databinding.FragmentProfileBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;

public class ProfileFragment extends Fragment implements CollectionCreationBottomSheet.OnInputListener {

    private ArrayList<it.unimib.readify.model.Collection> collectionsList;
    private FragmentProfileBinding fragmentProfileBinding;
    private CollectionViewModel collectionViewModel;
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

        DrawerLayout drawerLayout = fragmentProfileBinding.drawLayout;
        ImageView imageView = fragmentProfileBinding.hamburger;
        NavigationView navigationView = fragmentProfileBinding.navView;

        runCollectionsView(view);
        runCollectionCreationProcess(view);

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




    //managing collections existence
    public void runCollectionsView(View view) {
        //test data
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

        //initializing viewModel and collectionAdapter
        collectionViewModel = new ViewModelProvider(requireActivity(), new DataViewModelFactory())
                .get(CollectionViewModel.class);
        collectionAdapter = new CollectionAdapter(
                new CollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onCollectionItemClick(Collection collection) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("collectionData", collection);
                        Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_collectionFragment, bundle);
                    }
                }, requireActivity().getApplication());

        //managing recycler view
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        fragmentProfileBinding.recyclerviewCollections.setLayoutManager(layoutManager);
        fragmentProfileBinding.recyclerviewCollections.setAdapter(collectionAdapter);

        //managing viewModel
        collectionViewModel.getCollectionListLiveData().observe(getViewLifecycleOwner(), new Observer<List<Collection>>() {
            @Override
            public void onChanged(List<Collection> collections) {
                collectionAdapter.setCollectionsList(collections);
            }
        });
        collectionViewModel.updateCollectionListLiveData(collectionsList);
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
        collectionsList.add(newCollection);
        collectionViewModel.updateCollectionListLiveData(collectionsList);
    }
}