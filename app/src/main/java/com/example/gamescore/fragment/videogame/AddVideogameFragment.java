package com.example.gamescore.fragment.videogame;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.gamescore.R;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;

import java.io.ByteArrayOutputStream;

public class AddVideogameFragment extends Fragment {

    ImageView gameImg;
    Drawable gameDefault;

    public AddVideogameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        gameDefault = getDrawable(getContext(), R.drawable.videogame_default);
        View view = inflater.inflate(R.layout.fragment_add_videogame, container, false);
        gameImg = view.findViewById(R.id.game_img);
        gameImg.setImageDrawable(gameDefault);
        Button selectImg = view.findViewById(R.id.select_game_img);
        TextView resetImg = view.findViewById(R.id.reset_game_img);
        EditText gameName = view.findViewById(R.id.game_name);
        EditText gameSinopsis = view.findViewById(R.id.game_sinopsis);
        Button saveButton = view.findViewById(R.id.save_game);
        EditText gameRating = view.findViewById(R.id.game_rating);
        selectImg.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            startActivityForResult(intent, 1);
        });
        resetImg.setFocusable(false);
        resetImg.setOnClickListener(v -> {
            gameImg.setImageDrawable(gameDefault);
        });
        saveButton.setOnClickListener(v -> {
            String name = gameName.getText().toString();
            String sinopsis = gameSinopsis.getText().toString();
            String ratingStr = gameRating.getText().toString();
            Drawable img = gameImg.getDrawable();

            if (!name.isEmpty() || !sinopsis.isEmpty()) {
                if (ratingStr.isEmpty()) {
                    ratingStr = "0.0";
                }
                double rating = Double.parseDouble(ratingStr);
                if (rating > 5.0) {
                    gameRating.requestFocus();
                    Toast.makeText(getContext(), "Rating cannot be more than 5.0", Toast.LENGTH_SHORT).show();
                } else if (rating < 0.0) {
                    gameRating.requestFocus();
                    Toast.makeText(getContext(), "Rating cannot be less than 0.0", Toast.LENGTH_SHORT).show();
                } else {
                    saveGame(name, sinopsis, rating, img);
                    Navigation.findNavController(getActivity(), R.id.nav_host_videogame).navigate(R.id.showVideogameFragment);
                }
            } else {
                if (name.isEmpty()) {
                    gameName.requestFocus();
                    Toast.makeText(getContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    gameSinopsis.requestFocus();
                    Toast.makeText(getContext(), "Sinopsis can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Uri uri = data.getData();
            if (uri != null) {
                gameImg.setImageURI(uri);
            }
        }
    }

    private void saveGame(String gameName, String sinopsis, double rating, Drawable gameImg) {
        SQLiteDatabase db = openDB();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) gameImg).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] img = baos.toByteArray();
        ContentValues registro = new ContentValues();
        registro.put("nombre", gameName);
        registro.put("sinopsis", sinopsis);
        registro.put("nota_media", rating);
        registro.put("imagen", img);
        long result = db.insert("juegos", null, registro);
        if (result == -1) {
            Toast.makeText(getContext(), "No se ha podido guardar el juego", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Se ha guardado correctament el juego " + gameName, Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(getContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }
}