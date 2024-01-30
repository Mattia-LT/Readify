package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.unimib.readify.R;
import it.unimib.readify.adapter.BookItemCollectionAdapter;
import it.unimib.readify.databinding.FragmentCollectionBinding;
import it.unimib.readify.model.Collection;

public class CollectionFragment extends Fragment {

    private FragmentCollectionBinding collectionProfileBinding;
    private BookItemCollectionAdapter bookItemCollectionAdapter;
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

        //initializing adapter
        bookItemCollectionAdapter = new BookItemCollectionAdapter();

        //managing recycler view
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        collectionProfileBinding.collectionFragmentBooksRecyclerView.setLayoutManager(layoutManager);
        collectionProfileBinding.collectionFragmentBooksRecyclerView.setAdapter(bookItemCollectionAdapter);

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

        //managing backButton
        collectionProfileBinding.backButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_collectionFragment_to_profileFragment);
        });
    }
}
