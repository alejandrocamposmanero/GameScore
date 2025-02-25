package com.example.gamescore.fragment.main;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.gamescore.R;
import com.example.gamescore.activity.LoginActivity;
import com.example.gamescore.activity.MainActivity;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;
import com.example.gamescore.fragment.main.home.PostFragment;


public class ProfileFragment extends Fragment {

    private TextView noResults;
    private FrameLayout myReviews;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Constantes.login = preferences.getBoolean("login", false);
        if (!Constantes.login) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            Toast.makeText(getContext(), "Please, log in or register", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.isHomeFragment = false;
        MainActivity.isProfileFragment = true;
        MainActivity.isMyGamesFragment = false;
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        noResults = view.findViewById(R.id.no_reviews);
        myReviews = view.findViewById(R.id.my_reviews);

        return view;
    }

    private void cargarPerfil(View view) {
        Button editProfile = view.findViewById(R.id.edit_profile);
        if (!Constantes.login) {
            editProfile.setText("Log in");
        } else {
            editProfile.setText(getString(R.string.edit_profile));
        }
        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            if (Constantes.login) {
                intent.putExtra("edit-profile", true);
            }
            startActivity(intent);
            MainActivity.isProfileFragment = false;
        });
        noResults.setVisibility(View.GONE);
        myReviews.setVisibility(View.VISIBLE);
        TextView displayName = view.findViewById(R.id.display_name);
        ImageView profilePic = view.findViewById(R.id.user_icon);
        profilePic.setImageDrawable(getProfilePic());
        displayName.setText(getDisplayName());

        Fragment fragment = new PostFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id-user", getUserId());
        bundle.putInt("tag", -1);
        bundle.putInt("id-juego", -1);
        fragment.setArguments(bundle);
        if (Constantes.login)
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.my_reviews, fragment).commit();
        else
            hidePosts();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarPerfil(getView());
    }

    private Drawable getProfilePic() {
        Drawable profilePic = null;
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT profile_pic FROM usuarios WHERE username='" + Constantes.loggedUser + "'", null);
        if (fila.moveToFirst()) {
            try {
                byte[] img = fila.getBlob(0);
                Bitmap imagen = BitmapFactory.decodeByteArray(img, 0, img.length);
                profilePic = new BitmapDrawable(getResources(), imagen);
            } catch (SQLiteBlobTooBigException sqlbtbe) {
                db.delete("juegos", "id_juego=" + fila.getInt(0), null);
            }
        } else {
            profilePic = getContext().getDrawable(R.drawable.user_account);
        }
        fila.close();
        db.close();
        return profilePic;
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
        fila.close();
        db.close();
        return displayName;
    }

    private int getUserId() {
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

    public void hidePosts() {
        noResults.setVisibility(TextView.VISIBLE);
        myReviews.setVisibility(View.GONE);
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(getContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }
}