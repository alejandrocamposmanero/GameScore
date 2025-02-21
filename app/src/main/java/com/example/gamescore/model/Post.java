package com.example.gamescore.model;

public class Post {
    private int idPost;
    private int idUser;
    private int idJuego;
    private String postMessage;
    private int rating;

    public Post(int idPost, String postMessage, int rating, int idUser, int idJuego) {
        this.idPost = idPost;
        this.idUser = idUser;
        this.idJuego = idJuego;
        this.postMessage = postMessage;
        this.rating = rating;
    }

    public int getIdPost() {
        return idPost;
    }

    public int getIdUser() {
        return idUser;
    }

    public int getIdJuego() {
        return idJuego;
    }

    public String getPostMessage() {
        return postMessage;
    }

    public int getRating() {
        return rating;
    }
}
