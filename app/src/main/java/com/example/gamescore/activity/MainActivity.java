package com.example.gamescore.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.example.gamescore.R;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.model.Videogame;
import com.example.gamescore.dialog.MiDialogDeleteAccount;
import com.example.gamescore.fragment.main.SettingsFragment;
import com.example.gamescore.fragment.main.home.DiscoverFragment;
import com.example.gamescore.fragment.main.home.HomeFragment;
import com.example.gamescore.fragment.main.home.ReviewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener,
        DiscoverFragment.MiOnFragmentClickListener, MiDialogDeleteAccount.MiDialogDeleteListener,
        DiscoverFragment.MiTabDiscoverListener, ReviewFragment.MiPostTabListener {

    BottomNavigationView bottomNav;
    FloatingActionButton addVideogame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(this);
        addVideogame = findViewById(R.id.add_videogame);
        addVideogame.setOnClickListener(v -> {
            Intent intent = new Intent(this, VideogameActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("add", true);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        inicializarConstantes();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.homeFragment);
        } else if (id == R.id.currently) {
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.currentlyFragment);
            addVideogame.setVisibility(View.GONE);
        } else if (id == R.id.settings) {
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.settingsFragment);
            addVideogame.setVisibility(View.GONE);
        } else if (id == R.id.profile) {
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.profileFragment);
            addVideogame.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.appbar_search).getActionView();
        ComponentName component = new ComponentName(this, SearchResultActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(component));
        return true;
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
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onDeleteOk() {
        getSettingsFragment().preferencesClick();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Constantes.login = false;
        preferences.edit().putBoolean("login", false).apply();
    }

    @Override
    public void onDeleteCancel() {
        Toast.makeText(getApplicationContext(), "Has cancelado la acción", Toast.LENGTH_SHORT).show();
    }

    private void inicializarConstantes() {
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(this);
        Constantes.login = preferencias.getBoolean("login", false);
        Constantes.loggedUser = preferencias.getString(getString(R.string.key_username), "Not logged in");
    }

    @Override
    public void onTabDiscoverSelected(int cant) {
        if (cant <= 0) {
            getHomeFragment().isTabEmpty();
        } else {
            getHomeFragment().isTabFull();
        }
        addVideogame.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTabPostSelected(int cant) {
        if (cant <= 0) {
            getHomeFragment().isTabEmpty();
        } else {
            getHomeFragment().isTabFull();
        }
        addVideogame.setVisibility(View.GONE);
    }

    private HomeFragment getHomeFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_main);
        if (fragment instanceof NavHostFragment) {
            Fragment current = fragment.getChildFragmentManager().getFragments().get(0);
            if (current instanceof HomeFragment)
                return (HomeFragment) current;
        }
        return null;
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
}