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
                "(id_juego INTEGER primary key AUTOINCREMENT, " +
                "nombre TEXT, " +
                "imagen BLOB, " +
                "nota_media REAL, " +
                "sinopsis TEXT)");
        db.execSQL("CREATE TABLE usuarios " +
                "(id_user INTEGER primary key, " +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "email TEXT," +
                "display_name TEXT," +
                "profile_pic BLOB)");
        db.execSQL("CREATE TABLE posts " +
                "(id_post INTEGER primary key AUTOINCREMENT," +
                "rating REAL," +
                "resena TEXT," +
                "tag TEXT," +
                "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "id_user INTEGER," +
                "id_juego INTEGER," +
                "UNIQUE (id_post, id_juego)," +
                "foreign key (id_juego) REFERENCES juegos(id_juego)," +
                "foreign key (id_user) REFERENCES usuarios (id_user))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS posts");
        db.execSQL("DROP TABLE IF EXISTS juegos");
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("CREATE TABLE posts " +
                "(id_post INTEGER primary key AUTOINCREMENT," +
                "rating REAL," +
                "resena TEXT," +
                "tag TEXT," +
                "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "id_user INTEGER," +
                "id_juego INTEGER," +
                "UNIQUE (id_post, id_juego)," +
                "foreign key (id_juego) REFERENCES juegos(_id_juego)," +
                "foreign key (id_user) REFERENCES usuarios (username))");
        db.execSQL("CREATE TABLE juegos " +
                "(id_juego INTEGER primary key AUTOINCREMENT, " +
                "nombre TEXT, " +
                "imagen BLOB, " +
                "nota_media REAL, " +
                "sinopsis TEXT)");
        db.execSQL("CREATE TABLE usuarios " +
                "(id_user INTEGER primary key, " +
                "username TEXT UNIQUE," +
                "password TEXT," +
                "email TEXT," +
                "display_name TEXT," +
                "profile_pic BLOB)");
    }
}
