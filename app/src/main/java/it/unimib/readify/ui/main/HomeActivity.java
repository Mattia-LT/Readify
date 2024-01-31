package it.unimib.readify.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.unimib.readify.R;
import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.util.ServiceLocator;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.top_appbar_home);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(R.id.fragment_container_view_home);

        NavController navController = null;
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view_home);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.searchFragment,
                R.id.profileFragment,
                R.id.favoritesFragment
        ).build();

        //For the toolbar
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //For the bottom navigation view
        NavigationUI.setupWithNavController(bottomNavigationView, navController);



        //initializing userRepository and userViewModel
        /*
        IUserRepository userRepository = ServiceLocator.getInstance().getUserRepository();
        UserViewModel userViewModel = new ViewModelProvider(this, new DataViewModelFactory(userRepository))
                .get(UserViewModel.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("userRepository", userRepository);
        bundle.putParcelable("userViewModel", userViewModel);
         */


    }
}
