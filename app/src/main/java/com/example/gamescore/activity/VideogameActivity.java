package com.example.gamescore.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;

import com.example.gamescore.R;

public class VideogameActivity extends AppCompatActivity {

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_videogame);

        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bundle = getIntent().getExtras();
        boolean addGame = false;
        if (bundle != null)
            addGame = bundle.getBoolean("add", false);
        if (addGame) {
            Navigation.findNavController(this, R.id.nav_host_videogame).navigate(R.id.addVideogameFragment);
        }
    }
}