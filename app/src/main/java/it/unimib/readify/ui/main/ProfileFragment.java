package it.unimib.readify.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import it.unimib.readify.R;
import it.unimib.readify.adapter.CollectionAdapter;
import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.databinding.FragmentProfileBinding;
import it.unimib.readify.model.Collection;
import it.unimib.readify.model.Result;
import it.unimib.readify.model.User;
import it.unimib.readify.viewmodel.UserViewModel;
import it.unimib.readify.viewmodel.UserViewModelFactory;
import it.unimib.readify.util.ServiceLocator;

public class ProfileFragment extends Fragment implements CollectionCreationBottomSheet.OnInputListener {

    private FragmentProfileBinding fragmentProfileBinding;
    private UserViewModel userViewModel;
    private CollectionAdapter collectionAdapter;
    private User user;

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

        //initializing repository and viewModel
        IUserRepository userRepository = ServiceLocator.getInstance()
                .getUserRepository(requireActivity().getApplication());
        userViewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(userRepository)).get(UserViewModel.class);

        if(userViewModel.getLoggedUser() != null) {
            user = userViewModel.getLoggedUser();
            userViewModel.getUserDataFromToken("utente1").observe(getViewLifecycleOwner(), result -> {
                if(result.isSuccess()) {
                    user = ((Result.UserSuccess) result).getData();
                    Log.d("user data", user.toString());
                } else {
                    Log.d("error", "error");
                }
            });
        }


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