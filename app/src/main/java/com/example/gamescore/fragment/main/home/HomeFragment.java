package com.example.gamescore.fragment.main.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gamescore.R;
import com.example.gamescore.activity.GameActivity;
import com.example.gamescore.activity.MainActivity;
import com.example.gamescore.adapter.MiTabsHomeAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private TextView tabEmpty;
    private TabLayout tabLayout;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.isHomeFragment = true;
        MainActivity.isProfileFragment = false;
        MainActivity.isMyGamesFragment = false;
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        FloatingActionButton fab = view.findViewById(R.id.add_videogame);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), GameActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("add", true);
            intent.putExtras(bundle);
            startActivity(intent);
            MainActivity.isHomeFragment = false;
        });

        tabEmpty = view.findViewById(R.id.tab_empty);
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new MiTabsHomeAdapter(this));
        String[] opciones = getResources().getStringArray(R.array.home_opciones);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(opciones[position]);
        }).attach();
        return view;
    }

    public void isTabEmpty() {
        viewPager.setVisibility(View.INVISIBLE);
        tabEmpty.setVisibility(TextView.VISIBLE);
    }

    public void isTabFull() {
        viewPager.setVisibility(View.VISIBLE);
        tabEmpty.setVisibility(TextView.GONE);
    }

    public void goDiscover() {
        tabLayout.selectTab(tabLayout.getTabAt(1));
    }
}