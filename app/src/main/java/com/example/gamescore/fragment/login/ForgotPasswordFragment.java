package com.example.gamescore.fragment.login;

import android.content.ContentValues;
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

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

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
        OnBackPressedDispatcher onBack = requireActivity().getOnBackPressedDispatcher();
        onBack.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getArguments() != null) {
                    Bundle bundle = getArguments();
                    boolean changePassword = bundle.getBoolean("change-password", false);
                    if (changePassword)
                        requireActivity().finish();
                    else
                        Navigation.findNavController(requireActivity(), R.id.nav_host_login).navigate(R.id.loginFragment);
                }
            }
        });
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
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    Constantes.loggedUser = getUsername();
                    Constantes.login = true;
                    editor.putString(getString(R.string.key_username), Constantes.loggedUser);
                    editor.putBoolean("login", Constantes.login).apply();
                    requireActivity().finish();
                }
            } else {
                if (newPassword.isEmpty()) {
                    changePassword.requestFocus();
                } else {
                    changePasswordConfirm.requestFocus();
                }
                Toast.makeText(requireContext(), "You can't leave an empty field", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private String getDisplayName() {
        String displayName = "";
        SQLiteDatabase db = openDB();
        Bundle bundle = getArguments();
        boolean useEmail;
        if (bundle != null) {
            useEmail = bundle.getBoolean("use-email", false);
            Cursor fila;
            if (useEmail) {
                fila = db.rawQuery("SELECT display_name FROM usuarios WHERE email='" + bundle.getString("email") + "'", null);
            } else {
                fila = db.rawQuery("SELECT display_name FROM usuarios WHERE username='" + bundle.getString("username", Constantes.loggedUser) + "'", null);
            }

            if (fila.moveToFirst())
                displayName = fila.getString(0);
            fila.close();
        }
        db.close();
        return displayName;
    }

    private String getUsername() {
        String username = "";
        Bundle bundle = getArguments();
        boolean useEmail;
        if (bundle != null) {
            useEmail = bundle.getBoolean("use-email", false);
            if (useEmail) {
                SQLiteDatabase db = openDB();
                Cursor fila = db.rawQuery("SELECT username FROM usuarios WHERE email='" + bundle.getString("email") + "'", null);
                if (fila.moveToFirst())
                    username = fila.getString(0);
                fila.close();
                db.close();
            } else {
                username = bundle.getString("username");
            }
        }
        return username;
    }

    private void updatePassword(String newPassword) {
        SQLiteDatabase db = openDB();
        ContentValues password = new ContentValues();
        password.put("password", newPassword);
        Bundle bundle = getArguments();
        boolean useEmail;
        String username;
        int cant = 0;
        if (bundle != null) {
            useEmail = bundle.getBoolean("use-email", false);
            if (useEmail) {
                username = bundle.getString("email");
                cant = db.update("usuarios", password, "email='" + username + "'", null);
            } else {
                username = bundle.getString("username");
                cant = db.update("usuarios", password, "username='" + username + "'", null);
            }
        }
        db.close();
        if (cant > 0) {
            Toast.makeText(requireContext(), "Password updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Couldn't update the password", Toast.LENGTH_SHORT).show();
        }
    }

    private SQLiteDatabase openDB() {
        MiAdminSQLite admin = MiAdminSQLite.getInstance(requireContext(), Constantes.NOMBRE_DB, null, Constantes.VERSION_DB);
        return admin.getWritableDatabase();
    }
}