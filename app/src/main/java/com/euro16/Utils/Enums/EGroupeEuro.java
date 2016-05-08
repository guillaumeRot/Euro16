package com.euro16.Utils.Enums;

/**
 * Created by Guillaume on 02/05/2016.
 */
public enum EGroupeEuro {

    GROUPE_A("A", "Groupe A", true),
    GROUPE_B("B", "Groupe B", true),
    GROUPE_C("C", "Groupe C", true),
    GROUPE_D("D", "Groupe D", true),
    GROUPE_E("E", "Groupe E", true),
    GROUPE_F("F", "Groupe F", true),
    HUITIEMES("HF", "Huitièmes de finale", false),
    QUARTS("QF", "Quarts de finale", false),
    DEMIES("DF", "Demi-finales", false),
    FINALE("FI", "Finale", false);

    private final String grpBase;
    private final String nomGrp;
    private final boolean hasClassement;

    EGroupeEuro(String grpBase, String nomGrp, boolean hasClassement) {
        this.grpBase = grpBase;
        this.nomGrp = nomGrp;
        this.hasClassement = hasClassement;
    }

    public String getGrpBase() {
        return grpBase;
    }

    public String getNomGrp() {
        return nomGrp;
    }

    public boolean hasClassement() {
        return hasClassement;
    }

    public static EGroupeEuro getGroupeEuro(String groupe) {
        if (groupe != null) {
            for (EGroupeEuro groupeEuro : EGroupeEuro.values()) {
                if (groupe.equalsIgnoreCase(groupeEuro.getGrpBase()) || groupe.equalsIgnoreCase(groupeEuro.getNomGrp())) {
                    return groupeEuro;
                }
            }
        }
        return null;
    }
}
