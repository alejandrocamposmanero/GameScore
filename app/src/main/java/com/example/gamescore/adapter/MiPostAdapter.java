package com.example.gamescore.adapter;

import android.database.Cursor;
import android.database.sqlite.SQLiteBlobTooBigException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gamescore.R;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;
import com.example.gamescore.data.model.Post;

import java.util.List;

public class MiPostAdapter extends RecyclerView.Adapter<MiPostAdapter.ViewHolder> {

    public interface MiOnPostClickedListener {
        void onPostClicked(Post post);
    }

    private MiOnPostClickedListener miListener;
    private final List<Post> mValues;

    public MiPostAdapter(List<Post> items, MiOnPostClickedListener miListener) {
        mValues = items;
        this.miListener = miListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_post, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        SQLiteDatabase db = openDB(holder.mView);
        Drawable videogameImg = getVideogameImg(db, holder.mItem.getIdJuego(), holder.mView);
        Drawable profilePic = getUserProfilePic(db, holder.mItem.getIdUser(), holder.mView);
        String videogameName = getVideogameName(db, holder.mItem.getIdJuego());
        String username = getUsername(db, holder.mItem.getIdUser());
        String review;
        String usernameAction;

        switch (holder.mItem.getTag()) {
            case UNDEFINED:
                usernameAction = "Not saved";
                holder.mRatingBar.setVisibility(View.VISIBLE);
                review = getVideogameSinopsis(db, holder.mItem.getIdJuego());
                break;
            case TO_PLAY:
                usernameAction = "Has saved to play";
                holder.mRatingBar.setVisibility(View.GONE);
                holder.mRatingText.setVisibility(View.INVISIBLE);
                review = getVideogameSinopsis(db, holder.mItem.getIdJuego());
                break;
            case PLAYING:
                usernameAction = "Is playing";
                holder.mRatingText.setVisibility(View.INVISIBLE);
                holder.mRatingBar.setVisibility(View.GONE);
                review = getVideogameSinopsis(db, holder.mItem.getIdJuego());
                break;
            case PLAYED:
                usernameAction = "Has rated with ";
                holder.mRatingBar.setVisibility(View.VISIBLE);
                review = holder.mItem.getPostMessage();
                if (review == null || review.isEmpty() || review.isBlank())
                    review = getVideogameSinopsis(db, holder.mItem.getIdJuego());
                break;
            default:
                usernameAction = "Not found";
                holder.mRatingBar.setVisibility(View.VISIBLE);
                review = "Not found";
        }
        db.close();
        SpannableStringBuilder str = new SpannableStringBuilder(username + " " + usernameAction.toLowerCase());
        str.setSpan(new android.text.style.StyleSpan(Typeface.BOLD_ITALIC), 0, username.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.mReview.setText(review);
        holder.mProfilePic.setImageDrawable(profilePic);
        holder.mVideogameImg.setImageDrawable(videogameImg);
        holder.mRatingBar.setRating((float) holder.mItem.getRating());
        holder.mUsernameAction.setText(str);
        holder.mVideogameName.setText(videogameName);
        holder.mRatingText.setText(usernameAction + " " + holder.mItem.getRating());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public Post mItem;
        public ImageView mProfilePic;
        public ImageView mVideogameImg;
        public RatingBar mRatingBar;
        public TextView mUsernameAction;
        public TextView mReview;
        public TextView mVideogameName;
        public TextView mRatingText;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mProfilePic = view.findViewById(R.id.user_icon);
            mVideogameImg = view.findViewById(R.id.videogame_img);
            mRatingBar = view.findViewById(R.id.rating_bar);
            mUsernameAction = view.findViewById(R.id.username);
            mReview = view.findViewById(R.id.videogame_sinopsis);
            mVideogameName = view.findViewById(R.id.videogame_name);
            mRatingText = view.findViewById(R.id.videogame_rating);
            mView.setOnClickListener(v -> miListener.onPostClicked(mItem));
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mUsernameAction.getText() + " has rated " + mVideogameName.getText() + "'";
        }
    }

    private Drawable getVideogameImg(SQLiteDatabase db, int id, View view) {
        Drawable profilePic = null;
        Cursor fila = db.rawQuery("SELECT imagen FROM juegos WHERE id_juego=" + id, null);
        if (fila.moveToFirst()) {
            try {
                byte[] img = fila.getBlob(0);
                Bitmap imagen = BitmapFactory.decodeByteArray(img, 0, img.length);
                profilePic = new BitmapDrawable(view.getResources(), imagen);
            } catch (SQLiteBlobTooBigException sqlbtbe) {
                db.delete("juegos", "id_juego=" + fila.getInt(0), null);
            }
        } else {
            profilePic = view.getResources().getDrawable(R.drawable.videogame_default);
        }
        fila.close();
        return profilePic;
    }

    private String getVideogameName(SQLiteDatabase db, int id) {
        String name = "";
        Cursor fila = db.rawQuery("SELECT nombre FROM juegos WHERE id_juego=" + id, null);
        if (fila.moveToFirst())
            name = fila.getString(0);
        else
            name = "Not found";
        fila.close();
        return name;
    }

    private String getVideogameSinopsis(SQLiteDatabase db, int id) {
        String sinopsis = "";
        Cursor fila = db.rawQuery("SELECT sinopsis FROM juegos WHERE id_juego=" + id, null);
        if (fila.moveToFirst())
            sinopsis = fila.getString(0);
        else
            sinopsis = "Not found";
        fila.close();
        return sinopsis;
    }

    private Drawable getUserProfilePic(SQLiteDatabase db, int id, View view) {
        Drawable profilePic = null;
        Cursor fila = db.rawQuery("SELECT profile_pic FROM usuarios WHERE id_user=" + id, null);
        if (fila.moveToFirst()) {
            try {
                byte[] img = fila.getBlob(0);
                Bitmap imagen = BitmapFactory.decodeByteArray(img, 0, img.length);
                profilePic = new BitmapDrawable(view.getResources(), imagen);
            } catch (SQLiteBlobTooBigException sqlbtbe) {
                db.delete("juegos", "id_juego=" + fila.getInt(0), null);
            }
        } else {
            profilePic = view.getResources().getDrawable(R.drawable.videogame_default);
        }
        fila.close();
        return profilePic;
    }

    private String getUsername(SQLiteDatabase db, int id) {
        String username = "";
        Cursor fila = db.rawQuery("SELECT display_name FROM usuarios WHERE id_user=" + id, null);
        if (fila.moveToFirst())
            username = fila.getString(0);
        else
            username = "Not found";
        fila.close();
        return username;
    }

    private SQLiteDatabase openDB(View view) {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(view.getContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }
}