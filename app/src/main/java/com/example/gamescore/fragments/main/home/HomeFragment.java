package com.example.gamescore.fragments.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gamescore.R;
import com.example.gamescore.adapters.MiTabsHomeAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new MiTabsHomeAdapter(this));
        String[] opciones = getResources().getStringArray(R.array.home_opciones);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(opciones[position]);
        }).attach();
        return view;
    }
}