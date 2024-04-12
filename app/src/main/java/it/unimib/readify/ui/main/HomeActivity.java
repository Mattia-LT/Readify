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

public class HomeActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityHomeBinding activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());

        MaterialToolbar toolbar = activityHomeBinding.topAppbarHome;
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(activityHomeBinding.fragmentContainerViewHome.getId());

        NavController navController = null;
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        BottomNavigationView bottomNavigationView = activityHomeBinding.bottomNavigationViewHome;

        if (navController != null) {
            AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.homeFragment,
                    R.id.searchFragment,
                    R.id.profileFragment
            ).build();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }
    }
}