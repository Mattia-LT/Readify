package it.unimib.readify.ui.main;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.unimib.readify.R;
import it.unimib.readify.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity{

    private ActivityHomeBinding activityHomeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
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

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                boolean isTopLevelDestination = appBarConfiguration.getTopLevelDestinations().contains(destination.getId());
                if(!isTopLevelDestination){
                    Drawable coloredIcon = ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24);
                    int whiteColor = getResources().getColor(R.color.white, null);
                    if (coloredIcon != null) {
                        coloredIcon.setColorFilter(whiteColor, PorterDuff.Mode.SRC_IN);
                        toolbar.setNavigationIcon(coloredIcon);
                    }
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(activityHomeBinding.fragmentContainerViewHome.getId());

        NavController navController;
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return false;
    }
}