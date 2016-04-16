package com.euro16.Model;

/**
 * Created by Guillaume on 09/04/2016.
 */
public class Communaute {

    private String nom;
    private String admin;
    private String photo;
    private String type;

    public Communaute(String nom, String admin, String photo, String type) {
        this.nom = nom;
        this.admin = admin;
        this.photo = photo;
        this.type = type;
    }

    public String getNom() {
        return nom;
    }

    public String getAdmin() {
        return admin;
    }

    public String getPhoto() {
        return photo;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Communaute{" +
                "nom = '" + nom + '\'' +
                ", admin = '" + admin + '\'' +
                ", photo = '" + photo + '\'' +
                ", type = '" + type + '\'' +
                '}';
    }
}
