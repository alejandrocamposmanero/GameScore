package com.example.gamescore.fragment.main.home;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamescore.R;
import com.example.gamescore.activity.MainActivity;
import com.example.gamescore.adapter.MiGameAdapter;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;
import com.example.gamescore.data.model.Game;

import java.util.ArrayList;

public class GameFragment extends Fragment {

    public interface MiOnFragmentClickListener {
        void onFragmentClick(Game game);
    }

    public interface MiResultSearchListener {
        void onResultSearch(int cant);
    }

    public interface MiTabDiscoverListener {
        void onTabDiscoverSelected(int cant);
    }

    private MiTabDiscoverListener miListenerTab;
    private MiOnFragmentClickListener miListenerClick;
    private MiResultSearchListener miListenerSearch;
    private RecyclerView recyclerView;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private Bundle bundle;

    public GameFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        bundle = getArguments();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            ArrayList<Game> dataGame = leerDatos();
            recyclerView.setAdapter(new MiGameAdapter(dataGame, videogame -> miListenerClick.onFragmentClick(videogame)));
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);
            if (miListenerTab != null) {
                miListenerTab.onTabDiscoverSelected(dataGame.size());
            }
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            miListenerClick = (MiOnFragmentClickListener) getActivity();
            if (getArguments() != null) {
                miListenerSearch = (MiResultSearchListener) getActivity();
            }
            if (getActivity() instanceof MainActivity) {
                miListenerTab = (MiTabDiscoverListener) getActivity();
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException(getActivity().toString() + " falta implementar listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        miListenerClick = null;
        miListenerSearch = null;
        miListenerTab = null;
    }

    private ArrayList<Game> leerDatos() {
        ArrayList<Game> datos = new ArrayList<>();
        if (bundle != null) {
            return leerBusqueda();
        }
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_juego, nombre, sinopsis, imagen, nota_media FROM juegos", null);
        while (fila.moveToNext()) {
            try {
                byte[] img = fila.getBlob(3);
                Bitmap imagen = BitmapFactory.decodeByteArray(img, 0, img.length);
                Drawable imagenDr = new BitmapDrawable(getResources(), imagen);
                datos.add(new Game(fila.getInt(0), fila.getString(1), fila.getString(2), imagenDr, fila.getDouble(4)));
            } catch (SQLiteBlobTooBigException sqlbtbe) {
                db.delete("juegos", "id_juego=" + fila.getInt(0), null);
                break;
            }
        }
        fila.close();
        db.close();
        return datos;
    }

    private ArrayList<Game> leerBusqueda() {
        ArrayList<Game> datos = new ArrayList<>();
        String game = bundle.getString("query");
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_juego, nombre, sinopsis, imagen, nota_media FROM juegos WHERE nombre LIKE '%" + game + "%' ", null);
        while (fila.moveToNext()) {
            byte[] img;
            try {
                img = fila.getBlob(3);
            } catch (SQLiteBlobTooBigException sqlbtbe) {
                db.delete("juegos", "id_juego=" + fila.getInt(0), null);
                break;
            }
            Bitmap imagen = BitmapFactory.decodeByteArray(img, 0, img.length);
            Drawable imagenDr = new BitmapDrawable(getResources(), imagen);
            datos.add(new Game(fila.getInt(0), fila.getString(1), fila.getString(2), imagenDr, fila.getDouble(4)));
        }
        miListenerSearch.onResultSearch(datos.size());
        fila.close();
        db.close();
        return datos;
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(getContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<Game> datos = leerDatos();
        recyclerView.setAdapter(new MiGameAdapter(datos, videogame -> miListenerClick.onFragmentClick(videogame)));
        if (miListenerTab != null)
            miListenerTab.onTabDiscoverSelected(datos.size());
    }
}