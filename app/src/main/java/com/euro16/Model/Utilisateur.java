package com.euro16.Model;

/**
 * Created by Guillaume on 09/04/2016.
 */
public class Utilisateur {

    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String photo;

    public Utilisateur(String id, String nom, String prenom, String email, String photo) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }
}
