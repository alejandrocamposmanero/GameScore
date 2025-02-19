package com.example.gamescore.activitys;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;

import com.example.gamescore.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        SharedPreferences.Editor preferences = getSharedPreferences("datos", Context.MODE_PRIVATE).edit();
        if (id == R.id.home) {
            preferences.putInt("item-selected", 0);
            preferences.apply();
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.homeFragment);
            return true;
        } else if (id == R.id.currently) {
            preferences.putInt("item-selected", 1);
            preferences.apply();
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.currentlyFragment);
            return true;
        } else if (id == R.id.settings) {
            preferences.putInt("item-selected", 2);
            preferences.apply();
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.settingsFragment);
            return true;
        } else if (id == R.id.profile) {
            preferences.putInt("item-selected", 3);
            preferences.apply();
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.profileFragment);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.notifications) {
            Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        int itemSelected = getSharedPreferences("datos", Context.MODE_PRIVATE).getInt("item-selected", 0);
        switch (itemSelected) {
            case 0:
                Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.homeFragment);
                break;
            case 1:
                Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.currentlyFragment);
                break;
            case 2:
                Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.settingsFragment);
                break;
            case 3:
                Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.profileFragment);
                break;
        }
    }

}