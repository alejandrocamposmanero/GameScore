package com.example.gamescore.fragment.main.home;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamescore.R;
import com.example.gamescore.activity.GameActivity;
import com.example.gamescore.activity.MainActivity;
import com.example.gamescore.adapter.MiPostAdapter;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;
import com.example.gamescore.data.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class PostFragment extends Fragment {

    public interface MiFragmentClickListener {
        void onFragmentClick(Post post);
    }

    public interface MiPostTabListener {
        void onTabPostSelected(int cant);
    }

    public interface MiPostsEmptyListener {
        void onPostsProfileEmpty();

        void onPostsMyGamesEmpty();
    }

    private MiPostsEmptyListener miListenerEmpty;
    private MiFragmentClickListener miListenerClick;
    private MiPostTabListener miListenerTab;
    private RecyclerView recyclerView;
    private Bundle bundle;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    public PostFragment() {
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
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        // Set the adapter
        bundle = getArguments();
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            List<Post> dataReview = leerDatos();
            recyclerView.setAdapter(new MiPostAdapter(dataReview, post -> miListenerClick.onFragmentClick(post)));
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);
            if (miListenerTab != null)
                miListenerTab.onTabPostSelected(dataReview.size());
            if (miListenerEmpty != null && dataReview.isEmpty() && MainActivity.isProfileFragment)
                miListenerEmpty.onPostsProfileEmpty();
            else if (miListenerEmpty != null && dataReview.isEmpty() && MainActivity.isMyGamesFragment)
                miListenerEmpty.onPostsMyGamesEmpty();
        }

        return view;
    }

    private List<Post> leerDatos() {
        List<Post> datos = new ArrayList<>();
        if (getArguments() != null) {
            if (getArguments().getInt("id-juego") != -1) {
                return leerDatosJuego();
            } else if (getArguments().getInt("tag") != -1) {
                return leerDatosTag();
            } else if (getArguments().getInt("id-user") != -1)
                return leerDatosUser();
        }
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_post, resena, rating, tag, id_user, id_juego, fecha FROM posts ORDER BY fecha DESC", null);
        while (fila.moveToNext()) {
            Post.Tag tag = getPostTag(fila.getInt(3));
            datos.add(new Post(fila.getInt(0), fila.getString(1), fila.getDouble(2), tag, fila.getInt(4), fila.getInt(5)));
        }
        fila.close();
        db.close();
        return datos;
    }

    private List<Post> leerDatosJuego() {
        List<Post> datos = new ArrayList<>();
        int idJuego = -1;
        if (bundle != null) {
            idJuego = bundle.getInt("id-juego");
        }
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_post, resena, rating, tag, id_user, id_juego, fecha FROM posts WHERE id_juego=" + idJuego + " ORDER BY fecha DESC", null);
        while (fila.moveToNext()) {
            Post.Tag tag = getPostTag(fila.getInt(3));
            datos.add(new Post(fila.getInt(0), fila.getString(1), fila.getDouble(2), tag, fila.getInt(4), fila.getInt(5)));
        }
        fila.close();
        db.close();
        return datos;
    }

    private List<Post> leerDatosUser() {
        ArrayList<Post> datos = new ArrayList<>();
        int idUser = -1;
        if (bundle != null)
            idUser = bundle.getInt("id-user");

        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_post, resena, rating, tag, id_user, id_juego, fecha FROM posts WHERE id_user=" + idUser + " ORDER BY fecha DESC", null);
        while (fila.moveToNext()) {
            Post.Tag tag = getPostTag(fila.getInt(3));
            datos.add(new Post(fila.getInt(0), fila.getString(1), fila.getDouble(2), tag, fila.getInt(4), fila.getInt(5)));
        }
        fila.close();
        db.close();
        return datos;
    }

    private List<Post> leerDatosTag() {
        List<Post> datos = leerDatosUser();
        return datos.stream().filter(p -> p.getTag() == getPostTag(bundle.getInt("tag"))).collect(Collectors.toList());
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(getContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            miListenerClick = (MiFragmentClickListener) getActivity();
            if (MainActivity.isHomeFragment) {
                miListenerTab = (MiPostTabListener) getActivity();
            }
            if (getArguments() != null || GameActivity.isShowGameFragment)
                miListenerEmpty = (MiPostsEmptyListener) getActivity();
        } catch (ClassCastException cce) {
            throw new ClassCastException(getActivity().toString() + " falta implementar listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        miListenerClick = null;
        miListenerTab = null;
        miListenerEmpty = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        List<Post> datos = leerDatos();
        recyclerView.setAdapter(new MiPostAdapter(datos, post -> miListenerClick.onFragmentClick(post)));
        if (miListenerTab != null)
            miListenerTab.onTabPostSelected(datos.size());
    }

    private Post.Tag getPostTag(int numTag) {
        switch (numTag) {
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

    public void noGames() {
        Toast.makeText(getContext(), "There are no games for the tag you selected", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(getActivity(), R.id.nav_host_main).navigate(R.id.myGamesFragment);
    }

}