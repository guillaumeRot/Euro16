package com.euro16.Model;

/**
 * Created by Guillaume on 02/05/2016.
 */
public class Equipe {

    private String nom;
    private int pts;
    private int joues;
    private int gagnes;
    private int nuls;
    private int perdus;
    private int goalAverage;

    public Equipe(String nom, int pts, int joues, int gagnes, int nuls, int perdus, int goalAverage) {
        this.nom = nom;
        this.pts = pts;
        this.joues = joues;
        this.gagnes = gagnes;
        this.nuls = nuls;
        this.perdus = perdus;
        this.goalAverage = goalAverage;
    }

    public String getNom() {
        return nom;
    }

    public int getPts() {
        return pts;
    }

    public int getJoues() {
        return joues;
    }

    public int getGagnes() {
        return gagnes;
    }

    public int getNuls() {
        return nuls;
    }

    public int getPerdus() {
        return perdus;
    }

    public int getGoalAverage() {
        return goalAverage;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPts(int pts) {
        this.pts = pts;
    }

    public void setJoues(int joues) {
        this.joues = joues;
    }

    public void setGagnes(int gagnes) {
        this.gagnes = gagnes;
    }

    public void setNuls(int nuls) {
        this.nuls = nuls;
    }

    public void setPerdus(int perdus) {
        this.perdus = perdus;
    }

    public void setGoalAverage(int goalAverage) {
        this.goalAverage = goalAverage;
    }

    public int compareTo(Equipe equipe) {
        int diffPts = equipe.getPts() - pts;
        if(diffPts == 0) {
            int diffGoalAverage = equipe.getGoalAverage() - goalAverage;
            if(diffGoalAverage == 0) {
                int diffJoues = joues - equipe.getJoues();
                if(diffJoues == 0) {
                    return nom.compareTo(equipe.getNom());
                }
                return diffJoues;
            }
            return diffGoalAverage;
        }
        return diffPts;
    }

    @Override
    public String toString() {
        return "Equipe{" +
                "nom='" + nom + '\'' +
                ", pts=" + pts +
                ", joues=" + joues +
                ", gagnes=" + gagnes +
                ", nuls=" + nuls +
                ", perdus=" + perdus +
                ", goalAverage=" + goalAverage +
                '}';
    }
}
