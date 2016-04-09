package com.euro16.Utils;

/**
 * Créé par Guillaume le 02/04/2016.
 */
public enum EMonde {

    GLOBAL(0, "Global"),
    COMMUNAUTE(1, "Communauté"),
    GROUPE(2, "Groupe");

    private final int typeMonde;
    private final String nomMonde;

    EMonde(int typeMonde, String nomMonde) {
        this.typeMonde = typeMonde;
        this.nomMonde = nomMonde;
    }

    public int getTypeMonde() {
        return this.typeMonde;
    }

    public String getNomMonde() {
        return this.nomMonde;
    }
}
