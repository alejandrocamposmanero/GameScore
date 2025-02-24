package com.example.gamescore.activity;

import android.app.SearchManager;
import android.content.ComponentName;
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
import androidx.preference.PreferenceManager;

import com.example.gamescore.R;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.model.Game;
import com.example.gamescore.data.model.Post;
import com.example.gamescore.dialog.MiDialogDeleteAccount;
import com.example.gamescore.fragment.main.ProfileFragment;
import com.example.gamescore.fragment.main.SettingsFragment;
import com.example.gamescore.fragment.main.home.GameFragment;
import com.example.gamescore.fragment.main.home.HomeFragment;
import com.example.gamescore.fragment.main.home.PostFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener,
        GameFragment.MiOnFragmentClickListener, MiDialogDeleteAccount.MiDialogDeleteListener,
        GameFragment.MiTabDiscoverListener, PostFragment.MiPostTabListener,
        PostFragment.MiFragmentClickListener, PostFragment.MiPostsEmptyListener {

    public static boolean isHomeFragment = true;
    public static boolean isProfileFragment = false;
    public static boolean isMyGamesFragment = false;
    private BottomNavigationView bottomNav;
    private FloatingActionButton addVideogame;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(this);
        bundle = getIntent().getExtras();
        boolean goDiscover = false;
        if (bundle != null) {
            goDiscover = bundle.getBoolean("go-discover", false);
        }
        if (goDiscover)
            ((HomeFragment) getCurrentFragment()).goDiscover();
        inicializarConstantes();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.homeFragment);
        } else if (id == R.id.currently) {
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.myGamesFragment);
        } else if (id == R.id.settings) {
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.settingsFragment);
        } else if (id == R.id.profile) {
            Navigation.findNavController(this, R.id.nav_host_main).navigate(R.id.profileFragment);
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
        isHomeFragment = false;
        isProfileFragment = false;
        isMyGamesFragment = false;
        return true;
    }

    @Override
    public void onFragmentClick(Game game) {
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id-juego", game.getId());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onDeleteOk() {
        ((SettingsFragment) getCurrentFragment()).preferencesClick();
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
        if (isHomeFragment)
            if (cant <= 0) {
                ((HomeFragment) getCurrentFragment()).isTabEmpty();
            } else {
                ((HomeFragment) getCurrentFragment()).isTabFull();
            }
    }

    @Override
    public void onTabPostSelected(int cant) {
        if (isHomeFragment)
            if (cant <= 0) {
                ((HomeFragment) getCurrentFragment()).isTabEmpty();
            } else {
                ((HomeFragment) getCurrentFragment()).isTabFull();
            }
    }

    public void onPostsMyGamesEmpty() {
        ((PostFragment) getCurrentFragment()).noGames();

    }

    @Override
    public void onPostsProfileEmpty() {
        ((ProfileFragment) getCurrentFragment()).hidePosts();
    }

    private Fragment getCurrentFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_main);
        if (fragment instanceof NavHostFragment) {
            return fragment.getChildFragmentManager().getFragments().get(0);
        }
        return null;
    }

    @Override
    public void onFragmentClick(Post post) {
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id-juego", post.getIdJuego());
        intent.putExtras(bundle);
        startActivity(intent);
        isHomeFragment = false;
        isProfileFragment = false;
        isMyGamesFragment = false;
    }
}