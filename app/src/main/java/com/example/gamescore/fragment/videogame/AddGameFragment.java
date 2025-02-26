package com.example.gamescore.fragment.videogame;

import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ext.SdkExtensions;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.gamescore.R;
import com.example.gamescore.activity.GameActivity;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;

import java.io.ByteArrayOutputStream;

public class AddGameFragment extends Fragment {

    ImageView gameImg;
    Drawable gameDefault;

    public AddGameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedDispatcher onBack = requireActivity().getOnBackPressedDispatcher();
        onBack.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GameActivity.isShowGameFragment = false;
        gameDefault = getDrawable(requireContext(), R.drawable.videogame_default);
        View view = inflater.inflate(R.layout.fragment_add_game, container, false);
        gameImg = view.findViewById(R.id.game_img);
        gameImg.setImageDrawable(gameDefault);
        Button selectImg = view.findViewById(R.id.select_game_img);
        TextView resetImg = view.findViewById(R.id.reset_game_img);
        EditText gameName = view.findViewById(R.id.game_name);
        EditText gameSinopsis = view.findViewById(R.id.game_sinopsis);
        Button saveButton = view.findViewById(R.id.save_game);
        EditText gameRating = view.findViewById(R.id.game_rating);
        selectImg.setOnClickListener(v -> {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2) {
                intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            } else {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            }
            startActivityForResult(intent, 1);
        });
        resetImg.setFocusable(false);
        resetImg.setOnClickListener(v -> gameImg.setImageDrawable(gameDefault));
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
                    Toast.makeText(requireContext(), "Rating cannot be more than 5.0", Toast.LENGTH_SHORT).show();
                } else if (rating < 0.0) {
                    gameRating.requestFocus();
                    Toast.makeText(requireContext(), "Rating cannot be less than 0.0", Toast.LENGTH_SHORT).show();
                } else {
                    int idJuego = saveGame(name, sinopsis, rating, img);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id-juego", idJuego);
                    Navigation.findNavController(requireActivity(), R.id.nav_host_videogame).navigate(R.id.showGameFragment, bundle);
                }
            } else {
                if (name.isEmpty()) {
                    gameName.requestFocus();
                    Toast.makeText(requireContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
                } else {
                    gameSinopsis.requestFocus();
                    Toast.makeText(requireContext(), "Sinopsis can't be empty", Toast.LENGTH_SHORT).show();
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

    private int saveGame(String gameName, String sinopsis, double rating, Drawable gameImg) {
        SQLiteDatabase db = openDB();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) gameImg).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] img = comprimirImagen(baos.toByteArray());
        ContentValues registro = new ContentValues();
        registro.put("nombre", gameName);
        registro.put("sinopsis", sinopsis);
        registro.put("nota_media", rating);
        registro.put("imagen", img);
        long result = db.insert("juegos", null, registro);
        if (result == -1) {
            Toast.makeText(requireContext(), "Couldn't save the game", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Game saved correctly " + gameName, Toast.LENGTH_SHORT).show();
        }
        int idJuego = -1;
        Cursor fila = db.rawQuery("SELECT id_juego FROM juegos WHERE nombre ='" + gameName + "'", null);
        if (fila.moveToFirst()) {
            idJuego = fila.getInt(0);
        }
        fila.close();
        db.close();
        return idJuego;
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(requireContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }

    private byte[] comprimirImagen(byte[] img) {
        while (img.length > 500000) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.8), (int) (bitmap.getHeight() * 0.8), true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
            img = stream.toByteArray();
        }
        return img;
    }
}