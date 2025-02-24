package com.example.gamescore.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gamescore.R;
import com.example.gamescore.data.model.Game;

import java.util.List;

public class MiGameAdapter extends RecyclerView.Adapter<MiGameAdapter.ViewHolder> {

    public interface MiOnVideogameClickedListener {
        void onVideogameClicked(Game game);
    }

    private MiOnVideogameClickedListener miListener;
    private final List<Game> mValues;

    public MiGameAdapter(List<Game> items, MiOnVideogameClickedListener miListener) {
        mValues = items;
        this.miListener = miListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_game, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mVideogameSinopsis.setText(mValues.get(position).getSinopsis());
        holder.mVideogameName.setText(mValues.get(position).getName());
        double ratingNum = mValues.get(position).getRating();
        String rating = String.format("Has a media rating of %.2f", ratingNum);
        holder.mVideogameRating.setText(rating);
        holder.mVideogameImg.setImageDrawable(mValues.get(position).getImagen());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public Game mItem;
        public final TextView mVideogameName;
        public final TextView mVideogameSinopsis;
        public final TextView mVideogameRating;
        public final ImageView mVideogameImg;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mVideogameImg = view.findViewById(R.id.videogame_img);
            mVideogameName = view.findViewById(R.id.videogame_name);
            mVideogameSinopsis = view.findViewById(R.id.videogame_sinopsis);
            mVideogameRating = view.findViewById(R.id.videogame_rating);
            mView.setOnClickListener(v -> {
                miListener.onVideogameClicked(mItem);
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mVideogameName.getText() + "'";
        }
    }
}