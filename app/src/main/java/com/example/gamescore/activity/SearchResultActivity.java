package com.example.gamescore.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.gamescore.R;
import com.example.gamescore.data.model.Game;
import com.example.gamescore.fragment.main.home.GameFragment;

import java.util.Objects;

public class SearchResultActivity extends AppCompatActivity implements GameFragment.MiOnFragmentClickListener, GameFragment.MiResultSearchListener {

    TextView searchResult;
    TextView noResults;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_result);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.search));

        searchResult = findViewById(R.id.search_result_text);
        noResults = findViewById(R.id.no_results);
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            bundle.putString("query", query);
        }
        GameFragment fragment = new GameFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.search_result_fragment, fragment).commit();
    }


    @Override
    public void onFragmentClick(Game game) {
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id-juego", game.getId());
        intent.putExtras(bundle);
        startActivity(intent, bundle);
    }

    @Override
    public void onResultSearch(int cant) {
        String result = cant + " " + getString(R.string.search_result_cant);
        searchResult.setText(result);
        if (cant <= 0) {
            searchResult.setVisibility(TextView.GONE);
            String noResultSearch = String.format(getString(R.string.no_results), query);
            noResults.setVisibility(TextView.VISIBLE);
            noResults.setText(noResultSearch);
            findViewById(R.id.search_result_fragment).setVisibility(TextView.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.appbar_search).getActionView();
        ComponentName component = new ComponentName(this, SearchResultActivity.class);
        Objects.requireNonNull(searchView).setSearchableInfo(searchManager.getSearchableInfo(component));
        return true;
    }
}