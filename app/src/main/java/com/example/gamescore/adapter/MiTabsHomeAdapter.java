package com.example.gamescore.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.gamescore.fragment.main.home.DiscoverFragment;
import com.example.gamescore.fragment.main.home.ReviewFragment;

public class MiTabsHomeAdapter extends FragmentStateAdapter {

    public MiTabsHomeAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ReviewFragment();
                break;
            case 1:
                fragment = new DiscoverFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
