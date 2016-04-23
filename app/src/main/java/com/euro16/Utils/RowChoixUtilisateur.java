package com.euro16.Utils;

/**
 * Created by Guillaume on 16/04/2016.
 */
public class RowChoixUtilisateur {
    private String photo;
    private String nom;
    private String prenom;

    public RowChoixUtilisateur(String nom, String prenom, String photo) {
        this.photo = photo;
        this.nom = nom;
        this.prenom = prenom;
    }
    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    @Override
    public String toString() {
        return "Nom : " + nom + "\nImage : " + photo;
    }
}