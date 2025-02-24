package com.example.gamescore.data.model;

import android.graphics.drawable.Drawable;

public class Game {
    private int id;
    private String name;
    private double rating;
    private String sinopsis;
    private Drawable imagen;

    public Game() {
    }

    public Game(int id) {
        this.id = id;
    }

    public Game(int id, String name, String sinopsis, Drawable imagen, double rating) {
        this.id = id;
        this.name = name;
        this.sinopsis = sinopsis;
        this.imagen = imagen;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public double getRating() {
        return rating;
    }

    public Drawable getImagen() {
        return imagen;
    }
}
