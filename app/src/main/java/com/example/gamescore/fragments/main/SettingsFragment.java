package com.example.gamescore.fragments.main;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.gamescore.R;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;
import com.example.gamescore.dialogs.MiDialogDeleteAccount;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preference logout = findPreference(getString(R.string.key_logout));
        Preference delete = findPreference(getString(R.string.key_delete));
        EditTextPreference changeEmail = findPreference(getString(R.string.key_change_email));
        getCurrentEmail();
        if (Constantes.login) {
            changeEmail.setEnabled(true);
            logout.setEnabled(true);
            delete.setEnabled(true);
        } else {
            changeEmail.setEnabled(false);
            logout.setEnabled(false);
            delete.setEnabled(false);
        }
        logout.setOnPreferenceClickListener(v -> {
            if (Constantes.login) {
                SharedPreferences preferences = getActivity().getSharedPreferences(Constantes.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
                Constantes.login = false;
                Toast.makeText(getContext(), "Has cerrado sesión " + Constantes.loggedUser, Toast.LENGTH_SHORT).show();
                Constantes.loggedUser = "Not logged";
                Constantes.login = false;
                preferences.edit().putString("username", Constantes.loggedUser).apply();
                preferences.edit().putBoolean("login", false).apply();
                getCurrentEmail();
                changeEmail.setEnabled(false);
                delete.setEnabled(false);
                logout.setEnabled(false);
                return true;
            }
            Toast.makeText(getContext(), "No has iniciado sesión", Toast.LENGTH_SHORT).show();
            return false;
        });
        delete.setOnPreferenceClickListener(v -> {
            if (Constantes.login) {
                DialogFragment dialog = new MiDialogDeleteAccount();
                dialog.show(getActivity().getSupportFragmentManager(), "dialog-delete");
                return true;
            }
            Toast.makeText(getContext(), "No has iniciado sesión", Toast.LENGTH_SHORT).show();
            return false;
        });
        changeEmail.setOnPreferenceClickListener(v -> {
            if (!Constantes.login) {
                Toast.makeText(getContext(), "No has iniciado sesión", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        });
        changeEmail.setOnPreferenceChangeListener((preference, newValue) -> {
            editEmail((String) newValue);
            getCurrentEmail();
            return true;
        });
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    private void deleteAccount() {
        SQLiteDatabase db = openDB();
        int cant = db.delete("usuarios", "username='" + Constantes.loggedUser + "'", null);
        if (cant > 0) {
            Toast.makeText(getContext(), "Se ha borrado correctamente el usuario " + Constantes.loggedUser, Toast.LENGTH_SHORT).show();
            SharedPreferences preferences = getActivity().getSharedPreferences(Constantes.NOMBRE_PREFERENCIAS, Context.MODE_PRIVATE);
            Constantes.loggedUser = "Not logged";
            Constantes.login = false;
            preferences.edit().putString("username", Constantes.loggedUser).apply();
            preferences.edit().putBoolean("login", Constantes.login).apply();
            getCurrentEmail();
            findPreference(getString(R.string.key_change_email)).setEnabled(false);
            findPreference(getString(R.string.key_delete)).setEnabled(false);
            findPreference(getString(R.string.key_logout)).setEnabled(false);
        } else {
            Toast.makeText(getContext(), "No se ha borrado el usuario", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private void editEmail(String newEmail) {
        SQLiteDatabase db = openDB();
        ContentValues registro = new ContentValues();
        registro.put("email", newEmail);
        int cant = db.update("usuarios", registro, "username='" + Constantes.loggedUser + "'", null);
        if (cant > 0) {
            Toast.makeText(getContext(), "Se ha actualizado el email a " + newEmail, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No se ha podido actualizar el email", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private void getCurrentEmail() {
        SQLiteDatabase db = openDB();
        Cursor fila = db.rawQuery("SELECT email FROM usuarios WHERE username='" + Constantes.loggedUser + "'", null);
        String email = "";
        if (fila.moveToFirst()) {
            email = fila.getString(0);
        } else {
            email = "Not logged in";
        }
        getPreferenceManager().getSharedPreferences().edit().putString(getString(R.string.key_change_email), email).apply();
        db.close();
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(getContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }

    public void preferencesClick() {
        deleteAccount();
    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentEmail();
    }
}