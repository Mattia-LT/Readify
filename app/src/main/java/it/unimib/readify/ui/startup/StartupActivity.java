package it.unimib.readify.ui.startup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import it.unimib.readify.R;
import it.unimib.readify.ui.main.HomeActivity;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        Button btn_getting_started = findViewById(R.id.button_startup);

        btn_getting_started.setOnClickListener(view -> {
            Intent intent = new Intent(this, HomeActivity.class);
            // intent.putExtra(key, value);



            // Dobbiamo usare i bundles, lo sistemo appena recupero le esercitazioni
            // Bundle bundle = new Bundle();
            // intent.putExtras(bundle);


            startActivity(intent);
            //finish();
        });





    }
}
