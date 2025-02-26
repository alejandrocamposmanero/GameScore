package com.example.gamescore.fragment.main;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.example.gamescore.activity.MainActivity;
import com.example.gamescore.data.Constantes;
import com.example.gamescore.data.MiAdminSQLite;
import com.example.gamescore.dialog.MiDialogDeleteAccount;

import java.util.Objects;

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
        MainActivity.isHomeFragment = false;
        MainActivity.isProfileFragment = false;
        MainActivity.isMyGamesFragment = false;
        preferences = getPreferenceScreen().getSharedPreferences();
        logout = findPreference(getString(R.string.key_logout));
        delete = findPreference(getString(R.string.key_delete));
        changePassword = findPreference(getString(R.string.key_password));
        changeUsername = findPreference(getString(R.string.key_username));
        changeEmail = findPreference(getString(R.string.key_email));
        enablePreferences(Constantes.login);

        logout.setOnPreferenceClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            Toast.makeText(requireContext(), "You've logged out " + Constantes.loggedUser, Toast.LENGTH_SHORT).show();
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
            dialog.show(requireActivity().getSupportFragmentManager(), "dialog-delete");
            return true;
        });

        changeUsername.setOnPreferenceChangeListener((preference, newValue) -> editUsername((String) newValue));
        changePassword.setOnPreferenceClickListener(v -> {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("change-password", true);
            bundle.putString("username", Constantes.loggedUser);
            intent.putExtras(bundle);
            startActivity(intent);
            return true;
        });
        changeEmail.setOnBindEditTextListener(editText -> editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS));
        changeEmail.setOnPreferenceChangeListener((preference, newValue) -> editEmail((String) newValue));
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }

    private void deleteAccount() {
        SQLiteDatabase db = openDB();
        int idUser = -1;
        Cursor fila = db.rawQuery("SELECT id_user FROM usuarios WHERE username='" + Constantes.loggedUser + "'", null);
        if (fila.moveToFirst())
            idUser = fila.getInt(0);
        fila.close();
        int cant = db.delete("posts", "id_user=" + idUser, null);
        cant += db.delete("usuarios", "username='" + Constantes.loggedUser + "'", null);
        if (cant > 0) {
            Toast.makeText(requireContext(), "User correctly deleted " + Constantes.loggedUser, Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = preferences.edit();
            Constantes.loggedUser = "Not logged in";
            Constantes.login = false;
            editor.putString(getString(R.string.key_username), Constantes.loggedUser);
            editor.putBoolean("login", Constantes.login);
            editor.putString(getString(R.string.key_email), "Not logged in").apply();
            enablePreferences(Constantes.login);
        } else {
            Toast.makeText(requireContext(), "The user hasn't been deleted", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private boolean editEmail(String newEmail) {
        SharedPreferences.Editor editor = preferences.edit();
        if (checkEmail(newEmail) || newEmail.isEmpty()) {
            Toast.makeText(requireContext(), "Not valid email, choose another one", Toast.LENGTH_SHORT).show();
            String email = Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).getString(getString(R.string.key_email), "Not logged in");
            editor.putString(getString(R.string.key_email), email).apply();
            return false;
        }
        SQLiteDatabase db = openDB();
        ContentValues registro = new ContentValues();
        registro.put("email", newEmail);
        int cant = db.update("usuarios", registro, "username='" + Constantes.loggedUser + "'", null);
        if (cant > 0) {
            Toast.makeText(requireContext(), "Email updated to " + newEmail, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Couldn't update the email", Toast.LENGTH_SHORT).show();
        }
        editor.putString(getString(R.string.key_email), newEmail).apply();
        db.close();
        return true;
    }

    private boolean editUsername(String newUsername) {
        SharedPreferences.Editor editor = preferences.edit();
        if (checkUsername(newUsername) || newUsername.contains(" ") || newUsername.isEmpty()) {
            Toast.makeText(requireContext(), "Username not valid, choose another one", Toast.LENGTH_SHORT).show();
            editor.putString(getString(R.string.key_username), Constantes.loggedUser);
            return false;
        }
        SQLiteDatabase db = openDB();
        ContentValues registro = new ContentValues();
        registro.put("username", newUsername);
        int cant = db.update("usuarios", registro, "username='" + Constantes.loggedUser + "'", null);
        if (cant > 0) {
            Constantes.loggedUser = newUsername;
            Toast.makeText(requireContext(), "Username updated to " + newUsername, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Couldn't update the username", Toast.LENGTH_SHORT).show();
        }
        editor.putString(getString(R.string.key_username), newUsername).apply();
        db.close();
        return true;
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(requireContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
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