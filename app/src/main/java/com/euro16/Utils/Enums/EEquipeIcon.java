package com.euro16.Utils.Enums;

/**
 * Created by Guillaume on 05/05/2016.
 */
public enum EEquipeIcon {

    SUISSE("Suisse", "suisse"),
    ALBANIE("Albanie", "albanie"),
    FRANCE("France", "france"),
    ROUMANIE("Roumanie", "roumanie"),
    GALLES("Galles", "galles"),
    RUSSIE("Russie", "russie"),
    ANGLETERRE("Angleterre", "angleterre"),
    SLOVAQUIE("Slovaquie", "slovaquie"),
    ALLEMAGNE("Allemagne", "allemagne"),
    IRLANDE_DU_NORD("Irlande du Nord", "irlande_du_nord"),
    UKRAINE("Ukraine", "ukraine"),
    POLOGNE("Pologne", "pologne"),
    TURQUIE("Turquie", "turquie"),
    ESPAGNE("Espagne", "espagne"),
    CROATIE("Croatie", "croatie"),
    REP_TCHEQUE("Rep. Tcheque", "republique_tcheque"),
    ITALIE("Italie", "italie"),
    SUEDE("Suede", "suede"),
    IRLANDE("Irlande", "irlande"),
    BELGIQUE("Belgique", "belgique"),
    ISLANDE("Islande", "islande"),
    AUTRICHE("Autriche", "autriche"),
    PORTUGAL("Portugal", "portugal"),
    HONGRIE("Hongrie", "hongrie");


    private String nomEquipe;
    private String nomIcon;

    EEquipeIcon(String nomEquipe, String nomIcon) {
        this.nomEquipe = nomEquipe;
        this.nomIcon = nomIcon;
    }

    public String getNomEquipe() {
        return nomEquipe;
    }

    public String getNomIcon() {
        return nomIcon;
    }

    public static String getNomIcon(String nomEquipe) {
        if (nomEquipe != null) {
            for (EEquipeIcon equipeIcon : EEquipeIcon.values()) {
                if (equipeIcon.getNomEquipe().equalsIgnoreCase(nomEquipe)) {
                    return equipeIcon.getNomIcon();
                }
            }
        }
        return null;
    }
}
