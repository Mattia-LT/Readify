package it.unimib.readify.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;

public class ProfileFragment extends Fragment implements CollectionCreationBottomSheet.OnInputListener {

    private RecyclerView recyclerview_collections;
    private ArrayList<it.unimib.readify.model.Collection> collectionsArray;
    private Collection newCollection;

    public ProfileFragment() { /*Required empty public constructor*/ }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        runCollectionsView(view);
        runCollectionCreationProcess(view);
    }

    //CollectionCreationBottomSheet.OnInputListener method
    @Override
    public void sendInput(Collection newCollection) {
        this.newCollection = newCollection;
    }

    public void runCollectionsView(View view) {
        recyclerview_collections = view.findViewById(R.id.recyclerview_collections);
        //dati di prova
        collectionsArray = new ArrayList<>();
        collectionsArray.add(0, new Collection("horror", true,
                new ArrayList<>(Collections.singletonList(new OLWorkApiResponse(
                        new ArrayList<>(Arrays.asList(-1, 6498519, 8904777))
                )))));
        collectionsArray.add(1, new Collection("fantasy", false, null));
        collectionsArray.add(2, new Collection("preferiti", false, null));
        collectionsArray.add(3, new Collection("da consigliare", true,
                new ArrayList<>(Collections.singletonList(new OLWorkApiResponse(
                        new ArrayList<>(Arrays.asList(-1, 108274, 233884))
                )))));
        collectionsArray.add(4, new Collection("horror", true, null));

        //gestione recycler view
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        CollectionAdapter collectionAdapter = new CollectionAdapter(collectionsArray,
                new CollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onCollectionItemClick(Collection collection) {
                        Snackbar.make(view, collection.getName(), Snackbar.LENGTH_SHORT).show();
                    }
                }, requireActivity().getApplication());
        recyclerview_collections.setLayoutManager(layoutManager);
        recyclerview_collections.setAdapter(collectionAdapter);
    }

    public void runCollectionCreationProcess(View view) {
        ConstraintLayout createCollection = view.findViewById(R.id.createCollection);
        CollectionCreationBottomSheet collectionCreationBottomSheet = new CollectionCreationBottomSheet();
        collectionCreationBottomSheet.onInputListener(this);

        createCollection.setOnClickListener( v -> {
            collectionCreationBottomSheet.show(getChildFragmentManager(), collectionCreationBottomSheet.getTag());
        });
    }
}