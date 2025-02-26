package com.example.gamescore.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.gamescore.R;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;
import com.example.gamescore.data.model.Post;
import com.example.gamescore.dialog.MiDialogDeleteReview;
import com.example.gamescore.dialog.MiDialogNoReview;
import com.example.gamescore.fragment.main.home.PostFragment;
import com.example.gamescore.fragment.videogame.ShowGameFragment;

import java.util.Objects;

public class GameActivity extends AppCompatActivity implements
        PostFragment.MiFragmentClickListener, MiDialogDeleteReview.MiDialogDeleteListener,
        MiDialogNoReview.MiDialogNoReviewListener, PostFragment.MiPostsEmptyListener {

    public static boolean isShowGameFragment;
    private Bundle bundle;
    private int idJuego;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_videogame);

        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        isShowGameFragment = true;

        bundle = getIntent().getExtras();
        boolean addGame = false;

        idJuego = -1;
        if (bundle != null) {
            addGame = bundle.getBoolean("add", false);
            idJuego = bundle.getInt("id-juego", -1);
        }
        if (addGame) {
            Navigation.findNavController(this, R.id.nav_host_videogame).navigate(R.id.addVideogameFragment);
        }
        if (idJuego != -1) {
            getSupportActionBar().setTitle(getVideogameName());
            Navigation.findNavController(this, R.id.nav_host_videogame).navigate(R.id.showGameFragment, bundle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_videogame, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (Constantes.login) {
            if (id == R.id.appbar_edit) {
                if (!getVideogameReview() && isShowGameFragment) {
                    DialogFragment dialog = new MiDialogNoReview();
                    dialog.show(getSupportFragmentManager(), "dialog-no-review");
                } else if (isShowGameFragment) {
                    Navigation.findNavController(this, R.id.nav_host_videogame).navigate(R.id.reviewVideogameFragment, bundle);
                }
            }
            if (id == R.id.appbar_delete) {
                if (getVideogameReview() && isShowGameFragment) {
                    DialogFragment dialog = new MiDialogDeleteReview();
                    dialog.show(getSupportFragmentManager(), "dialog-delete");
                } else if (isShowGameFragment) {
                    Toast.makeText(getApplicationContext(), "You don't have any review to delete", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "You must be logged in to perform this action", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onDeleteOk() {
        int idUser = getUserId();
        int idPost = -1;
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_post FROM posts WHERE id_juego=" + idJuego + " AND id_user=" + idUser, null);
        if (fila.moveToFirst()) {
            idPost = fila.getInt(0);
        }
        int cant = db.delete("posts", "id_post=" + idPost, null);
        if (cant <= 0) {
            Toast.makeText(getApplicationContext(), "The post hasn't been deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "The post has been deleted", Toast.LENGTH_SHORT).show();
        }
        if (getCurrentFragment() instanceof ShowGameFragment) {
            ((ShowGameFragment) getCurrentFragment()).updateGame();
        }
        fila.close();
        db.close();
    }

    @Override
    public void onDeleteCancel() {
        Toast.makeText(getApplicationContext(), "The action has been cancelled", Toast.LENGTH_SHORT).show();
    }

    private boolean getVideogameReview() {
        boolean hasReview;
        if (!Constantes.login) {
            Toast.makeText(getApplicationContext(), "You have to be logged in to perform this action", Toast.LENGTH_SHORT).show();
            return false;
        }
        int idUser = getUserId();
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_post, id_user, id_juego FROM posts WHERE id_user=" + idUser + " and id_juego=" + idJuego, null);
        hasReview = fila.moveToFirst();
        fila.close();
        db.close();
        return hasReview;
    }

    private String getVideogameName() {
        String nombre;
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT nombre FROM juegos WHERE id_juego=" + idJuego, null);
        if (fila.moveToFirst()) {
            nombre = fila.getString(0);
        } else {
            nombre = "Not found";
        }
        fila.close();
        db.close();
        return nombre;
    }

    @Override
    public void onCreateReview() {
        createReview();
        Navigation.findNavController(this, R.id.nav_host_videogame).navigate(R.id.reviewVideogameFragment, bundle);
    }

    @Override
    public void onCancelReview() {
        Toast.makeText(getApplicationContext(), "Action cancelled", Toast.LENGTH_SHORT).show();
    }

    private void createReview() {
        int idUser = getUserId();
        SQLiteDatabase db = openDB();
        ContentValues registro = new ContentValues();
        registro.put("id_juego", idJuego);
        registro.put("tag", 3);
        registro.put("id_user", idUser);
        db.insert("posts", null, registro);
        db.close();
    }

    private int getUserId() {
        SQLiteDatabase db = openDB();
        int idUser = -1;
        Cursor user = db.rawQuery("SELECT id_user FROM usuarios WHERE username='" + Constantes.loggedUser + "'", null);
        if (user.moveToFirst()) {
            idUser = user.getInt(0);
        }
        user.close();
        db.close();
        return idUser;
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(getApplicationContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }

    @Override
    public void onFragmentClick(Post post) {
    }

    private Fragment getCurrentFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_videogame);
        if (fragment instanceof NavHostFragment) {
            return fragment.getChildFragmentManager().getFragments().get(0);
        }
        return null;
    }

    @Override
    public void onPostsProfileEmpty() {
    }

    @Override
    public void onPostsMyGamesEmpty() {
    }
}