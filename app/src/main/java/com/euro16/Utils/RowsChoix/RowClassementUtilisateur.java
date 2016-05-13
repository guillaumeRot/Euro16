package com.euro16.Utils.RowsChoix;

/**
 * Created by Guillaume on 12/05/2016.
 */
public class RowClassementUtilisateur {

    private String photo;
    private String nom;
    private String prenom;
    private String pts;

    public RowClassementUtilisateur(String nom, String prenom, String photo, String pts) {
        this.photo = photo;
        this.nom = nom;
        this.prenom = prenom;
        this.pts = pts;
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
    public String getPts() {
        return pts;
    }

    public void setPts(String pts) {
        this.pts = pts;
    }
    @Override
    public String toString() {
        return "Nom : " + nom + "\nImage : " + photo;
    }
}
