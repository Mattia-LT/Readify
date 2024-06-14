package it.unimib.readify.ui.startup;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import it.unimib.readify.R;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }
}