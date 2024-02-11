package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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



        //repository
        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication());

        //initializing viewModel and adapter
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(userRepository))
                .get(UserViewModel.class);
        bookItemCollectionAdapter = new BookItemCollectionAdapter(
                new BookItemCollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onBookItemClick(OLWorkApiResponse book) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("book", book);
                        Navigation.findNavController(view).navigate(R.id.action_collectionFragment_to_bookDetailsFragment, bundle);
                    }
                }, requireActivity().getApplication());

        //managing recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        collectionProfileBinding.collectionFragmentBooksRecyclerView.setLayoutManager(layoutManager);
        collectionProfileBinding.collectionFragmentBooksRecyclerView.setAdapter(bookItemCollectionAdapter);

        //managing viewModel

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

        //managing backButton
        collectionProfileBinding.backButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_collectionFragment_to_profileFragment);
        });
    }
}
