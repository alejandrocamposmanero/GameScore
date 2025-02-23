package com.example.gamescore.fragment.main.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gamescore.R;
import com.example.gamescore.adapter.MiTabsHomeAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager;
    private TextView tabEmpty;

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

        tabEmpty = view.findViewById(R.id.tab_empty);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
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
}