package com.example.gamescore.activitys;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.gamescore.R;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.dialogs.MiDialogDeleteAccount;
import com.example.gamescore.fragments.main.SettingsFragment;
import com.example.gamescore.fragments.main.home.DiscoverFragment;
import com.example.gamescore.model.Videogame;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener,
        DiscoverFragment.MiOnFragmentClickListener, MiDialogDeleteAccount.MiDialogDeleteListener {

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
        inicializarConstantes();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.homeFragment);
            return true;
        } else if (id == R.id.currently) {
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.currentlyFragment);
            return true;
        } else if (id == R.id.settings) {
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.settingsFragment);
            return true;
        } else if (id == R.id.profile) {
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
    public void onFragmentClick(Videogame videogame) {
        Intent intent = new Intent(getApplicationContext(), VideogameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("videogame-id", videogame.getId());
        startActivity(intent, bundle);
    }

    @Override
    public void onDeleteOk() {
        getSettingsFragment().preferencesClick();
        SharedPreferences preferences = getSharedPreferences(Constantes.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        Constantes.login = false;
        preferences.edit().putBoolean("login", false).apply();
    }

    @Override
    public void onDeleteCancel() {
        Toast.makeText(getApplicationContext(), "Has cancelado la acción", Toast.LENGTH_SHORT).show();
    }

    private SettingsFragment getSettingsFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_main);
        if (fragment instanceof NavHostFragment) {
            Fragment current = fragment.getChildFragmentManager().getFragments().get(0);
            if (current instanceof SettingsFragment)
                return (SettingsFragment) current;
        }
        return null;
    }

    private void inicializarConstantes() {
        SharedPreferences preferencias = getSharedPreferences(Constantes.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        Constantes.login = preferencias.getBoolean("login", false);
        Constantes.loggedUser = preferencias.getString("username", "Not logged in");
    }

}