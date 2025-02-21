package com.example.gamescore.fragments.main.home;

import android.content.Context;
import android.database.Cursor;
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
import com.example.gamescore.adapters.MyVideogameAdapter;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;
import com.example.gamescore.model.Videogame;

import java.util.ArrayList;

public class DiscoverFragment extends Fragment {

    public interface MiOnFragmentClickListener {
        void onFragmentClick(Videogame videogame);
    }

    private MiOnFragmentClickListener miListener;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private Bundle bundle;

    public DiscoverFragment() {
    }

    public DiscoverFragment(Bundle bundle) {
        this.bundle = bundle;
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
        View view = inflater.inflate(R.layout.fragment_discover_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            ArrayList<Videogame> dataVideogame = leerDatos();
            recyclerView.setAdapter(new MyVideogameAdapter(dataVideogame, videogame -> miListener.onFragmentClick(videogame)));
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            miListener = (MiOnFragmentClickListener) getActivity();
        } catch (ClassCastException cce) {
            throw new ClassCastException(getActivity().toString() + " falta implementar listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        miListener = null;
    }

    private ArrayList<Videogame> leerDatos() {
        ArrayList<Videogame> datos = new ArrayList<>();
        if (bundle != null) {
            return leerBusqueda();
        }
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_juego, nombre, imagen, nota_media FROM juegos", null);
        while (fila.moveToNext()) {
            byte[] img = fila.getBlob(2);
            Bitmap imagen = BitmapFactory.decodeByteArray(img, 0, img.length);
            Drawable imagenDr = new BitmapDrawable(getResources(), imagen);
            datos.add(new Videogame(fila.getInt(0), fila.getString(1), imagenDr, fila.getDouble(3)));
        }
        fila.close();
        db.close();
        return datos;
    }

    private ArrayList<Videogame> leerBusqueda() {
        ArrayList<Videogame> datos = new ArrayList<>();
        String game = bundle.getString("query");
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_juego, nombre, imagen, nota_media FROM juegos WHERE nombre LIKE %" + game + "% ", null);
        while (fila.moveToNext()) {
            byte[] img = fila.getBlob(2);
            Bitmap imagen = BitmapFactory.decodeByteArray(img, 0, img.length);
            Drawable imagenDr = new BitmapDrawable(getResources(), imagen);
            datos.add(new Videogame(fila.getInt(0), fila.getString(1), imagenDr, fila.getDouble(3)));
        }
        fila.close();
        db.close();
        return datos;
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(getContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }
}