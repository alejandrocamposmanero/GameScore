package com.example.gamescore.fragments.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.gamescore.R;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button button = view.findViewById(R.id.button_login);
        button.setOnClickListener(v -> {
            SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
            preferences.edit().putBoolean("login", true).apply();

        });
        return view;
    }
}