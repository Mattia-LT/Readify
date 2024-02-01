package it.unimib.readify.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import it.unimib.readify.R;
import it.unimib.readify.data.repository.user.IUserRepository;
import it.unimib.readify.databinding.ActivityHomeBinding;
import it.unimib.readify.model.User;
import it.unimib.readify.util.ServiceLocator;
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
