package it.unimib.readify.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.unimib.readify.R;
import it.unimib.readify.databinding.ActivityHomeBinding;
public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialToolbar toolbar = binding.topAppbarHome;
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(binding.fragmentContainerViewHome.getId());

        NavController navController = null;
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
        BottomNavigationView bottomNavigationView = binding.bottomNavigationViewHome;

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.searchFragment,
                R.id.profileFragment
        ).build();

        //For the toolbar
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //For the bottom navigation view
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}
