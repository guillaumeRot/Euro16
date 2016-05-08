package com.euro16.Utils.Enums;

/**
 * Créé par Guillaume le 06/04/2016.
 */
public enum EUtilisateurStatut {

    PARTICIPE(1, "Participe"),
    DEMANDE_PARTICIPE(2, "A demandé à participer"),
    EST_INVITE(3, "A été invité à participer");

    private final int statut;
    private final String descStatut;

    EUtilisateurStatut(int statut, String descStatut) {
        this.statut = statut;
        this.descStatut = descStatut;
    }

    public int getStatut() {
        return this.statut;
    }

    public String getDescStatut() {
        return this.descStatut;
    }
}
