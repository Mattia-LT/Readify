package it.unimib.readify.ui.main;

import static it.unimib.readify.util.Constants.COLLECTION;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.databinding.FragmentUserDetailsBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.OLWorkApiResponse;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.BookViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModel;
import it.unimib.readify.viewmodel.TestDatabaseViewModelFactory;

public class UserDetailsFragment extends Fragment {

    private FragmentUserDetailsBinding binding;
    private TestDatabaseViewModel testDatabaseViewModel;
    private BookViewModel bookViewModel;
    private CollectionAdapter collectionAdapter;
    private List<Collection> publicCollections;

    public UserDetailsFragment() {}

    public static UserDetailsFragment newInstance() {
        return new UserDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserDetailsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadMenu();
        initRepositories();
        initRecyclerView(view);
        fetchUserData();
    }


    private void loadMenu(){
        // Set up the toolbar and remove all icons
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.top_appbar_home);
        // Enable the back button
        Drawable coloredIcon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_arrow_back_24);
        int newColor = getResources().getColor(R.color.white, null);
        if (coloredIcon != null) {
            coloredIcon.setColorFilter(newColor, PorterDuff.Mode.SRC_IN);
        }
        toolbar.setNavigationIcon(coloredIcon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

    }

    private void initRepositories(){
        //initializing viewModels
        testDatabaseViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(TestDatabaseViewModel.class);

        bookViewModel = TestDatabaseViewModelFactory.getInstance(requireActivity().getApplication())
                .create(BookViewModel.class);
    }

    private void initRecyclerView(View view){
        publicCollections = new ArrayList<>();
        collectionAdapter = new CollectionAdapter(
                new CollectionAdapter.OnItemClickListener() {
                    @Override
                    public void onCollectionItemClick(Collection collection) {
                        Navigation.findNavController(view).navigate(R.id.action_userDetailsFragment_to_collectionFragment);
                    }
                }, requireActivity().getApplication());

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recyclerviewUserCollections.setLayoutManager(layoutManager);
        binding.recyclerviewUserCollections.setAdapter(collectionAdapter);
        collectionAdapter.setCollectionsList(publicCollections);
    }
    private void fetchUserData(){
        User receivedUser = UserDetailsFragmentArgs.fromBundle(getArguments()).getUser();
        String username = UserDetailsFragmentArgs.fromBundle(getArguments()).getUsername();
        requireActivity().setTitle(username);
        showUserInfo(receivedUser);
    }

    private void showUserInfo(User user){
        binding.textviewFollowerCounter.setText(String.valueOf(user.getFollowers().getCounter()));
        binding.textviewFollowingCounter.setText(String.valueOf(user.getFollowing().getCounter()));
        binding.textviewUserUsername.setText(user.getUsername());
        binding.textviewUserBiography.setText(user.getBiography());
        int avatarId;
        try {
            avatarId = R.drawable.class.getDeclaredField(user.getAvatar().toLowerCase()).getInt(null);
        } catch (Exception e) {
            avatarId = R.drawable.ic_baseline_profile_24;
        }
        Glide.with(requireActivity().getApplicationContext())
                .load(avatarId)
                .dontAnimate()
                .into(binding.avatarImageView);

        // todo devo ancora implementare
        //loadUserCollections(user);

    }

    private void loadUserCollections(User user) {
        //get books from api
        int counter = 0;
        for (int i = 0; i < user.getFetchedCollections().size(); i++) {
            int finalCounter = counter;
            bookViewModel.fetchBooks(user.getFetchedCollections().get(i).getBooks(), COLLECTION)
                    .observe(getViewLifecycleOwner(), resultsList -> {
                        for (int j = 0; j < resultsList.size(); j++) {
                            if (resultsList.get(j).isSuccess()) {
                                OLWorkApiResponse book = ((Result.WorkSuccess) resultsList.get(j)).getData();
                                user.getFetchedCollections().get(finalCounter).getWorks().add(j, book);
                            }
                        }
                    });
            counter++;
        }

        publicCollections = user.getFetchedCollections();
        collectionAdapter.setCollectionsList(publicCollections);
        //todo
    }
}