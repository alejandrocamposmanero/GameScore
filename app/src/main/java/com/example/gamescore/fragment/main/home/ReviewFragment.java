package com.example.gamescore.fragment.main.home;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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
import com.example.gamescore.adapter.MyReviewAdapter;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;
import com.example.gamescore.data.model.Post;

import java.util.ArrayList;


public class ReviewFragment extends Fragment {

    public interface MiFragmentClickListener {
        void onFragmentClick(Post post);
    }

    public interface MiPostTabListener {
        void onTabPostSelected(int cant);
    }

    private MiFragmentClickListener miListenerClick;
    private MiPostTabListener miListenerTab;
    private RecyclerView recyclerView;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    public ReviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            ArrayList<Post> dataReview = leerDatos();
            recyclerView.setAdapter(new MyReviewAdapter(dataReview, post -> miListenerClick.onFragmentClick(post)));
            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(itemDecoration);
            if (miListenerTab != null) {
                miListenerTab.onTabPostSelected(dataReview.size());
            }
        }

        return view;
    }

    private ArrayList<Post> leerDatos() {
        ArrayList<Post> datos = new ArrayList<>();
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT id_post, resena, rating, tag, id_user, id_juego FROM posts", null);
        while (fila.moveToNext()) {
            Post.Tag tag;
            switch (fila.getInt(3)) {
                case 0:
                    tag = Post.Tag.TO_PLAY;
                    break;
                case 1:
                    tag = Post.Tag.PLAYING;
                    break;
                case 2:
                    tag = Post.Tag.PLAYED;
                    break;
                default:
                    tag = Post.Tag.UNDEFINED;
            }
            datos.add(new Post(fila.getInt(0), fila.getString(1), fila.getDouble(2), tag, fila.getInt(4), fila.getInt(5)));
        }
        fila.close();
        db.close();
        return datos;
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
            if (getActivity() instanceof MainActivity) {
                miListenerTab = (MiPostTabListener) getActivity();
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException(getActivity().toString() + " falta implementar listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        miListenerClick = null;
        miListenerTab = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onresume", "hola");
        ArrayList<Post> datos = leerDatos();
        recyclerView.setAdapter(new MyReviewAdapter(datos, post -> miListenerClick.onFragmentClick(post)));
        if (miListenerTab != null)
            miListenerTab.onTabPostSelected(datos.size());
    }
}