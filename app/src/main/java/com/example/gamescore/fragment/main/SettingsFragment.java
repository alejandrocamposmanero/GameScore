package com.example.gamescore.fragment.main;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.gamescore.R;
import com.example.gamescore.activity.LoginActivity;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;
import com.example.gamescore.dialog.MiDialogDeleteAccount;

public class SettingsFragment extends PreferenceFragmentCompat {

    Preference logout;
    Preference delete;
    Preference changePassword;
    EditTextPreference changeUsername;
    EditTextPreference changeEmail;
    SharedPreferences preferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getPreferenceScreen().getSharedPreferences();
        logout = findPreference(getString(R.string.key_logout));
        delete = findPreference(getString(R.string.key_delete));
        changePassword = findPreference(getString(R.string.key_password));
        changeUsername = findPreference(getString(R.string.key_username));
        changeEmail = findPreference(getString(R.string.key_email));
        enablePreferences(Constantes.login);

        logout.setOnPreferenceClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            Toast.makeText(getContext(), "Has cerrado sesión " + Constantes.loggedUser, Toast.LENGTH_SHORT).show();
            Constantes.loggedUser = "Not logged in";
            Constantes.login = false;
            editor.putString(getString(R.string.key_username), Constantes.loggedUser);
            editor.putBoolean("login", Constantes.login);
            editor.putString(getString(R.string.key_email), "Not logged in").apply();
            enablePreferences(Constantes.login);
            return true;
        });

        delete.setOnPreferenceClickListener(v -> {
            DialogFragment dialog = new MiDialogDeleteAccount();
            dialog.show(getActivity().getSupportFragmentManager(), "dialog-delete");
            return true;
        });

        changeUsername.setOnPreferenceChangeListener((preference, newValue) -> {
            editUsername((String) newValue);
            return true;
        });
        changePassword.setOnPreferenceClickListener(v -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("change-password", true);
            bundle.putString("username", Constantes.loggedUser);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        });
        changeEmail.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS));
        changeEmail.setOnPreferenceChangeListener((preference, newValue) -> {
            editEmail((String) newValue);
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
            SharedPreferences.Editor editor = preferences.edit();
            Constantes.loggedUser = "Not logged in";
            Constantes.login = false;
            editor.putString(getString(R.string.key_username), Constantes.loggedUser);
            editor.putBoolean("login", Constantes.login);
            editor.putString(getString(R.string.key_email), "Not logged in").apply();
            enablePreferences(Constantes.login);
        } else {
            Toast.makeText(getContext(), "No se ha borrado el usuario", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private void editEmail(String newEmail) {
        SQLiteDatabase db = openDB();
        ContentValues registro = new ContentValues();
        registro.put("email", newEmail);
        SharedPreferences.Editor editor = preferences.edit();
        int cant = db.update("usuarios", registro, "username='" + Constantes.loggedUser + "'", null);
        if (cant > 0) {
            Toast.makeText(getContext(), "Se ha actualizado el email a " + newEmail, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No se ha podido actualizar el email", Toast.LENGTH_SHORT).show();
        }
        editor.putString(getString(R.string.key_email), newEmail).apply();
        db.close();
    }

    private void editUsername(String newUsername) {
        SQLiteDatabase db = openDB();
        ContentValues registro = new ContentValues();
        registro.put("username", newUsername);
        SharedPreferences.Editor editor = preferences.edit();
        int cant = db.update("usuarios", registro, "username='" + Constantes.loggedUser + "'", null);
        if (cant > 0) {
            Constantes.loggedUser = newUsername;
            Toast.makeText(getContext(), "Se ha actualizado el username a " + newUsername, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No se ha podido actualizar el username", Toast.LENGTH_SHORT).show();
        }
        editor.putString(getString(R.string.key_username), newUsername).apply();
        db.close();
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(getContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }

    private boolean logout() {

        return true;
    }

    private void enablePreferences(boolean value) {
        changeUsername.setEnabled(value);
        changePassword.setEnabled(value);
        changeEmail.setEnabled(value);
        logout.setEnabled(value);
        delete.setEnabled(value);
    }

    public void preferencesClick() {
        deleteAccount();
    }

}