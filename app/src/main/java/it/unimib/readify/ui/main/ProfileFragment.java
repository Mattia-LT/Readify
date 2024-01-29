package it.unimib.readify.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.databinding.FragmentProfileBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;

public class ProfileFragment extends Fragment implements CollectionCreationBottomSheet.OnInputListener {

    private ArrayList<it.unimib.readify.model.Collection> collectionsArray;
    private Collection newCollection;
    private FragmentProfileBinding fragmentProfileBinding;

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
        fragmentProfileBinding = FragmentProfileBinding.inflate(inflater,container,false);
        return fragmentProfileBinding.getRoot();
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

    //managing collections existence
    public void runCollectionsView(View view) {
        //test data
        collectionsArray = new ArrayList<>();
        collectionsArray.add(0, new Collection("horror", true,
                new ArrayList<>(Collections.singletonList(new OLWorkApiResponse(
                        new ArrayList<>(Arrays.asList(-1, 6498519, 8904777))
                )))));
        collectionsArray.add(1, new Collection("fantasy", true,
                new ArrayList<>(Collections.singletonList(null))));
        collectionsArray.add(2, new Collection("favourites", false, null));
        collectionsArray.add(3, new Collection("to recommend", true,
                new ArrayList<>(Collections.singletonList(new OLWorkApiResponse(
                        new ArrayList<>(Arrays.asList(-1, 108274, 233884))
                )))));
        collectionsArray.add(4, new Collection("horror", true, null));

        //managing recycler view
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        CollectionAdapter collectionAdapter = new CollectionAdapter(collectionsArray,
                new CollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onCollectionItemClick(Collection collection) {
                        Snackbar.make(view, collection.getName(), Snackbar.LENGTH_SHORT).show();
                    }
                }, requireActivity().getApplication());
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
}