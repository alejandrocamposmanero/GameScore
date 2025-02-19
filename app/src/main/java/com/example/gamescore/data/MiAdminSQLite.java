package com.example.gamescore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MiAdminSQLite extends SQLiteOpenHelper {

    private static MiAdminSQLite sInstance;

    public static MiAdminSQLite getInstance(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        if (sInstance == null)
            sInstance = new MiAdminSQLite(context, name, factory, version);
        return sInstance;
    }

    public MiAdminSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE juegos " +
                "(_id_juego INTEGER primary key AUTOINCREMENT, " +
                "nombre TEXT, " +
                "imagen BLOB, " +
                "nota_media REAL, " +
                "sinopsis TEXT)");
        db.execSQL("CREATE TABLE usuarios " +
                "(username TEXT primary key, " +
                "display_name TEXT,  " +
                "email TEXT, " +
                "profile_pic BLOB)");
        db.execSQL("CREATE TABLE resenas " +
                "(_id_resena INTEGER primary key AUTOINCREMENT, " +
                "resena TEXT, " +
                "user TEXT, " +
                "id_juego INTEGER, " +
                "foreign key (id_juego) REFERENCES juegos(_id_juego), " +
                "foreign key (user) REFERENCES usuarios (username))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
