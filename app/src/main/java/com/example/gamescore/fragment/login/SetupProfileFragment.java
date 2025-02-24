package com.example.gamescore.fragment.login;

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

import com.example.gamescore.R;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;

import java.io.ByteArrayOutputStream;

public class SetupProfileFragment extends Fragment {

    private ImageView profilePic;
    private Drawable defaultProfilePic;

    public SetupProfileFragment() {
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
        defaultProfilePic = getDrawable(getContext(), R.drawable.user_account);
        View view = inflater.inflate(R.layout.fragment_setup_profile, container, false);
        profilePic = view.findViewById(R.id.profile_pic);
        profilePic.setImageDrawable(defaultProfilePic);
        if (getArguments() != null) {
            loadProfile(view);
        }
        Button changeProfilePic = view.findViewById(R.id.change_profile_pic);
        Button saveProfile = view.findViewById(R.id.save_profile);
        EditText changeDisplayName = view.findViewById(R.id.change_display_name);
        TextView resetProfilePic = view.findViewById(R.id.reset_profile_pic);
        changeProfilePic.setOnClickListener(v -> {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2) {
                intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
            } else {
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.setType("image/*");
            }
            startActivityForResult(intent, 1);
        });
        resetProfilePic.setFocusable(false);
        resetProfilePic.setOnClickListener(v -> {
            profilePic.setImageDrawable(defaultProfilePic);
        });
        changeDisplayName.setHint(getDisplayName());
        saveProfile.setOnClickListener(v -> {
            Drawable profilePicImg = profilePic.getDrawable();
            String displayName = changeDisplayName.getText().toString();
            if (displayName.isEmpty()) {
                displayName = getDisplayName();
            }
            updateProfile(displayName, profilePicImg);
            getActivity().finish();
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Uri uri = data.getData();
            if (uri != null) {
                profilePic.setImageURI(uri);
            }
        }
    }

    private String getDisplayName() {
        String displayName = "";
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT display_name FROM usuarios WHERE username='" + Constantes.loggedUser + "'", null);
        if (fila.moveToFirst()) {
            displayName = fila.getString(0);
        } else {
            displayName = Constantes.loggedUser;
        }
        db.close();
        return displayName;
    }

    private void updateProfile(String display_name, Drawable profilePic) {
        SQLiteDatabase db = openDB();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) profilePic).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] img = comprimirImagen(baos.toByteArray());
        ContentValues registro = new ContentValues();
        registro.put("display_name", display_name);
        registro.put("profile_pic", img);
        int cant = db.update("usuarios", registro, "username='" + Constantes.loggedUser + "'", null);
        if (cant > 0) {
            Toast.makeText(getContext(), "Se ha guardado el perfil de " + Constantes.loggedUser, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No se han guardado los cambios", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private void loadProfile(View view) {
        Drawable profilePic = null;
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT profile_pic, display_name, username FROM usuarios WHERE username='" + Constantes.loggedUser + "'", null);
        if (fila.moveToFirst()) {
            byte[] img = fila.getBlob(0);
            Bitmap imagen = BitmapFactory.decodeByteArray(img, 0, img.length);
            profilePic = new BitmapDrawable(getResources(), imagen);
        } else {
            profilePic = defaultProfilePic;
        }
        ImageView showProfilePic = view.findViewById(R.id.profile_pic);
        showProfilePic.setImageDrawable(profilePic);
        EditText displayName = view.findViewById(R.id.change_display_name);
        displayName.setText(fila.getString(1));
        displayName.setHint(fila.getString(2));
        fila.close();
        db.close();
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(getContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
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