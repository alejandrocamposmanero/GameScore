package com.example.gamescore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gamescore.R;
import com.example.gamescore.model.Videogame;

import java.util.List;

public class MyVideogameAdapter extends RecyclerView.Adapter<MyVideogameAdapter.ViewHolder> {

    public interface MiOnVideogameClickedListener {
        void onVideogameClicked(Videogame videogame);
    }

    private MiOnVideogameClickedListener miListener;
    private final List<Videogame> mValues;

    public MyVideogameAdapter(List<Videogame> items, MiOnVideogameClickedListener miListener) {
        mValues = items;
        this.miListener = miListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_discover, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Videogame mItem;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        public String toString() {
            return super.toString() + " ''";
        }
    }
}