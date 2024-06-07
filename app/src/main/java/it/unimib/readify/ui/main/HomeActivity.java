package it.unimib.readify.ui.main;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashMap;
import java.util.Map;

import it.unimib.readify.R;
import it.unimib.readify.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity{

    private ActivityHomeBinding activityHomeBinding;
    private final Map<Integer, Long> lastClickTimeMap = new HashMap<>();
    private final Map<Integer, Integer> rootDestinations = new HashMap<>();
    private static final long DOUBLE_CLICK_THRESHOLD = 300L;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityHomeBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(activityHomeBinding.getRoot());

        MaterialToolbar toolbar = activityHomeBinding.topAppbarHome;
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().
                findFragmentById(activityHomeBinding.fragmentContainerViewHome.getId());

        navController = null;
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

            rootDestinations.put(R.id.homeFragment, R.id.homeFragment);
            rootDestinations.put(R.id.searchFragment, R.id.searchFragment);
            rootDestinations.put(R.id.profileFragment, R.id.profileFragment);

            bottomNavigationView.setOnItemReselectedListener(item -> {
                int itemId = item.getItemId();
                long currentTime = SystemClock.elapsedRealtime();
                Long lastClickTime = lastClickTimeMap.get(itemId);
                lastClickTime = ( lastClickTime == null ) ? 0L : lastClickTime;

                if (!rootDestinations.containsKey(itemId)) {
                    Log.w("HomeActivity", "Item ID " + itemId + " not found in rootDestinations.");
                    navController.navigate(itemId);
                    lastClickTimeMap.put(itemId, currentTime);
                }

                if (currentTime - lastClickTime < DOUBLE_CLICK_THRESHOLD) {
                    Integer rootDestinationObj = rootDestinations.get(itemId);
                    if (rootDestinationObj != null && navController.getCurrentDestination() != null) {
                        int rootDestination = rootDestinationObj;
                        if (navController.getCurrentDestination().getId() != rootDestination) {
                            navController.popBackStack(rootDestination, false);
                        }
                    }
                } else {
                    navController.navigate(itemId);
                }
                lastClickTimeMap.put(itemId, currentTime);
            });

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