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
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.model.Collection;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerview_collections;
    private ArrayList<it.unimib.readify.model.Collection> collectionsArray;

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
        recyclerview_collections = container.findViewById(R.id.recyclerview_collections);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //gestione recycler view
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        CollectionAdapter collectionAdapter = new CollectionAdapter(collectionsArray,
                new CollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onCollectionItemClick(Collection collection) {
                        Snackbar.make(view, collection.getName(), Snackbar.LENGTH_SHORT).show();
                    }
                });
        recyclerview_collections.setLayoutManager(layoutManager);
        recyclerview_collections.setAdapter(collectionAdapter);
    }
}