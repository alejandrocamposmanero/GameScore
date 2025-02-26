package com.example.gamescore.fragment.main;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.gamescore.R;
import com.example.gamescore.activity.MainActivity;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;

public class MyGamesFragment extends Fragment {

    public MyGamesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.isHomeFragment = false;
        MainActivity.isProfileFragment = false;
        MainActivity.isMyGamesFragment = true;
        View view = inflater.inflate(R.layout.fragment_my_games, container, false);
        Button toPlay = view.findViewById(R.id.to_play_games);
        Button playing = view.findViewById(R.id.playing_games);
        Button played = view.findViewById(R.id.played_games);
        toPlay.setOnClickListener(v -> {
            if (Constantes.login) {
                showGames(1);
            } else {
                Toast.makeText(requireContext(), "You must be logged in to perform this action", Toast.LENGTH_SHORT).show();
            }
        });
        playing.setOnClickListener(v -> {
            if (Constantes.login) {
                showGames(2);
            } else {
                Toast.makeText(requireContext(), "You must be logged in to perform this action", Toast.LENGTH_SHORT).show();
            }
        });
        played.setOnClickListener(v -> {
            if (Constantes.login) {
                showGames(3);
            } else {
                Toast.makeText(requireContext(), "You must be logged in to perform this action", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void showGames(int tag) {
        Bundle bundle = new Bundle();
        bundle.putInt("tag", tag);
        bundle.putInt("id-user", getUserId());
        bundle.putInt("id-juego", -1);
        Navigation.findNavController(requireActivity(), R.id.nav_host_main).navigate(R.id.postFragment, bundle);
    }

    private int getUserId() {
        SQLiteDatabase db = MiAdminSQLite.getInstance(requireContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB).getWritableDatabase();
        int idUser = -1;
        Cursor user = db.rawQuery("SELECT id_user FROM usuarios WHERE username='" + Constantes.loggedUser + "'", null);
        if (user.moveToFirst()) {
            idUser = user.getInt(0);
        }
        user.close();
        db.close();
        return idUser;
    }
}