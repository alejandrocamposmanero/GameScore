package com.example.gamescore.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.gamescore.R;

public class LoginActivity extends AppCompatActivity {

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_login);
        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            String title = getString(R.string.app_name);
            int idDestination = navDestination.getId();
            if (idDestination == R.id.forgotPasswordFragment)
                title = getString(R.string.forgot_password);
            else if (idDestination == R.id.registerFragment)
                title = getString(R.string.register);
            else if (idDestination == R.id.loginFragment)
                title = getString(R.string.login_title);
            else if (idDestination == R.id.setupProfileFragment)
                title = getString(R.string.setup_profile);
            getSupportActionBar().setTitle(title);
        });

        bundle = getIntent().getExtras();
        Log.d("bundle", "bundle=" + bundle);
        boolean changePassword = false;
        if (bundle != null)
            changePassword = bundle.getBoolean("change-password", false);
        if (changePassword) {
            navController.navigate(R.id.forgotPasswordFragment, bundle);
        }
    }
}