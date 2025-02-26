package com.example.gamescore.fragment.videogame;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteBlobTooBigException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.gamescore.R;
import com.example.gamescore.activity.GameActivity;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;
import com.example.gamescore.data.model.Game;
import com.example.gamescore.data.model.Post;

public class ReviewFragment extends Fragment {

    private Bundle bundle;
    private int idJuego;
    private int idPost;

    public ReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedDispatcher onBack = requireActivity().getOnBackPressedDispatcher();
        onBack.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(requireActivity(), R.id.nav_host_videogame).navigate(R.id.showGameFragment, getArguments());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GameActivity.isShowGameFragment = false;
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        RatingBar rating = view.findViewById(R.id.select_rating);
        EditText reviewText = view.findViewById(R.id.write_review);
        bundle = getArguments();
        loadReview(view);
        Button saveReview = view.findViewById(R.id.save_review);
        saveReview.setOnClickListener(v -> {
            float rated = rating.getRating();
            String reviewMsg = reviewText.getText().toString();
            updateReview(rated, reviewMsg);
            Navigation.findNavController(requireActivity(), R.id.nav_host_videogame).navigate(R.id.showGameFragment, bundle);
        });
        return view;
    }

    private void loadReview(View view) {
        if (bundle != null) {
            idJuego = bundle.getInt("id-juego", -1);
            idPost = bundle.getInt("id-post", -1);
            if (idPost == -1) {
                idPost = getIdPost();
            }
        }
        ImageView gameImg = view.findViewById(R.id.review_game_img);
        TextView gameName = view.findViewById(R.id.review_game_name);
        RatingBar rating = view.findViewById(R.id.select_rating);
        EditText reviewText = view.findViewById(R.id.write_review);

        gameImg.setImageDrawable(getGame().getImagen());
        gameName.setText(getGame().getName());
        rating.setRating((float) getReview().getRating());
        reviewText.setText(getReview().getPostMessage());
    }

    private void updateReview(float rating, String review) {
        SQLiteDatabase db = openDB();
        ContentValues registro = new ContentValues();
        registro.put("rating", rating);
        registro.put("resena", review);
        int cant = db.update("posts", registro, "id_post=" + idPost, null);
        if (cant > 0) {
            Toast.makeText(requireActivity(), "Review saved correctly", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireActivity(), "Couldn't save the review", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private int getIdPost() {
        int idPost = -1;
        int idUser = getIdUser();
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_post FROM posts WHERE id_juego=" + idJuego + " and id_user=" + idUser, null);
        if (fila.moveToFirst()) {
            idPost = fila.getInt(0);
        }
        fila.close();
        db.close();
        return idPost;
    }

    private int getIdUser() {
        int idUser = -1;
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_user FROM usuarios WHERE username='" + Constantes.loggedUser + "'", null);
        if (fila.moveToFirst()) {
            idUser = fila.getInt(0);
        }
        fila.close();
        db.close();
        return idUser;
    }

    private Game getGame() {
        Game juego = null;
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT imagen, nombre, sinopsis, nota_media FROM juegos WHERE id_juego=" + idJuego, null);
        if (fila.moveToFirst()) {
            try {
                byte[] img = fila.getBlob(0);
                Bitmap imagen = BitmapFactory.decodeByteArray(img, 0, img.length);
                Drawable videogameImg = new BitmapDrawable(getResources(), imagen);
                juego = new Game(idJuego, fila.getString(1), fila.getString(2), videogameImg, fila.getDouble(3));
            } catch (SQLiteBlobTooBigException sqlbtbe) {
                db.delete("juegos", "id_juego=" + fila.getInt(0), null);
            }
        } else {
            juego = new Game(idJuego);
        }
        fila.close();
        db.close();
        return juego;
    }

    private Post getReview() {
        Post post;
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_user, rating, resena FROM posts WHERE id_post=" + idPost, null);
        if (fila.moveToFirst()) {
            post = new Post(idPost, fila.getString(2), fila.getDouble(1), Post.Tag.PLAYED, fila.getInt(0), idJuego);
        } else {
            post = new Post(idPost);
        }
        fila.close();
        db.close();
        return post;
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(requireContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }
}