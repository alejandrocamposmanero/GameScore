package com.example.gamescore.model;

import android.graphics.drawable.Drawable;

public class Videogame {
    private int id;
    private String name;
    private double rating;
    private Drawable imagen;
    
    public Videogame(int id, String name, Drawable imagen, double rating) {
        this.id = id;
        this.name = name;
        this.imagen = imagen;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public Drawable getImagen() {
        return imagen;
    }
}
