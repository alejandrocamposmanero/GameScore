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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
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
import com.example.gamescore.fragment.main.home.PostFragment;

import java.util.Date;

public class ShowGameFragment extends Fragment {

    private Bundle bundle;
    private FrameLayout gamePosts;
    private TextView noPosts;
    private int idJuego;
    private Spinner spinnerSave;
    private boolean firstOpen;

    public ShowGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedDispatcher onBack = getActivity().getOnBackPressedDispatcher();
        onBack.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().finish();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GameActivity.isShowGameFragment = true;
        View view = inflater.inflate(R.layout.fragment_show_game, container, false);
        loadGame(view);
        return view;
    }

    private void loadGame(View view) {
        bundle = getArguments();
        if (bundle != null) {
            idJuego = bundle.getInt("id-juego");
            bundle.putInt("id-user", -1);
        } else {
            bundle = new Bundle();
        }
        Game juego = getGame();
        ImageView videogameImg = view.findViewById(R.id.show_game_img);
        TextView videogameName = view.findViewById(R.id.show_game_name);
        RatingBar videogameRating = view.findViewById(R.id.show_game_rating);
        noPosts = view.findViewById(R.id.no_reviews);
        gamePosts = view.findViewById(R.id.list_reviews);
        Fragment reviewFragment = new PostFragment();
        reviewFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction().replace(R.id.list_reviews, reviewFragment).commit();
        spinnerSave = view.findViewById(R.id.spinner_save);

        videogameImg.setImageDrawable(juego.getImagen());
        videogameName.setText(juego.getName());
        videogameRating.setRating((float) juego.getRating());
        String[] opciones = getResources().getStringArray(R.array.spinner_save_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, opciones);
        spinnerSave.setAdapter(adapter);
        spinnerSave.setSelection(getCurrentTag());
        firstOpen = true;
        spinnerSave.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!firstOpen) {
                    if (Constantes.login) {
                        if (!checkReview() && position != 0) {
                            guardarReview(position);
                            updateGame();
                        } else if (position != 0) {
                            updatePostTag(position);
                            if (position != 3)
                                updateGame();
                        } else
                            deleteReview();

                        if (position == 3) {
                            Navigation.findNavController(getActivity(), R.id.nav_host_videogame).navigate(R.id.reviewVideogameFragment, bundle);
                        }
                    } else {
                        Toast.makeText(getContext(), "You must be logged in to write a review", Toast.LENGTH_SHORT).show();
                        spinnerSave.setSelection(0);
                    }
                } else {
                    firstOpen = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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

    private void guardarReview(int tag) {
        int idUser = getIdUser();
        SQLiteDatabase db = openDB();
        ContentValues registro = new ContentValues();
        registro.put("id_juego", idJuego);
        registro.put("tag", tag);
        registro.put("id_user", idUser);
        long result = db.insert("posts", null, registro);
        if (result == -1) {
            Toast.makeText(getContext(), "The review hasn't been saved", Toast.LENGTH_SHORT).show();
            spinnerSave.setSelection(0);
        } else {
            Post.Tag tagText = getPostTag(tag);
            Cursor fila = db.rawQuery("SELECT id_post FROM posts WHERE id_juego=" + idJuego + " and id_user=" + idUser, null);
            int idPost = -1;
            if (fila.moveToFirst()) {
                idPost = fila.getInt(0);
            }
            fila.close();
            if (tag == 3) {
                bundle.putInt("id-post", idPost);
                Navigation.findNavController(getActivity(), R.id.nav_host_videogame).navigate(R.id.reviewVideogameFragment, bundle);
            }
            Toast.makeText(getContext(), "You have saved the game like " + tagText, Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    /*
    true si hay
    false si no hay
     */
    private void updatePostTag(int tag) {
        int idUser = getIdUser();
        int idPost = -1;
        int prevTag = 0;
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_post, tag FROM posts WHERE id_juego=" + idJuego + " and id_user=" + idUser, null);
        if (fila.moveToFirst()) {
            idPost = fila.getInt(0);
            prevTag = fila.getInt(1);
        }
        ContentValues registro = new ContentValues();
        registro.put("fecha", new Date().getTime());
        registro.put("tag", tag);
        int cant = db.update("posts", registro, "id_post=" + idPost, null);
        if (cant > 0) {
            Toast.makeText(getContext(), "Post updated to " + getPostTag(tag), Toast.LENGTH_SHORT).show();
            if (tag == 3) {
                bundle.putInt("id-post", idPost);
                Navigation.findNavController(getActivity(), R.id.nav_host_videogame).navigate(R.id.reviewVideogameFragment, bundle);
            }
        } else {
            Toast.makeText(getContext(), "Post couldn't be updated", Toast.LENGTH_SHORT).show();
            spinnerSave.setSelection(prevTag);
        }
        fila.close();
        db.close();
    }

    private int getCurrentTag() {
        int idPost = getIdPost();
        int tag = 0;
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT tag FROM posts WHERE id_post=" + idPost, null);
        if (fila.moveToFirst()) {
            tag = fila.getInt(0);
        }
        fila.close();
        db.close();
        return tag;
    }

    private void deleteReview() {
        int idPost = getIdPost();
        SQLiteDatabase db = openDB();
        db.delete("posts", "id_post=" + idPost, null);
        db.close();
    }

    private Post.Tag getPostTag(int tagNum) {
        switch (tagNum) {
            case 1:
                return Post.Tag.TO_PLAY;
            case 2:
                return Post.Tag.PLAYING;
            case 3:
                return Post.Tag.PLAYED;
            default:
                return Post.Tag.UNDEFINED;
        }
    }

    public void hidePosts() {
        noPosts.setVisibility(TextView.VISIBLE);
        gamePosts.setVisibility(View.GONE);
    }

    private void updateGame() {
        if (noPosts.getVisibility() == TextView.VISIBLE) {
            noPosts.setVisibility(TextView.GONE);
            gamePosts.setVisibility(View.VISIBLE);
        }
        Fragment reviewFragment = new PostFragment();
        reviewFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction().replace(R.id.list_reviews, reviewFragment).commit();
    }

    private boolean checkReview() {
        boolean hayPost;
        int idUser = getIdUser();
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_user, id_juego FROM posts WHERE id_user=" + idUser + " and id_juego=" + idJuego, null);
        hayPost = fila.moveToFirst();
        fila.close();
        db.close();
        return hayPost;
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

    private int getIdPost() {
        int idUser = getIdUser();
        int idPost = -1;
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_post FROM posts WHERE id_juego=" + idJuego + " and id_user=" + idUser, null);
        if (fila.moveToFirst()) {
            idPost = fila.getInt(0);
        }
        fila.close();
        db.close();
        return idPost;
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(getContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadGame(getView());
    }
}