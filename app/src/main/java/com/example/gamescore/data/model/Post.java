package com.example.gamescore.data.model;

public class Post {
    private int idPost;
    private int idUser;
    private int idJuego;
    private String postMessage;
    private double rating;
    private Tag tag;

    public enum Tag {
        UNDEFINED,
        TO_PLAY,
        PLAYING,
        PLAYED
    }

    public Post() {

    }

    public Post(int idPost) {
        this.idPost = idPost;
    }

    public Post(int idPost, String postMessage, double rating, Tag tag, int idUser, int idJuego) {
        this.idPost = idPost;
        this.idUser = idUser;
        this.idJuego = idJuego;
        this.postMessage = postMessage;
        this.rating = rating;
        this.tag = tag;
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

    public double getRating() {
        return rating;
    }

    public Tag getTag() {
        return tag;
    }
}
