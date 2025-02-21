package com.example.gamescore.fragments.login;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.gamescore.R;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;

public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        EditText registerUsername = view.findViewById(R.id.register_username);
        EditText registerEmail = view.findViewById(R.id.register_email);
        EditText registerPassword = view.findViewById(R.id.register_password);
        Button registerButton = view.findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> {
            String username = registerUsername.getText().toString();
            String email = registerEmail.getText().toString();
            String password = registerPassword.getText().toString();
            if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                if (checkUsername(username)) {
                    registerUsername.requestFocus();
                    Toast.makeText(getContext(), "Ese nombre de usuario está cogido, elige otro", Toast.LENGTH_SHORT).show();
                } else if (checkEmail(email)) {
                    registerEmail.requestFocus();
                    Toast.makeText(getContext(), "Ese email ya está registrado, utilice otro o inicie sesión", Toast.LENGTH_SHORT).show();
                } else {
                    saveUser(username, email, password);
                    SharedPreferences preferencias = getActivity().getSharedPreferences(Constantes.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
                    preferencias.edit().putBoolean("login", true).apply();
                    Constantes.login = preferencias.getBoolean("login", true);
                    Constantes.loggedUser = username;
                    getActivity().finish();
                }
            } else {
                if (username.isEmpty())
                    registerUsername.requestFocus();
                else if (email.isEmpty())
                    registerEmail.requestFocus();
                else
                    registerPassword.requestFocus();
                Toast.makeText(getContext(), "No puede estar vacío ningún campo", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }


    private void saveUser(String username, String email, String password) {
        SQLiteDatabase db = openDB();
        ContentValues datos = new ContentValues();
        datos.put("username", username);
        datos.put("display_name", username);
        datos.put("email", email);
        datos.put("password", password);
        long result = db.insert("usuarios", null, datos);
        if (result == -1) {
            Toast.makeText(getContext(), "No se ha podido registrar el usuario", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Se registró exitosamente " + username, Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    /*
    true means it is in the table
    false means it is not in the table
     */
    private boolean checkUsername(String username) {
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT username FROM usuarios WHERE username='" + username + "'", null);
        boolean valido = fila.moveToFirst();
        fila.close();
        db.close();
        return valido;
    }

    /*
    true means it is in the table
    false means it is not in the table
     */
    private boolean checkEmail(String email) {
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT email FROM usuarios WHERE email='" + email + "'", null);
        boolean valido = fila.moveToFirst();
        fila.close();
        db.close();
        return valido;
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(getContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }
}