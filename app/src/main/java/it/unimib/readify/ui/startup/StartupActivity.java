package it.unimib.readify.ui.startup;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.unimib.readify.databinding.ActivityStartupBinding;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStartupBinding binding = ActivityStartupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //TODO controllo login -> avanzo skippando questa parte
        binding.buttonStartup.setOnClickListener(view -> {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });





    }
}
