package com.example.gamescore.fragments.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.gamescore.R;
import com.example.gamescore.activitys.LoginActivity;
import com.example.gamescore.data.Constantes;


public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(Constantes.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
        Constantes.login = preferences.getBoolean("login", false);
        if (!Constantes.login) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        if (Constantes.login) {
            cargarPerfil(view);
        }
        return view;
    }

    private void cargarPerfil(View view) {
        TextView displayName = view.findViewById(R.id.display_name);
        displayName.setText(Constantes.loggedUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarPerfil(getView());
    }
}