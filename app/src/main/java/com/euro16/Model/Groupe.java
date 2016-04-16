package com.euro16.Model;

/**
 * Created by Guillaume on 09/04/2016.
 */
public class Groupe {

    private String nom;
    private String admin;
    private String photo;

    public Groupe(String nom, String admin, String photo) {
        this.nom = nom;
        this.admin = admin;
        this.photo = photo;
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

    @Override
    public String toString() {
        return "Groupe{" +
                "nom = '" + nom + '\'' +
                ", admin = '" + admin + '\'' +
                ", photo = '" + photo + '\'' +
                '}';
    }
}
