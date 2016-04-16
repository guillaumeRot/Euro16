package com.euro16.Utils;

/**
 * Created by Guillaume on 16/04/2016.
 */
public class RowChoixCommunaute {
    private int statutUtilImage;
    private String nom;

    public RowChoixCommunaute(String nom, int statutUtilImage) {
        this.statutUtilImage = statutUtilImage;
        this.nom = nom;
    }
    public int getStatutUtilImage() {
        return statutUtilImage;
    }
    public void setStatutUtilImage(int statutUtilImage) {
        this.statutUtilImage = statutUtilImage;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    @Override
    public String toString() {
        return "Nom : " + nom + "\nImage : " + statutUtilImage;
    }
}