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
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.gamescore.R;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;

public class ForgotPasswordFragment extends Fragment {

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        TextView usernameChange = view.findViewById(R.id.user_change_password_text);
        EditText changePassword = view.findViewById(R.id.change_password);
        EditText changePasswordConfirm = view.findViewById(R.id.change_password_confirm);
        String text = getDisplayName() + " " + getString(R.string.new_password);
        usernameChange.setText(text);
        Button changePasswordButton = view.findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(v -> {
            String newPassword = changePassword.getText().toString();
            String newPasswordConfirm = changePasswordConfirm.getText().toString();
            if (!newPassword.isEmpty() && !newPasswordConfirm.isEmpty()) {
                if (newPassword.equals(newPasswordConfirm)) {
                    updatePassword(newPassword);
                    SharedPreferences preferences = getActivity().getSharedPreferences(Constantes.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
                    Constantes.loggedUser = getUsername();
                    Constantes.login = true;
                    preferences.edit().putString("username", Constantes.loggedUser).apply();
                    preferences.edit().putBoolean("login", Constantes.login).apply();
                    getActivity().finish();
                }
            } else {
                if (newPassword.isEmpty()) {
                    changePassword.requestFocus();
                } else {
                    changePasswordConfirm.requestFocus();
                }
                Toast.makeText(getContext(), "No puede dejar ningún campo vacío", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private String getDisplayName() {
        String displayName = "";
        SQLiteDatabase db = openDB();
        Bundle bundle = getArguments();
        boolean useEmail = bundle.getBoolean("use-email", false);
        Cursor fila;
        if (useEmail) {
            fila = db.rawQuery("SELECT display_name FROM usuarios WHERE email='" + bundle.getString("email") + "'", null);
        } else {
            fila = db.rawQuery("SELECT display_name FROM usuarios WHERE username='" + bundle.getString("username") + "'", null);
        }
        if (fila.moveToFirst())
            displayName = fila.getString(0);
        fila.close();
        db.close();
        return displayName;
    }

    private String getUsername() {
        String username = "";
        Bundle bundle = getArguments();
        boolean useEmail = bundle.getBoolean("use-email", false);
        if (useEmail) {
            SQLiteDatabase db = openDB();
            Cursor fila = db.rawQuery("SELECT username FROM usuarios WHERE email='" + bundle.getString("email") + "'", null);
            if (fila.moveToFirst())
                username = fila.getString(0);
            db.close();
        } else {
            username = bundle.getString("username");
        }
        return username;
    }

    private void updatePassword(String newPassword) {
        SQLiteDatabase db = openDB();
        ContentValues password = new ContentValues();
        password.put("password", newPassword);
        Bundle bundle = getArguments();
        boolean useEmail = bundle.getBoolean("use-email", false);
        String username;
        int cant;
        if (useEmail) {
            username = bundle.getString("email");
            cant = db.update("usuarios", password, "email='" + username + "'", null);
        } else {
            username = bundle.getString("username");
            cant = db.update("usuarios", password, "username='" + username + "'", null);
        }
        db.close();
        if (cant > 0) {
            Toast.makeText(getContext(), "Se ha actualizado correctamente la contraseña", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No se ha actualizado la contraseña", Toast.LENGTH_SHORT).show();
        }
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(getContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }
}