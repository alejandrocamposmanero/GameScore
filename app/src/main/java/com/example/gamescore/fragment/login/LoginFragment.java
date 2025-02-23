package com.example.gamescore.fragment.login;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.example.gamescore.R;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;

public class LoginFragment extends Fragment {

    private boolean useEmail = false;

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
        EditText loginUsername = view.findViewById(R.id.login_username_mail);
        EditText loginPassword = view.findViewById(R.id.login_password);
        TextView changeUsername = view.findViewById(R.id.change_email_username);
        TextView register = view.findViewById(R.id.register);
        TextView forgotPassword = view.findViewById(R.id.forgot_password);

        changeUsername.setFocusable(false);
        register.setFocusable(false);
        forgotPassword.setFocusable(false);

        changeUsername.setOnClickListener(v -> {
            if (!useEmail) {
                useEmail = true;
                loginUsername.setText("");
                loginUsername.setHint(getString(R.string.hint_email));
                changeUsername.setText(getString(R.string.change_to_username));
            } else {
                useEmail = false;
                loginUsername.setText("");
                loginUsername.setHint(getString(R.string.hint_username));
                changeUsername.setText(getString(R.string.change_to_email));
            }
        });
        forgotPassword.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            String username = loginUsername.getText().toString();
            if (validUsername(username)) {
                bundle.putBoolean("use-email", useEmail);
                if (!useEmail)
                    bundle.putString("username", username);
                else
                    bundle.putString("email", username);
                Navigation.findNavController(getActivity(), R.id.nav_host_login).navigate(R.id.forgotPasswordFragment, bundle);
            } else {
                loginUsername.requestFocus();
                Toast.makeText(getActivity(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            }
        });
        register.setOnClickListener(v -> {
            Navigation.findNavController(getActivity(), R.id.nav_host_login).navigate(R.id.registerFragment);
        });
        Button button = view.findViewById(R.id.boton_login);
        button.setOnClickListener(v -> {
            String username = loginUsername.getText().toString();
            String password = loginPassword.getText().toString();
            if (!password.isEmpty() && !username.isEmpty()) {
                if (checkUser(username, password)) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("login", true);
                    if (!useEmail) {
                        Constantes.loggedUser = username;
                        editor.putString(getString(R.string.key_username), username);
                        editor.putString(getString(R.string.key_email), getEmail(username));
                    } else {
                        Constantes.loggedUser = getUsername(username);
                        editor.putString(getString(R.string.key_username), Constantes.loggedUser);
                        editor.putString(getString(R.string.key_email), username);
                    }
                    Constantes.login = true;
                    editor.apply();
                    Toast.makeText(getActivity(), "Login correcto " + Constantes.loggedUser, Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                } else if (!validUsername(username)) {
                    Toast.makeText(getActivity(), "No se ha encontrado el usuario", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "El usuario y la contraseña no coinciden", Toast.LENGTH_SHORT).show();
                }

            } else {
                if (username.isEmpty()) {
                    loginUsername.requestFocus();
                    Toast.makeText(getActivity(), "No ha introducido nombre de usuario", Toast.LENGTH_SHORT).show();
                } else {
                    loginUsername.requestFocus();
                    Toast.makeText(getActivity(), "No ha introducido contraseña", Toast.LENGTH_SHORT).show();
                }
            }

        });
        return view;
    }

    private boolean validUsername(String username) {
        SQLiteDatabase db = openDB();
        Cursor fila;
        if (!useEmail)
            fila = db.rawQuery("SELECT username FROM usuarios WHERE username='" + username + "'", null);
        else
            fila = db.rawQuery("SELECT email FROM usuarios WHERE email='" + username + "'", null);
        boolean valido = fila.moveToFirst();
        fila.close();
        db.close();
        return valido;
    }

    private boolean checkUser(String username, String password) {
        boolean valido = validUsername(username);
        SQLiteDatabase db = openDB();
        Cursor fila;
        if (!useEmail) {
            fila = db.rawQuery("SELECT username, password FROM usuarios WHERE username='" + username + "'", null);
        } else {
            fila = db.rawQuery("SELECT email, password FROM usuarios WHERE email='" + username + "'", null);
        }
        if (fila.moveToFirst())
            valido = valido && password.equals(fila.getString(1));
        fila.close();
        db.close();
        return valido;
    }

    private String getUsername(String email) {
        String username = "";
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT username FROM usuarios WHERE email='" + email + "'", null);
        if (fila.moveToFirst())
            username = fila.getString(0);
        db.close();
        return username;
    }

    private String getEmail(String username) {
        String email = "";
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT email FROM usuarios WHERE username='" + username + "'", null);
        if (fila.moveToFirst())
            email = fila.getString(0);
        db.close();
        return email;
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(getContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }
}